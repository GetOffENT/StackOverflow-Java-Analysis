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
 * @since 2024-12-17 8:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CountInSingleMonthVO", description = "封装某项数据每个月新产生的数量")
public class CountInSingleMonthVO {

    @Schema(description = "新增问题数量")
    private Integer questionCount;

    @Schema(description = "新增回答数量")
    private Integer answerCount;

    @Schema(description = "新增评论数量")
    private Integer commentCount;

    @Schema(description = "时间(某月) yyyy-MM")
    private String time;
}
