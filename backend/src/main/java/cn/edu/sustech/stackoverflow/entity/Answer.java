package cn.edu.sustech.stackoverflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "Answer对象", description = "回答信息表")
public class Answer implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("回答ID，唯一标识")
    @TableId(value = "answer_id", type = IdType.INPUT)
    private Long answerId;

    @ApiModelProperty("问题ID，逻辑外键")
    private Long questionId;

    @ApiModelProperty("回答者用户ID，逻辑外键")
    private Long ownerUserId;

    @ApiModelProperty("回答内容")
    private String body;

    @ApiModelProperty("是否被接受")
    private Boolean isAccepted;

    @ApiModelProperty("回答的赞同次数")
    private Integer upVoteCount;

    @ApiModelProperty("回答的反对次数")
    private Integer downVoteCount;

    @ApiModelProperty("回答得分")
    private Integer score;

    @ApiModelProperty("回答的评论数量")
    private Integer commentCount;

    @ApiModelProperty("回答创建时间")
    private LocalDateTime creationDate;

    @ApiModelProperty("回答的最后活动时间")
    private LocalDateTime lastActivityDate;

    @ApiModelProperty("回答的最后编辑时间")
    private LocalDateTime lastEditDate;

    @ApiModelProperty("社区拥有的日期")
    private LocalDateTime communityOwnedDate;

    @ApiModelProperty("内容许可")
    private String contentLicense;
}
