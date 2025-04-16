package com.jwtauth.jwtauth.utils;

import com.jwtauth.jwtauth.dto.BaseResponse;
import com.jwtauth.jwtauth.dto.DefaultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseHandler {

    private ResponseHandler(){}

    public  ResponseEntity<DefaultResponse> handleResponse(BaseResponse<?> response) {
        if (ResponseUtil.SUCCESS_CODE.equals(response.getCode())) {
            return ResponseEntity.ok(DefaultResponse.success(ResponseUtil.SUCCESS, response.getMessage(), response.getData()));
        } else if (ResponseUtil.INTERNAL_SERVER_ERROR_CODE.equals(response.getCode())) {
            return ResponseEntity.internalServerError()
                    .body(DefaultResponse.internalServerError(ResponseUtil.INTERNAL_SERVER_ERROR_CODE, response.getMessage()));
        } else {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(ResponseUtil.FAILED, response.getMessage(), response.getData()));
        }
    }
}