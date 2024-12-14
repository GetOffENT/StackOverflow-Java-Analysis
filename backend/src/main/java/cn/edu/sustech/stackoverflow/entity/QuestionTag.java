package cn.edu.sustech.stackoverflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "QuestionTag", description = "问题标签关联表")
public class QuestionTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "问题ID，逻辑外键")
    private Long questionId;

    @Schema(description = "标签ID，逻辑外键")
    private Long tagId;
}
