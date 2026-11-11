package com.elder.preconsult.common;

/**
 * 业务错误码。新增错误码必须在此登记，禁止在业务代码里裸写数字。
 */
public final class ErrorCode {

    /** 成功 */
    public static final int SUCCESS = 0;

    /** 参数不合法 */
    public static final int PARAM_INVALID = 40000;

    /** 量表不存在 */
    public static final int SCALE_NOT_FOUND = 40001;

    /** 系统内部错误（对外只返回该码，不返回堆栈） */
    public static final int SYSTEM_ERROR = 50000;

    private ErrorCode() {
    }
}
