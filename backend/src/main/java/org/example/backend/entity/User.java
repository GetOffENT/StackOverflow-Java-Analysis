package org.example.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "User对象", description = "用户信息表")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID，唯一标识")
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    @ApiModelProperty("StackOverflow账户ID")
    private Long accountId;

    @ApiModelProperty("用户声誉分数")
    private Integer reputation;

    @ApiModelProperty("用户类型（如：registered）")
    private String userType;

    @ApiModelProperty("用户接受率（0-100）")
    private Integer acceptRate;

    @ApiModelProperty("用户头像链接")
    private String profileImage;

    @ApiModelProperty("用户显示名称")
    private String displayName;

    @ApiModelProperty("用户个人主页链接")
    private String link;
}
