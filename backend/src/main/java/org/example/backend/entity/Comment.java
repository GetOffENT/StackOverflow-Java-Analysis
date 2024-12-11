package org.example.backend.entity;

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
 * 评论信息表
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
@ApiModel(value = "Comment对象", description = "评论信息表")
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("评论ID，唯一标识")
    @TableId(value = "comment_id", type = IdType.INPUT)
    private Long commentId;

    @ApiModelProperty("评论的目标（问题ID或回答ID）")
    private Long postId;

    @ApiModelProperty("评论者用户ID，逻辑外键")
    private Long ownerUserId;

    @ApiModelProperty("回复的用户ID（可以为空），逻辑外键")
    private Long replyToUserId;

    @ApiModelProperty("是否已编辑")
    private Boolean edited;

    @ApiModelProperty("评论得分")
    private Integer score;

    @ApiModelProperty("评论创建时间")
    private LocalDateTime creationDate;

    @ApiModelProperty("内容许可")
    private String contentLicense;
}
