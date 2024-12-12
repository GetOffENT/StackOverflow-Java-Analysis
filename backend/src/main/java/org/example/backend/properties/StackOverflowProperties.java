package org.example.backend.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-12 18:41
 */
@Component
@ConfigurationProperties(prefix = "stack-overflow.stack-exchange")
@Data
public class StackOverflowProperties {
    private String clientId;
    private String clientSecret;
    private String key;

    /**
     * 是否保存到JSON文件
     */
    private Boolean saveToJson;

    /**
     * JSON文件路径
     */
    private String jsonPath;

    /**
     * 获取问题的数量
     */
    private Integer count;
}
