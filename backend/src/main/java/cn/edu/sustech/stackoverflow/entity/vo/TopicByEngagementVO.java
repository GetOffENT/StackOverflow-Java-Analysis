package cn.edu.sustech.stackoverflow.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-15 17:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TopicByEngagementVO", description = "前n参与度标签")
public class TopicByEngagementVO {

    @Schema(description = "标签ID")
    private Long tagId;

    @Schema(description = "标签名")
    private String tagName;

    @Schema(description = "根据高声誉用户参与度(问题、回答、评论对应的权重)计算的分数")
    private Double score;

    @Schema(description = "问题分")
    private Double questionScore;

    @Schema(description = "回答分")
    private Double answerScore;

    @Schema(description = "评论分")
    private Double commentScore;

    @Schema(description = "标签下高声誉用户发布的问题数")
    private Integer questionCount;

    @Schema(description = "标签下问题中高声誉用户发布的回答数")
    private Integer answerCount;

    @Schema(description = "标签下问题和回答中的高声誉用户发布的评论数")
    private Integer commentCount;

    @Schema(description = "数据对应的某一年份")
    @JsonFormat(pattern = "yyyy")
    private LocalDateTime time;
}
