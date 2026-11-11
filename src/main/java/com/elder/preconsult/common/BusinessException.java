package com.elder.preconsult.common;

/**
 * 业务异常。业务层所有可预期错误必须抛本异常，
 * 禁止 throw new RuntimeException / 裸 Exception（AGENT.md 硬性规则 B-2）。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
