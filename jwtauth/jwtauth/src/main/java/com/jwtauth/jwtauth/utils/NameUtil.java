package com.jwtauth.jwtauth.utils;
import com.jwtauth.jwtauth.dto.BaseResponse;
import com.jwtauth.jwtauth.dto.DefaultResponse;
import com.jwtauth.jwtauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NameUtil {
    private final UserRepository userRepository;

    public boolean isUniqueName(String name) {
        boolean isUserEnteredNameExists = isUsernameExists(name);
        if (isUserEnteredNameExists) {
            log.warn("isUniqueName -> Name already exists, name : {}", name);
            return false;
        } else {
            log.info("isUniqueName-> Name is not on the db");
            return true;
        }
    }

    public boolean isUsernameExists(String name) {
        return userRepository.existsByUsername(name);
    }

    public static class ResponseHandler {

        public static <T> ResponseEntity<DefaultResponse> handleResponse(BaseResponse<T> response) {
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
}
