package cn.edu.sustech.stackoverflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 问题标签关联表
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
@TableName("question_tag")
@ApiModel(value = "QuestionTag对象", description = "问题标签关联表")
public class QuestionTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("问题ID，逻辑外键")
    private Long questionId;

    @ApiModelProperty("标签ID，逻辑外键")
    private Long tagId;
}
