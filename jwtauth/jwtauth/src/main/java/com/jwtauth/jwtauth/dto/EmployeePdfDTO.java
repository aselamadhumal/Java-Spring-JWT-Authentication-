package com.jwtauth.jwtauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeePdfDTO {

    private Long id;
    private String name;
    private String department;
    private Double salary;

}
