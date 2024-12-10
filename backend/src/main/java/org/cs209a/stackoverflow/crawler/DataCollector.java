package org.cs209a.stackoverflow.crawler;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataCollector {
    private static final Logger logger = LoggerFactory.getLogger(DataCollector.class);
    private static final boolean FROM_JSON = true;
    private final StackOverflowService stackOverflowService;
    private final DatabaseService databaseService;
    // 存储采集到的数据
    private final List<JSONObject> questionList = new ArrayList<>();
    private final List<JSONObject> answerList = new ArrayList<>();
    private final List<JSONObject> commentList = new ArrayList<>();
    private final List<JSONObject> timelineList = new ArrayList<>();
    // 配置参数
    private final int pageSize;
    private final int pageStep;
    // 统计信息
    private int totalQuestions;
    private int noAnswerQuestions;

    public DataCollector(DatabaseService databaseService, int pageSize, int pageStep) {
        this.databaseService = databaseService;
        this.pageSize = pageSize;
        this.pageStep = pageStep;
        this.stackOverflowService = new StackOverflowService(pageSize);
        refresh();
    }

    public void refresh() {
        try {
            JSONObject questionStats = stackOverflowService.getQuestionStats();
            JSONObject noAnswerStats = stackOverflowService.getNoAnswerStats();

            this.totalQuestions = questionStats.getInteger("total");
            this.noAnswerQuestions = noAnswerStats.getInteger("total");

        } catch (ApiException e) {
            logger.error("Failed to refresh statistics", e);
            throw e;
        }
    }

    public void collectData() {
        logger.info("Starting data collection");
        try {
            startNewCollection();
        } catch (Exception e) {
            logger.error("Collection failed", e);
            throw e;
        }
    }

    private void startNewCollection() {
        logger.info("Starting new collection");
        if (FROM_JSON) {
            loadJsonToDatabase();
            return;
        }
        collectQuestions();
        collectAnswers();
        collectComments();
        collectTimeLine();
        saveToDatabase();
    }

    private void collectTimeLine() {
        List<Integer> questionIds = questionList.stream()
                .map(q -> q.getInteger("question_id"))
                .toList();

        List<Integer> questions = new ArrayList<>();
        for (int questionId : questionIds) {
            questions.add(questionId);

            if (questions.size() == 100) {
                logger.info("Collecting timeline for questions: {}, progress: {}%",
                        questions,
                        String.format("%.2f", (100.0 * questionIds.indexOf(questionId) / questionIds.size()))
                );
                processTimeLineBatch(questions);
                questions.clear();
            }
        }
        if (!questions.isEmpty()) {
            processTimeLineBatch(questions);
        }
        logger.info("Timeline collection completed, total timeline: {}", timelineList.size());
    }

    private void processTimeLineBatch(List<Integer> questions) {
        try {
            List<JSONObject> timelines = stackOverflowService.getTimeLine(questions);
            timelineList.addAll(timelines);
        } catch (Exception e) {
            logger.error("Error collecting timeline for questions: {}", questions, e);
            throw e;
        }
    }


    private void collectQuestions() {
        int pageTotal = totalQuestions / pageSize;
        int startPage = 1;

        for (int page = startPage; page <= pageTotal; page += pageStep) {
            logger.info("Collecting questions at page: {}, progress: {}%",
                    page,
                    String.format("%.2f", (100.0 * page / pageTotal))
            );
            try {
                List<JSONObject> questions = stackOverflowService.getQuestions(page);
                questionList.addAll(questions);
            } catch (Exception e) {
                logger.error("Error collecting questions at page {}", page, e);
                throw e;
            }
        }
        logger.info("Questions collection completed, total questions: {}", questionList.size());
    }

    private void collectAnswers() {
        List<Integer> questionIds = new ArrayList<>();

        for (JSONObject question : questionList) {
            int questionId = question.getInteger("question_id");
            questionIds.add(questionId);

            if (questionIds.size() == 100) {
                logger.info("Collecting answers for question: {}, progress: {}%",
                        question.getInteger("question_id"),
                        String.format("%.2f", (100.0 * questionList.indexOf(question) / questionList.size()))
                );

                processAnswerBatch(questionIds);
                questionIds.clear();
            }
        }

        if (!questionIds.isEmpty()) {
            processAnswerBatch(questionIds);
        }
        logger.info("Answers collection completed, total answers: {}", answerList.size());
    }

    private void processAnswerBatch(List<Integer> questionIds) {
        try {
            List<JSONObject> answers = stackOverflowService.getAnswers(questionIds);
            answerList.addAll(answers);
        } catch (Exception e) {
            logger.error("Error collecting answers for questions: {}", questionIds, e);
            throw e;
        }
    }

    private void collectComments() {
        // Collect question comments
        List<Integer> questionIds = questionList.stream()
                .map(q -> q.getInteger("question_id"))
                .collect(Collectors.toList());

        for (int i = 0; i < questionIds.size(); i += 100) {
            List<Integer> batch = questionIds.subList(i, Math.min(i + 100, questionIds.size()));
            List<JSONObject> comments = stackOverflowService.getComments("question", batch);
            commentList.addAll(comments);
            logger.info("Collecting question comments - Progress: {}%", String.format("%.2f", (100.0 * i / questionIds.size())));
        }

        // Collect answer comments
        List<Integer> answerIds = answerList.stream()
                .map(a -> a.getInteger("answer_id"))
                .collect(Collectors.toList());

        for (int i = 0; i < answerIds.size(); i += 100) {
            List<Integer> batch = answerIds.subList(i, Math.min(i + 100, answerIds.size()));
            List<JSONObject> comments = stackOverflowService.getComments("answer", batch);
            commentList.addAll(comments);
            logger.info("Collecting answer comments - Progress: {}%", String.format("%.2f", (100.0 * i / answerIds.size())));
        }
        logger.info("Comments collection completed, total comments: {}", commentList.size());
    }

    private void loadJsonToDatabase() {
        logger.info("Reading json");
        try (BufferedReader questionReader = new BufferedReader(new FileReader("questions.json"));
             BufferedReader answerReader = new BufferedReader(new FileReader("answers.json"));
             BufferedReader commentReader = new BufferedReader(new FileReader("comments.json"));
             BufferedReader timelineReader = new BufferedReader(new FileReader("timeline.json"))) {
            String line;

            while ((line = questionReader.readLine()) != null) {
                questionList.add(JSONObject.parseObject(line));
            }
            while ((line = answerReader.readLine()) != null) {
                answerList.add(JSONObject.parseObject(line));
            }
            while ((line = commentReader.readLine()) != null) {
                commentList.add(JSONObject.parseObject(line));
            }
            while ((line = timelineReader.readLine()) != null) {
                timelineList.add(JSONObject.parseObject(line));
            }
            saveToDatabase();

        } catch (Exception e) {
            logger.error("Failed to read json", e);
            throw new ApiException("Failed to read json", e);
        }
    }

    private void saveToDatabase() {
        try {
            databaseService.saveToDatabase(questionList, answerList, commentList, timelineList);
            logger.info("Data collection completed successfully");
        } catch (Exception e) {
            logger.error("Database save failed", e);
            throw new ApiException("Failed to save to database", e);
        }
    }

    private int calculateTotalPages(Integer total) {
        return (int) Math.ceil((double) total / pageSize);
    }

    public double getNoAnswerPercent() {
        return (double) noAnswerQuestions / totalQuestions;
    }
}