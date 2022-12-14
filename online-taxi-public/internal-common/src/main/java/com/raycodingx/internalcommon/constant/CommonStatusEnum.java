package com.raycodingx.internalcommon.constant;


import lombok.Getter;

public enum CommonStatusEnum {

    /**
     * 验证码错误提示1000_1099
     */
    VERIFICATION_CODE_ERROR(1099,"验证码不正确"),

    SUCCESS(1,"success"),
    FAIL(0,"fail")
    ;
    @Getter
    private int code;
    @Getter
    private String value;

    CommonStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }
}
