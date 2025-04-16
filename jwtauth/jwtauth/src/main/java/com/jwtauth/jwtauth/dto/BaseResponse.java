package com.jwtauth.jwtauth.dto;

import lombok.*;

@Builder  // Added toBuilder = true here
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BaseResponse<T> {
    private String code;
    private String title;
    private String message;
    private T data;




}