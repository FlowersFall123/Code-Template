package com.example.backend.entity;

/*
 * @Auther:fz
 * @Date:2025/5/30
 * @Description:
 */

import lombok.Data;


@Data
public class RestBean<T> {
    private int status;
    private boolean success;
    private String message;
    private T data;

    public RestBean(int status, boolean success, String message , T data) {
        this.status = status;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> RestBean<T> success(String message) {
        return new RestBean<>(200, true, message ,null);
    }

    public static <T> RestBean<T> success(String message , T data) {
        return new RestBean<>(200, true, message, data);
    }

    public static <T> RestBean<T> failure(int status, String message) {
        return new RestBean<>(status, false, message, null);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}