package cn.edu.sustech.stackoverflow.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-16 1:34
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ErrorVO", description = "TopN 错误或异常")
public class ThrowableVO {
    @Schema(description = "错误或异常名")
    private String name;

    @Schema(description = "错误或异常次数")
    private Long count;

    @Schema(description = "错误或异常出现百分比")
    private Double percentage;
}
