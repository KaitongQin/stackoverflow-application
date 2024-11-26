package org.example.stackoverflow.data;

import java.sql.*;
import java.util.logging.*;

public class DatabaseService {
    private static final Logger logger = Logger.getLogger(DatabaseService.class.getName());

    // PostgreSQL连接URL
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/stackoverflow_data";  // PostgreSQL 数据库 URL
    private static final String DB_USER = "postgres";  // 数据库用户名
    private static final String DB_PASSWORD = "password";  // 数据库密码

    private Connection connection;

    public DatabaseService() {
        try {
            // 加载数据库驱动
            Class.forName("org.postgresql.Driver");
            // 连接到数据库
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            logger.info("Database connection established successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            logger.severe("Database connection failed: " + e.getMessage());
        }
    }

    // 创建所有相关的表
    public void createTables() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "user_id SERIAL PRIMARY KEY, "  // SERIAL 自动生成主键
                + "reputation INT, "
                + "display_name VARCHAR(255), "
                + "creation_date TIMESTAMP, "
                + "last_access_date TIMESTAMP)";

        String createQuestionsTable = "CREATE TABLE IF NOT EXISTS Questions ("
                + "question_id SERIAL PRIMARY KEY, "  // SERIAL 自动生成主键
                + "user_id INT, "
                + "title VARCHAR(255), "
                + "body TEXT, "
                + "creation_date TIMESTAMP, "
                + "tags VARCHAR(255), "
                + "answer_count INT, "
                + "comment_count INT, "
                + "view_count INT, "
                + "FOREIGN KEY (user_id) REFERENCES Users(user_id))";

        String createAnswersTable = "CREATE TABLE IF NOT EXISTS Answers ("
                + "answer_id SERIAL PRIMARY KEY, "  // SERIAL 自动生成主键
                + "question_id INT, "
                + "user_id INT, "
                + "body TEXT, "
                + "creation_date TIMESTAMP, "
                + "score INT, "
                + "is_accepted BOOLEAN, "
                + "FOREIGN KEY (question_id) REFERENCES Questions(question_id), "
                + "FOREIGN KEY (user_id) REFERENCES Users(user_id))";

        String createCommentsTable = "CREATE TABLE IF NOT EXISTS Comments ("
                + "comment_id SERIAL PRIMARY KEY, "  // SERIAL 自动生成主键
                + "post_id INT, "
                + "user_id INT, "
                + "body TEXT, "
                + "creation_date TIMESTAMP, "
                + "post_type VARCHAR(10), "  // 'question' 或 'answer'
                + "FOREIGN KEY (user_id) REFERENCES Users(user_id))";

        String createErrorTable = "CREATE TABLE IF NOT EXISTS Error_Exceptions ("
                + "error_id SERIAL PRIMARY KEY, "  // SERIAL 自动生成主键
                + "question_id INT, "
                + "error_type VARCHAR(255), "
                + "error_message TEXT, "
                + "error_count INT, "
                + "creation_date TIMESTAMP, "
                + "FOREIGN KEY (question_id) REFERENCES Questions(question_id))";

        String createJavaTopicsTable = "CREATE TABLE IF NOT EXISTS Java_Topics ("
                + "topic_id SERIAL PRIMARY KEY, "  // SERIAL 自动生成主键
                + "topic_name VARCHAR(255), "
                + "question_count INT, "
                + "description TEXT)";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createQuestionsTable);
            stmt.executeUpdate(createAnswersTable);
            stmt.executeUpdate(createCommentsTable);
            stmt.executeUpdate(createErrorTable);
            stmt.executeUpdate(createJavaTopicsTable);
            logger.info("Tables created successfully.");
        } catch (SQLException e) {
            logger.severe("Error while creating tables: " + e.getMessage());
        }
    }

    // 插入数据到 Users 表
    public void insertUser(int userId, int reputation, String displayName, Timestamp creationDate, Timestamp lastAccessDate) {
        String insertUserSQL = "INSERT INTO Users (user_id, reputation, display_name, creation_date, last_access_date) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, reputation);
            pstmt.setString(3, displayName);
            pstmt.setTimestamp(4, creationDate);
            pstmt.setTimestamp(5, lastAccessDate);
            pstmt.executeUpdate();
            logger.info("User inserted: " + displayName);
        } catch (SQLException e)            {
            logger.severe("Error inserting user: " + e.getMessage());
        }
    }

    // 插入数据到 Questions 表
    public void insertQuestion(int questionId, int userId, String title, String body, Timestamp creationDate,
                               String tags, int answerCount, int commentCount, int viewCount) {
        String insertQuestionSQL = "INSERT INTO Questions (question_id, user_id, title, body, creation_date, "
                + "tags, answer_count, comment_count, view_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuestionSQL)) {
            pstmt.setInt(1, questionId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, title);
            pstmt.setString(4, body);
            pstmt.setTimestamp(5, creationDate);
            pstmt.setString(6, tags);
            pstmt.setInt(7, answerCount);
            pstmt.setInt(8, commentCount);
            pstmt.setInt(9, viewCount);
            pstmt.executeUpdate();
            logger.info("Question inserted: " + title);
        } catch (SQLException e) {
            logger.severe("Error inserting question: " + e.getMessage());
        }
    }

    // 关闭数据库连接
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed.");
            }
        } catch (SQLException e) {
            logger.severe("Error closing database connection: " + e.getMessage());
        }
    }

    // 关闭外键约束
    public void disableForeignKeyCheck() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET session_replication_role = replica;");
        statement.close();
    }

    // 开启外键约束
    public void enableForeignKeyCheck() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SET session_replication_role = DEFAULT;");
        statement.close();
    }
}

