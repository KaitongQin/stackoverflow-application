package org.cs209a.stackoverflow.crawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerMain {

    private static final int PAGE_SIZE = 100;
    private static final int PAGE_STEP = 100;

    private static final Logger logger = LoggerFactory.getLogger(CrawlerMain.class);

    public static void main(String[] args) {
        // 使用 try-with-resources 自动管理资源
        try {
            DatabaseService databaseService = new DatabaseService();
            DataCollector dataCollector = new DataCollector(databaseService, PAGE_SIZE, PAGE_STEP);
            // 在禁用外键检查的状态下执行数据收集
            try {
//                databaseService.getAllError();
                dataCollector.collectData();
            } catch (Exception e) {
                throw new RuntimeException("Failed to collect data", e);
            }
        } catch (Exception e) {
            logger.error("Application failed", e);
            System.exit(1);
        }
    }
}
