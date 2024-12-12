package cn.edu.sustech.stackoverflow;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("cn.edu.sustech.stackoverflow.mapper")
@EnableTransactionManagement
public class StackOverflowJavaAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(StackOverflowJavaAnalysisApplication.class, args);
    }

}
