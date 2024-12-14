package cn.edu.sustech.stackoverflow.result;

/**
 * <p>
 * 后端统一返回结果编码
 * </p>
 *
 * @author Yuxian Wu
 * @version 1.0
 * @since 2024-11-06 18:05
 */
public enum ResultCode {
    SUCCESS(20000, "成功"),
    ERROR(20001, "失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return code;
    }

    public String message() {
        return message;
    }
}
