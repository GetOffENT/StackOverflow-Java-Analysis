package cn.edu.sustech.stackoverflow.result;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 后端统一返回结果(data是对象的形式)
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-11-06 18:05
 */
@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1成功，0和其它数字为失败
    private String message; //错误信息
    private T data; //数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = ResultCode.SUCCESS.code();
        result.message = ResultCode.SUCCESS.message();
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.data = object;
        result.code = ResultCode.SUCCESS.code();
        result.message = ResultCode.SUCCESS.message();
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.message = msg;
        result.code = ResultCode.ERROR.code();
        return result;
    }
}
