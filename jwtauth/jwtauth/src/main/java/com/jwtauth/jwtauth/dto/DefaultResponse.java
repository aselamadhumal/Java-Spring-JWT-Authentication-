package com.jwtauth.jwtauth.dto;

import com.jwtauth.jwtauth.utils.ResponseUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

@Getter
@Setter
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class DefaultResponse {

    private String code;
    private String title;
    private String message;
    private Object data;

    // Success method with data
    public static DefaultResponse success(String title, String message, Object data) {
        return new DefaultResponse(ResponseUtil.SUCCESS_CODE, title, message, data != null ? data : new HashMap<String, Object>());
    }

    // Success method without data
    public static DefaultResponse success(String title, String message) {
        return new DefaultResponse(ResponseUtil.SUCCESS_CODE, title, message, new HashMap<String, Object>());
    }

    // Error method with data
    public static DefaultResponse error(String title, String message, Object data) {
        return new DefaultResponse(ResponseUtil.FAILED_CODE, title, message, data != null ? data : new HashMap<String, Object>());
    }

    // Error method without data
    public static DefaultResponse error(String title, String message) {
        return new DefaultResponse(ResponseUtil.FAILED_CODE, title, message, new HashMap<String, Object>());
    }

    // Internal Server Error method with data
    public static DefaultResponse internalServerError(String title, String message, Object data) {
        return new DefaultResponse(ResponseUtil.INTERNAL_SERVER_ERROR_CODE, title, message, data != null ? data : new HashMap<String, Object>());
    }

    // Internal Server Error method without data
    public static DefaultResponse internalServerError(String title, String message) {
        return new DefaultResponse(ResponseUtil.INTERNAL_SERVER_ERROR_CODE, title, message, new HashMap<String, Object>());
    }
}
