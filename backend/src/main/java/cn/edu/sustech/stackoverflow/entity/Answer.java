package cn.edu.sustech.stackoverflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 回答信息表
 * </p>
 *
 * @author Yuxian Wu
 * @since 2024-12-11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Accessors(chain = true)
@Schema(name = "Answer", description = "回答信息表")
public class Answer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "回答ID，唯一标识")
    @TableId(value = "answer_id", type = IdType.INPUT)
    private Long answerId;

    @Schema(description = "问题ID，逻辑外键")
    private Long questionId;

    @Schema(description = "回答者用户ID，逻辑外键")
    private Long ownerUserId;

    @Schema(description = "回答内容")
    private String body;

    @Schema(description = "是否被接受")
    private Boolean isAccepted;

    @Schema(description = "回答的赞同次数")
    private Integer upVoteCount;

    @Schema(description = "回答的反对次数")
    private Integer downVoteCount;

    @Schema(description = "回答得分")
    private Integer score;

    @Schema(description = "回答的评论数量")
    private Integer commentCount;

    @Schema(description = "回答创建时间")
    private LocalDateTime creationDate;

    @Schema(description = "回答的最后活动时间")
    private LocalDateTime lastActivityDate;

    @Schema(description = "回答的最后编辑时间")
    private LocalDateTime lastEditDate;

    @Schema(description = "社区拥有的日期")
    private LocalDateTime communityOwnedDate;

    @Schema(description = "内容许可")
    private String contentLicense;
}
