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
@Schema(name = "Question", description = "问题信息表")
public class Question implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "问题ID，唯一标识")
    @TableId(value = "question_id", type = IdType.INPUT)
    private Long questionId;

    @Schema(description = "提问者用户ID，逻辑外键")
    private Long ownerUserId;

    @Schema(description = "问题标题")
    private String title;

    @Schema(description = "问题内容")
    private String body;

    @Schema(description = "问题的链接")
    private String link;

    @Schema(description = "是否有回答")
    private Boolean isAnswered;

    @Schema(description = "问题的回答数量")
    private Integer answerCount;

    @Schema(description = "接受的回答ID")
    private Long acceptedAnswerId;

    @Schema(description = "问题的评论数量")
    private Integer commentCount;

    @Schema(description = "问题的查看次数")
    private Integer viewCount;

    @Schema(description = "问题的收藏次数")
    private Integer favoriteCount;

    @Schema(description = "问题的赞同次数")
    private Integer upVoteCount;

    @Schema(description = "问题的反对次数")
    private Integer downVoteCount;

    @Schema(description = "问题的得分")
    private Integer score;

    @Schema(description = "问题创建时间")
    private LocalDateTime creationDate;

    @Schema(description = "问题的最后活动时间")
    private LocalDateTime lastActivityDate;

    @Schema(description = "问题的最后编辑时间")
    private LocalDateTime lastEditDate;

    @Schema(description = "问题的保护时间")
    private LocalDateTime protectedDate;

    @Schema(description = "社区拥有的日期")
    private LocalDateTime communityOwnedDate;

    @Schema(description = "内容许可")
    private String contentLicense;
}
