package com.jwtauth.jwtauth.dto;

import com.jwtauth.jwtauth.utils.MessageConstantUtil;
import com.jwtauth.jwtauth.utils.ResponseUtil;
import lombok.*;

@Data
@Builder(toBuilder = true)  // Added toBuilder = true here
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> {
    private String code;
    private String title;
    private String message;
    private T data;


    public static <T> BaseResponse<T> success(T data, String message) {
        return BaseResponse.<T>builder()
                .code(ResponseUtil.SUCCESS_CODE)
                .title(ResponseUtil.SUCCESS)
                .message(String.valueOf(MessageConstantUtil.class))
                .data(data)
                .build();
    }


    public static <T> BaseResponse<T> failure(String message) {
        return BaseResponse.<T>builder()
                .code(ResponseUtil.FAILED_CODE)
                .title(ResponseUtil.FAILED)
                .message(String.valueOf(MessageConstantUtil.class))
                .build();
    }


    public BaseResponse<T> withData(T data) {
        return this.toBuilder().data(data).build();
    }
}