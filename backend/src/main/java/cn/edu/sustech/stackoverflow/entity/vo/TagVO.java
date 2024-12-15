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
 * @since 2024-12-14 17:15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TagVO", description = "标签TopN")
public class TagVO {

    @Schema(description = "标签ID")
    private Long tagId;

    @Schema(description = "标签名")
    private String tagName;

    @Schema(description = "标签被使用次数")
    private Integer count;

    @Schema(description = "标签出现百分比")
    private Double percentage;

    @Schema(description = "数据对应的某一年份")
    @JsonFormat(pattern = "yyyy")
    private LocalDateTime time;
}
