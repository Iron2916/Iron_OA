package com.iron.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    LOGIN_ERROR(208,"认证失败"),
    PERMISSION(204, "抱歉您没有操作的此权限！"),
    LOGIN_MOBLE_ERROR(205, "请先登录");


    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code,String message) {
        this.code = code;
        this.message = message;
    }
}
