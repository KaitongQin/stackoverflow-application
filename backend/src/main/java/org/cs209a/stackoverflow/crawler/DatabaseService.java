package org.cs209a.stackoverflow.crawler;

import com.alibaba.fastjson.JSONObject;
import org.cs209a.stackoverflow.mapper.*;
import org.cs209a.stackoverflow.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class DatabaseService {
    private static final int BATCH_SIZE = 1000;
    private final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    @Autowired
    private StanfordCoreNLPService stanfordCoreNLPService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private TimelineMapper timelineMapper;
    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private QuestionTagMapper questionTagMapper;

    private static User getUser(JSONObject json) {
        JSONObject ownerJson = json.getJSONObject("owner");
        User user = new User();
        user.setAccountId(ownerJson.getInteger("account_id") == null ? -1 : ownerJson.getInteger("account_id"));
        user.setUserId(ownerJson.getInteger("user_id") == null ? -1 : ownerJson.getInteger("user_id"));
        user.setProfileImage(ownerJson.getString("profile_image"));
        user.setLink(ownerJson.getString("link") == null ? "" : ownerJson.getString("link"));
        user.setUserType(ownerJson.getString("user_type"));
        user.setDisplayName(ownerJson.getString("display_name"));
        user.setReputation(ownerJson.getInteger("reputation") == null ? -1 : ownerJson.getInteger("reputation"));
        return user;
    }

    public void batchInsertQuestionRecord(List<JSONObject> questions) throws SQLException {
        List<User> userList = new ArrayList<>();
        List<QuestionTag> questionTagList = new ArrayList<>();
        List<Question> questionList = questions.parallelStream().map(q -> {
            JSONObject owner = q.getJSONObject("owner");
            User user = getUser(owner);
            userList.add(user);

            String[] questionTags = q.getJSONArray("tags").stream().map(Object::toString).toArray(String[]::new);
            List<QuestionTag> questionTagList1 = Arrays.stream(questionTags).map(qt -> {
                QuestionTag questionTag = new QuestionTag();
                questionTag.setQuestionId(q.getInteger("question_id"));
                questionTag.setTagName(qt);
                return questionTag;
            }).toList();
            questionTagList.addAll(questionTagList1);

            Question question = new Question();
            question.setId(q.getInteger("question_id"));
            question.setOwnerId(owner.getInteger("account_id"));
            question.setTitle(q.getString("title"));
            question.setBody(q.getString("body"));
            question.setCreationDate(convertDate(q.getInteger("creation_date")));
            question.setAnswerCount(q.getInteger("answer_count"));
            question.setCommentCount(q.getInteger("comment_count"));
            question.setViewCount(q.getInteger("view_count"));
            question.setScore(q.getInteger("score"));
            return question;
        }).toList();
        questionMapper.insert(questionList, BATCH_SIZE);
        userMapper.insert(userList, BATCH_SIZE);
        questionTagMapper.insert(questionTagList, BATCH_SIZE);
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
        List<User> userList = new ArrayList<>();
        List<Timeline> timelineList = timeLine.parallelStream().map(t -> {
            User user = getUser(t.getJSONObject("user"));
            userList.add(user);
            Timeline timeline = new Timeline();
            timeline.setCreationDate(convertDate(t.getInteger("creation_date")));
            timeline.setPostId(t.getInteger("post_id"));
            timeline.setTimelineType(t.getString("timeline_type"));
            timeline.setUserId(user.getAccountId());
            timeline.setCommentId(t.getInteger("comment_id"));
            timeline.setDownVoteCount(t.getInteger("down_vote_count"));
            timeline.setOwnerId(t.getInteger("owner_id"));
            timeline.setQuestionId(t.getInteger("question_id"));
            timeline.setRevisionGuid(t.getString("revision_guid"));
            timeline.setUpVoteCount(t.getInteger("up_vote_count"));
            return timeline;
        }).toList();
        userMapper.insert(userList, BATCH_SIZE);
        timelineMapper.insert(timelineList, BATCH_SIZE);
    }

    public void batchInsertAnswerRecord(List<JSONObject> answers) throws SQLException {
        List<User> ownerList = new ArrayList<>();
        List<Answer> answerList = answers.parallelStream().map(ans -> {
            User owner = getUser(ans.getJSONObject("owner"));
            Answer answer = new Answer();
            answer.setBody(ans.getString("body"));
            answer.setCreationDate(convertDate(ans.getInteger("creation_date")));
            answer.setId(ans.getInteger("answer_id"));
            answer.setIsAccepted(ans.getBoolean("is_accepted"));
            answer.setScore(ans.getInteger("score"));
            answer.setQuestionId(ans.getInteger("question_id"));
            answer.setOwnerId(owner.getAccountId());
            ownerList.add(owner);
            return answer;
        }).toList();

        answerMapper.insert(answerList, BATCH_SIZE);
        userMapper.insert(ownerList, BATCH_SIZE);
    }

    public void batchInsertCommentRecord(List<JSONObject> comments) throws SQLException {
        List<User> ownerList = new ArrayList<>();
        List<Comment> commentList = comments.parallelStream().map(c -> {
            User owner = getUser(c);
            Comment comment = new Comment();
            comment.setBody(c.getString("body"));
            comment.setCreationDate(convertDate(c.getInteger("creation_date")));
            comment.setId(c.getInteger("comment_id"));
            comment.setEdited(c.getBoolean("edited"));
            comment.setPostId(c.getInteger("post_id"));
            comment.setScore(c.getInteger("score"));
            comment.setOwnerId(owner.getAccountId());
            ownerList.add(owner);
            return comment;
        }).toList();

        commentMapper.insert(commentList, BATCH_SIZE);
        userMapper.insert(ownerList, BATCH_SIZE);
    }

    private Timestamp convertDate(Integer date) {
        return date == null ? null : new Timestamp(date * 1000L);
    }

}

