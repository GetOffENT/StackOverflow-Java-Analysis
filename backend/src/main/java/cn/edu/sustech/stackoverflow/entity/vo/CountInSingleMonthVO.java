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

    @Schema(description = "数量")
    private Integer count;

    @Schema(description = "时间(某月) yyyy-MM")
    private String time;
}
