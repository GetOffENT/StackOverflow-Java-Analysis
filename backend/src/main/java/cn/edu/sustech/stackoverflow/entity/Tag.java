package cn.edu.sustech.stackoverflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 标签信息表
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
@Schema(name = "Tag", description = "标签信息表")
public class Tag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "标签ID，唯一标识")
    @TableId(value = "tag_id", type = IdType.AUTO)
    private Long tagId;

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "标签被使用次数")
    private Integer count;
}
