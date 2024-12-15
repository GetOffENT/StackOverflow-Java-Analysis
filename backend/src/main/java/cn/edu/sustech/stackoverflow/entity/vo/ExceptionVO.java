package cn.edu.sustech.stackoverflow.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-15 10:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExceptionVO", description = "TopN异常")
public class ExceptionVO {
    @Schema(description = "异常名")
    private String name;

    @Schema(description = "异常次数")
    private Long count;

    @Schema(description = "异常出现百分比")
    private Double percentage;
}
