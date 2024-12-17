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
 * @since 2024-12-17 7:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "OverviewVO", description = "数据概览")
public class OverviewVO {

    @Schema(description = "问题数量")
    private Long questionCount;

    @Schema(description = "回答数量")
    private Long answerCount;

    @Schema(description = "评论数量")
    private Long commentCount;

    @Schema(description = "用户数量")
    private Long userCount;
}
