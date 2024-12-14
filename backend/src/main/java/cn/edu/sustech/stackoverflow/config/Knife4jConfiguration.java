package cn.edu.sustech.stackoverflow.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-14 18:50
 */
@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info() // 基本信息配置
                        .title("StackOverflow-Java-Analysis接口文档")
                        .description("StackOverflow-Java-Analysis后端接口服务...")
                        .version("v1.0.0") // 版本
                        .contact(new Contact().name("Yuxian Wu").email("12212614@mail.sustech.edu.cn"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );
    }

}
