package cn.edu.sustech.stackoverflow.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-12-15 9:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ErrorAndExceptionVO", description = "TopN 错误和异常")
public class ErrorAndExceptionVO {

    @Schema(description = "错误")
    List<ThrowableVO> errors;

    @Schema(description = "异常")
    List<ThrowableVO> exceptions;
}
