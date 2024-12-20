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
 * @since 2024-12-16 0:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AnswerWithLengthVO", description = "回答信息及回答长度")
public class AnswerWithLengthVO {
    @Schema(description = "回答ID")
    private Long answerId;

    @Schema(description = "赞同数")
    private Integer upVoteCount;

    @Schema(description = "反对数")
    private Integer downVoteCount;

    @Schema(description = "是否被接受")
    private Boolean isAccepted;

    @Schema(description = "回答长度")
    private Integer length;

    @Schema(description = "回答创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime answerCreateDate;
}
