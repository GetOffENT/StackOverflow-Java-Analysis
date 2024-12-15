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
 * @since 2024-12-15 22:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AnswerWithCreateDateVO", description = "回答信息以及相关数据创建时间")
public class AnswerWithCreateDateVO {

    @Schema(description = "回答ID")
    private Long answerId;

    @Schema(description = "是否是第一个回答")
    private Boolean first;

    @Schema(description = "是否被接受")
    private Boolean isAccepted;

    @Schema(description = "赞同数")
    private Integer upVoteCount;

    @Schema(description = "反对数")
    private Integer downVoteCount;

    @Schema(description = "从问题创建开始的到回答创建的间隔时间(ms)")
    private Long duration;

    @Schema(description = "回答创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime answerCreateDate;

    @Schema(description = "问题ID")
    private Long questionId;

    @Schema(description = "问题创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime questionCreateDate;
}
