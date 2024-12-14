package cn.edu.sustech.stackoverflow;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("cn.edu.sustech.stackoverflow.mapper")
@EnableTransactionManagement
@Slf4j
public class StackOverflowJavaAnalysisApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment environment = SpringApplication.run(StackOverflowJavaAnalysisApplication.class, args).getEnvironment();
        log.info("""

                        ----------------------------------------------------------
                        \t\
                        Application: '{}' is running Success!\s
                        \t\
                        Local URL: \thttp://localhost:{}
                        \t\
                        Document:\thttp://localhost:{}/doc.html
                        ----------------------------------------------------------""",
                environment.getProperty("spring.application.name"),
                environment.getProperty("server.port"),
                environment.getProperty("server.port"));
    }

}
