package com.example.backend.common;

public interface UserStatus {
    /**
     * 状态
     * 0:禁用
     * 1:正常
     * -1:注销
     * 默认值： 1
     */
    public static final Long DISABLED = 0L;
    public static final Long NORMAL = 1L;
    public static final Long CANCELED = -1L;
    public static final Long DEFAULT = 1L;

}
