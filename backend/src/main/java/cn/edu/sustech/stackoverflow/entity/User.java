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
 * 用户信息表
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
@Schema(name = "User", description = "用户信息表")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID，唯一标识")
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    @Schema(description = "StackOverflow账户ID")
    private Long accountId;

    @Schema(description = "用户声誉分数")
    private Integer reputation;

    @Schema(description = "用户类型（如：registered）")
    private String userType;

    @Schema(description = "用户接受率（0-100）")
    private Integer acceptRate;

    @Schema(description = "用户头像链接")
    private String profileImage;

    @Schema(description = "用户显示名称")
    private String displayName;

    @Schema(description = "用户个人主页链接")
    private String link;
}
