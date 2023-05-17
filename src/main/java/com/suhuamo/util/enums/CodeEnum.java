package com.suhuamo.util.enums;

/**
 * @author suhuamo
 * @slogan 想和喜欢的人睡在冬日的暖阳里
 * @date 2023/03/05
 * 状态码的枚举类
 */
public enum CodeEnum {
    SUCCESS(200, "成功"),
    UNAUTHORIZED_ERROR(401, "未授权"),
    UNEXPECTED_TOKEN(400,"错误token值"),
    PARAM_ERROR(500, "参数错误"),
    TIMED_OUT(408, "请求超时");
    // 返回状态码
    private int code;
    // 描述
    private String desc;


    CodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
