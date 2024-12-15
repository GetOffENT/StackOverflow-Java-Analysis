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
@Schema(name = "ErrorVO", description = "TopN 错误")
public class ErrorVO {
    @Schema(description = "错误名")
    private String name;

    @Schema(description = "错误次数")
    private Long count;

    @Schema(description = "错误出现百分比")
    private Double percentage;
}
