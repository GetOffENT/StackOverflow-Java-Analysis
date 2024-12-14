package cn.edu.sustech.stackoverflow.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
