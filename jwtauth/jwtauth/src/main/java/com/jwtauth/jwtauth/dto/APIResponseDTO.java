package com.jwtauth.jwtauth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponseDTO<T> {

    int recordCount;
    T response;

}
