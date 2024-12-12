package org.example.backend.crawler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.properties.StackOverflowProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-12 20:25
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataCrawler {

    private final StackOverflowProperties stackOverflowProperties;

    private final DataProcessor dataProcessor;

    private int questionCount;


    /**
     * 获取包含Java标签的问题数量
     */
    public void getQuestionCount() {
        log.info("开始获取包含Java标签的问题数量...");
        String url = "https://api.stackexchange.com/2.3/questions";
        Map<String, String> params = Map.of(
                "filter", "total",
                "tagged", "java",
                "site", "stackoverflow"
        );
        String response = OkHttpUtil.get(url, params, null);
        JSONObject jsonObject = JSONObject.parseObject(response);
        questionCount = jsonObject.getInteger("total");
        log.info("获取到包含Java标签的问题数量为{}", questionCount);
    }


    /**
     * 获取问题及其相关信息(包含回答和评论，回答也包含评论)
     */
    @Transactional(rollbackFor = Exception.class)
    public void getQuestionsWithAnswersAndComments() {
        before();

        log.info("开始获取问题及其相关信息...");

        // 获取问题的数量
        Integer count = stackOverflowProperties.getCount();

        // 计算页数
        Integer pageSize = 100;
        int pageCount = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;

        // 总页数
        int totalPageCount = questionCount % pageSize == 0 ? questionCount / pageSize : questionCount / pageSize + 1;

        // 每pageStep页随机选择一页
        int pageStep = totalPageCount / pageCount;

        // 最后一页的问题数量
        int lastPageSize = count % pageSize == 0 ? pageSize : count % pageSize;


        String url = "https://api.stackexchange.com/2.3/questions";
        for (int i = 0; i < pageCount; i++) {
            // 随机选择一页
            int page = (int) (Math.random() * pageStep) + i * pageStep;
            log.info("正在获取第{}页的问题..., 每页{}个", page, pageSize);
            Map<String, String> params = Map.of(
                    "page", String.valueOf(page),
                    "pagesize", String.valueOf(i == pageCount - 1 ? lastPageSize : pageSize),
                    "order", "desc",
                    "sort", "activity",
                    "tagged", "java",
                    "site", "stackoverflow",
                    "filter", stackOverflowProperties.getFilter(),
                    "key", stackOverflowProperties.getKey()
            );
            String response = OkHttpUtil.get(url, params, null);
            JSONObject jsonObject = JSONObject.parseObject(response);
            JSONArray items = jsonObject.getJSONArray("items");

            if (stackOverflowProperties.getSaveToJson()) {
                jsonArray.addAll(items);
            }

            dataProcessor.handleQuestions(items);
        }

        log.info("获取问题及其相关信息完成");
        after();
    }


    private JSONArray jsonArray;

    /**
     * 获取问题及其相关信息之前的操作
     */
    private void before() {
        getQuestionCount();

        if (stackOverflowProperties.getSaveToJson()) {
            jsonArray = new JSONArray();
        }

        if (stackOverflowProperties.getSaveToDatabase()) {
            dataProcessor.initDatabase();
        }
    }

    /**
     * 获取问题及其相关信息之后的操作
     */
    private void after() {
        if (stackOverflowProperties.getSaveToJson()) {
            File file = new File(stackOverflowProperties.getJsonPath());
            try (FileWriter fileWriter = new FileWriter(file)) {
                JSONObject fileContent = new JSONObject();
                fileContent.put("items", jsonArray);

                fileWriter.write(JSONObject.toJSONString(fileContent, true));
                log.info("写入JSON文件成功");
            } catch (IOException e) {
                log.error("写入文件失败", e);
            }
        }
    }

}
