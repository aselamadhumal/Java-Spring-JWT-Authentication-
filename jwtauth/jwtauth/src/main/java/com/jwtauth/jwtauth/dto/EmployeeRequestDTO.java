package com.jwtauth.jwtauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequestDTO {

    @Length(min = 1, max = 5,message = "add field")
    private String name;

    private String department;

    private Double salary;

}
