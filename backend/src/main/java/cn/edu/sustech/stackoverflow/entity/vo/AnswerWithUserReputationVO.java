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
 * @since 2024-12-15 22:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AnswerWithUserReputationVO", description = "回答信息及回答用户声望")
public class AnswerWithUserReputationVO {

    @Schema(description = "回答ID")
    private Long answerId;

    @Schema(description = "赞同数")
    private Integer upVoteCount;

    @Schema(description = "反对数")
    private Integer downVoteCount;

    @Schema(description = "是否被接受")
    private Boolean isAccepted;

    @Schema(description = "回答用户声望")
    private Integer reputation;

}
