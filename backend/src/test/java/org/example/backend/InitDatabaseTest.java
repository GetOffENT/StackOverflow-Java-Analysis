package org.example.backend;


import org.example.backend.crawler.DataCrawler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-11 22:42
 */
@SpringBootTest(classes = BackendApplication.class)
public class InitDatabaseTest {

    @Autowired
    private DataCrawler dataCrawler;


    @Test
    public void test(){
        dataCrawler.getQuestionsWithAnswersAndComments();
    }
}
