package cn.edu.sustech.stackoverflow.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-15 17:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TopicByEngagementQueryDTO", description = "指定时间段内用户声望高的用户参与度最高的n个话题查询参数")
public class TopicByEngagementQueryDTO {

    @Schema(description = "前n个")
    Integer n;

    @Schema(description = "reputation前百分之多少的用户")
    Double percentage;

    @Schema(description = "高声誉用户问题数量权重")
    Double questionWeight;

    @Schema(description = "高声誉用户回答数量权重")
    Double answerWeight;

    @Schema(description = "高声誉用户评论数量权重")
    Double commentWeight;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "开始时间")
    LocalDateTime start;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "结束时间")
    LocalDateTime end;
}
