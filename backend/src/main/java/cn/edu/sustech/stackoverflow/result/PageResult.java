package cn.edu.sustech.stackoverflow.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 封装分页查询结果
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-11-06 18:05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private Long total; //总记录数

    private List<T> records; //当前页数据集合

}
