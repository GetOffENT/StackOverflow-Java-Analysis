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
 * 问题信息表
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
@ApiModel(value = "Question对象", description = "问题信息表")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("问题ID，唯一标识")
    @TableId(value = "question_id", type = IdType.INPUT)
    private Long questionId;

    @ApiModelProperty("提问者用户ID，逻辑外键")
    private Long ownerUserId;

    @ApiModelProperty("问题标题")
    private String title;

    @ApiModelProperty("问题内容")
    private String body;

    @ApiModelProperty("问题的链接")
    private String link;

    @ApiModelProperty("是否有回答")
    private Boolean isAnswered;

    @ApiModelProperty("问题的回答数量")
    private Integer answerCount;

    @ApiModelProperty("接受的回答ID")
    private Long acceptedAnswerId;

    @ApiModelProperty("问题的评论数量")
    private Integer commentCount;

    @ApiModelProperty("问题的查看次数")
    private Integer viewCount;

    @ApiModelProperty("问题的收藏次数")
    private Integer favoriteCount;

    @ApiModelProperty("问题的赞同次数")
    private Integer upVoteCount;

    @ApiModelProperty("问题的反对次数")
    private Integer downVoteCount;

    @ApiModelProperty("问题的得分")
    private Integer score;

    @ApiModelProperty("问题创建时间")
    private LocalDateTime creationDate;

    @ApiModelProperty("问题的最后活动时间")
    private LocalDateTime lastActivityDate;

    @ApiModelProperty("问题的最后编辑时间")
    private LocalDateTime lastEditDate;

    @ApiModelProperty("问题的保护时间")
    private LocalDateTime protectedDate;

    @ApiModelProperty("内容许可")
    private String contentLicense;
}
