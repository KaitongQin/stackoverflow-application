package org.cs209a.stackoverflow.crawler;

import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.cs209a.stackoverflow.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class DatabaseService {
    private static final int BATCH_SIZE = 1000;
    private final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private StanfordCoreNLPService stanfordCoreNLPService;
    private HikariDataSource dataSource;
    // 预编译SQL语句的缓存
    private PreparedStatement questionStmt;
    private PreparedStatement answerStmt;
    private PreparedStatement commentStmt;
    private PreparedStatement ownerStmt;
    private PreparedStatement tagQuestionStmt;
    private PreparedStatement timelineStmt;
    // 批处理计数器
    private int questionBatchCount = 0;
    private int answerBatchCount = 0;
    private int commentBatchCount = 0;
    private int timelineBatchCount = 0;

    public DatabaseService() {
        String host = "localhost";
        String user = "postgres", password = "123456", database = "stackoverflow";
        int port = 5432;
        setupConnectionPool(host, port, user, password, database);
        stanfordCoreNLPService = new StanfordCoreNLPService();
    }

    private static User getUser(JSONObject json) {
        JSONObject ownerJson = json.getJSONObject("owner");
        if (ownerJson == null) {
            User user = new User();
            user.setAccountId(-1);
            user.setUserId(-1);
            user.setReputation(-1);
            user.setUserType("does_not_exist");
            user.setLink("");
            user.setDisplayName("");
            user.setProfileImage("");
            return user;
        }
        User user = new User();
        user.setAccountId(ownerJson.getInteger("account_id") == null ? -1 : ownerJson.getInteger("account_id"));
        user.setUserId(ownerJson.getInteger("user_id") == null ? -1 : ownerJson.getInteger("user_id"));
        user.setProfileImage(ownerJson.getString("profile_image") == null ? "" : ownerJson.getString("profile_image"));
        user.setLink(ownerJson.getString("link") == null ? "" : ownerJson.getString("link"));
        user.setUserType(ownerJson.getString("user_type"));
        user.setDisplayName(ownerJson.getString("display_name") == null ? "" : ownerJson.getString("display_name"));
        user.setReputation(ownerJson.getInteger("reputation") == null ? -1 : ownerJson.getInteger("reputation"));
        return user;
    }

    public void saveToDatabase(List<JSONObject> questions, List<JSONObject> answers, List<JSONObject> comments, List<JSONObject> timeLine) {
        try {
            batchInsertQuestionRecord(questions);
            batchInsertAnswerRecord(answers);
            batchInsertCommentRecord(comments);
            batchInsertTimeLineRecord(timeLine);
            logger.info("Data successfully saved to database");
        } catch (SQLException e) {
            logger.error("Failed to save data to database", e);
            throw new RuntimeException("Database operation failed", e);
        }
    }

    private void batchInsertTimeLineRecord(List<JSONObject> timeLine) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            prepareStatements(conn);

            for (JSONObject record : timeLine) {
                User user = getUser(record);
                addTimelineBatch(record, user);
                addOwnerBatch(user);

                timelineBatchCount++;
                if (timelineBatchCount >= BATCH_SIZE) {
                    executeTimelineBatch(conn);
                }
            }

            // 执行剩余的批处理
            executeTimelineBatch(conn);
            conn.commit();
        } catch (SQLException e) {
            logger.error("Failed to insert timeline record", e);
            throw new RuntimeException("Failed to insert timeline record", e);
        }
    }

    private void executeTimelineBatch(Connection conn) {
        try {
            timelineStmt.executeBatch();
            ownerStmt.executeBatch();
            timelineBatchCount = 0;
            conn.commit();
        } catch (SQLException e) {
            logger.error("Failed to execute timeline batch", e);
            throw new RuntimeException("Failed to execute timeline batch", e);
        }
    }

    private void addTimelineBatch(JSONObject record, User user) throws SQLException {
        timelineStmt.setInt(1, record.getInteger("comment_id") == null ? -1 : record.getInteger("comment_id"));
        timelineStmt.setTimestamp(2, convertDate(record.getInteger("creation_date")));
        timelineStmt.setInt(3, record.getInteger("down_vote_count") == null ? -1 : record.getInteger("down_vote_count"));
        timelineStmt.setInt(4, user.getAccountId());
        timelineStmt.setInt(5, record.getInteger("post_id") == null ? -1 : record.getInteger("post_id"));
        timelineStmt.setInt(6, record.getInteger("question_id"));
        timelineStmt.setString(7, record.getString("revision_guid"));
        timelineStmt.setString(8, record.getString("timeline_type"));
        timelineStmt.setInt(9, record.getInteger("up_vote_count") == null ? -1 : record.getInteger("up_vote_count"));
        timelineStmt.setInt(10, user.getAccountId());
        timelineStmt.addBatch();
    }

    private Timestamp convertDate(Integer date) {
        return date == null ? null : new Timestamp(date * 1000L);
    }

    private void setupConnectionPool(String host, int port, String user, String password, String database) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        config.setUsername(user);
        config.setPassword(password);

        // 连接池配置
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    private void prepareStatements(Connection conn) throws SQLException {
        questionStmt = conn.prepareStatement(
                "INSERT INTO question VALUES (?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING"
        );
        answerStmt = conn.prepareStatement(
                "INSERT INTO answer VALUES (?,?,?,?,?,?,?) ON CONFLICT DO NOTHING"
        );
        commentStmt = conn.prepareStatement(
                "INSERT INTO comment VALUES (?,?,?,?,?,?,?) ON CONFLICT DO NOTHING"
        );
        ownerStmt = conn.prepareStatement(
                "INSERT INTO \"user\" VALUES (?,?,?,?,?,?,?) ON CONFLICT DO NOTHING"
        );
        tagQuestionStmt = conn.prepareStatement(
                "INSERT INTO question_tag VALUES (?,?) ON CONFLICT DO NOTHING"
        );
        timelineStmt = conn.prepareStatement(
                "INSERT INTO timeline VALUES (?,?,?,?,?,?,?,?,?,?) ON CONFLICT DO NOTHING"
        );
    }

    public void batchInsertQuestionRecord(List<JSONObject> questions) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            prepareStatements(conn);

            for (JSONObject question : questions) {
                // 插入问题相关数据
                User owner = getUser(question);
                addQuestionBatch(question, owner);
                addOwnerBatch(owner);


                // 处理标签
                int questionId = question.getInteger("question_id");
                String[] tags = question.getJSONArray("tags").toArray(String[]::new);
                for (String tag : tags) {
                    addTagQuestionBatch(tag, questionId);
                }

                questionBatchCount++;
                if (questionBatchCount >= BATCH_SIZE) {
                    executeBatch(conn);
                }
            }

            // 执行剩余的批处理
            executeBatch(conn);
            conn.commit();
        }
    }

    private void addQuestionBatch(JSONObject question, User owner) throws SQLException {
        questionStmt.setInt(1, question.getInteger("question_id"));
        questionStmt.setString(2, question.getString("title"));
        questionStmt.setString(3, question.getString("body"));
        questionStmt.setInt(4, question.getInteger("score"));
        questionStmt.setInt(5, question.getInteger("view_count"));
        questionStmt.setInt(6, question.getInteger("answer_count"));
        questionStmt.setInt(7, owner.getAccountId());
        questionStmt.setTimestamp(8, convertDate(question.getInteger("creation_date")));
        questionStmt.addBatch();
    }

    private void executeBatch(Connection conn) throws SQLException {
        questionStmt.executeBatch();
        ownerStmt.executeBatch();
        tagQuestionStmt.executeBatch();
        questionBatchCount = 0;
        conn.commit();
    }

    private void addOwnerBatch(User owner) throws SQLException {
        ownerStmt.setInt(1, owner.getUserId());
        ownerStmt.setInt(2, owner.getAccountId());
        ownerStmt.setString(3, owner.getAccountId() == -1 ? "does_not_exist" : owner.getDisplayName());
        ownerStmt.setInt(4, owner.getReputation());
        ownerStmt.setString(5, owner.getUserType());
        ownerStmt.setString(6, owner.getLink());
        ownerStmt.setString(7, owner.getProfileImage());
        ownerStmt.addBatch();
    }


    private void addTagQuestionBatch(String tagName, int questionId) throws SQLException {
        tagQuestionStmt.setString(2, tagName);
        tagQuestionStmt.setInt(1, questionId);
        tagQuestionStmt.addBatch();
    }


    public void batchInsertAnswerRecord(List<JSONObject> answers) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            prepareStatements(conn);

            for (JSONObject answer : answers) {
                User owner = getUser(answer);
                addAnswerBatch(answer, owner);
                addOwnerBatch(owner);

                answerBatchCount++;
                if (answerBatchCount >= BATCH_SIZE) {
                    executeAnswerBatch(conn);
                }
            }

            // 执行剩余的批处理
            executeAnswerBatch(conn);
            conn.commit();
        }
    }

    private void addAnswerBatch(JSONObject answer, User owner) throws SQLException {
//        private Integer id;
//        private Integer questionId;
//        private String body;
//        private Integer score;
//        private Boolean isAccepted;
//        private Integer ownerId;
//        private Timestamp creationDate;
//        CREATE TABLE IF NOT EXISTS answer
//(
//    id            INT PRIMARY KEY,    -- 答案ID
//    question_id   INT       NOT NULL, -- 问题ID
//    body          TEXT      NOT NULL, -- 内容
//    score         INT       NOT NULL, -- 得分
//    is_accepted   BOOLEAN   NOT NULL, -- 是否被采纳
//    owner_id      INT       NOT NULL, -- 回答者ID
//    creation_date TIMESTAMP NOT NULL  -- 创建时间          -- 内容许可证
//);
        answerStmt.setInt(1, answer.getInteger("answer_id"));
        answerStmt.setInt(2, answer.getInteger("question_id"));
        answerStmt.setString(3, answer.getString("body"));
        answerStmt.setInt(4, answer.getInteger("score"));
        answerStmt.setBoolean(5, answer.getBoolean("is_accepted"));
        answerStmt.setInt(6, owner.getAccountId());
        answerStmt.setTimestamp(7, convertDate(answer.getInteger("creation_date")));
        answerStmt.addBatch();
    }

    private void executeAnswerBatch(Connection conn) throws SQLException {
        answerStmt.executeBatch();
        ownerStmt.executeBatch();
        answerBatchCount = 0;
        conn.commit();
    }

    public void batchInsertCommentRecord(List<JSONObject> comments) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            prepareStatements(conn);

            for (JSONObject comment : comments) {
                User owner = getUser(comment);
                addCommentBatch(comment, owner);
                addOwnerBatch(owner);

                commentBatchCount++;
                if (commentBatchCount >= BATCH_SIZE) {
                    executeCommentBatch(conn);
                }
            }

            // 执行剩余的批处理
            executeCommentBatch(conn);
            conn.commit();
        }
    }

    private void addCommentBatch(JSONObject comment, User owner) throws SQLException {
        //private Integer id;
        //    private Integer postId;
        //    private String body;
        //    private Integer score;
        //    private Integer ownerId;
        //    private Timestamp creationDate;
        //    private Boolean edited;
        //CREATE TABLE IF NOT EXISTS comment
        //(
        //    id            INT PRIMARY KEY,    -- 评论ID
        //    post_id       INT       NOT NULL, -- 相关帖子ID（问题或回答）
        //    body          TEXT      NOT NULL, -- 内容
        //    score         INT       NOT NULL, -- 得分
        //    owner_id      INT       NOT NULL, -- 评论者ID
        //    creation_date TIMESTAMP NOT NULL, -- 创建时间
        //    edited        BOOLEAN   NOT NULL  -- 是否被编辑
        //);
        commentStmt.setInt(1, comment.getInteger("comment_id"));
        commentStmt.setInt(2, comment.getInteger("post_id"));
        commentStmt.setString(3, comment.getString("body"));
        commentStmt.setInt(4, comment.getInteger("score"));
        commentStmt.setInt(5, owner.getAccountId());
        commentStmt.setTimestamp(6, convertDate(comment.getInteger("creation_date")));
        commentStmt.setBoolean(7, comment.getBoolean("edited"));
        commentStmt.addBatch();
    }

    private void executeCommentBatch(Connection conn) throws SQLException {
        commentStmt.executeBatch();
        ownerStmt.executeBatch();
        commentBatchCount = 0;
        conn.commit();
    }

}

