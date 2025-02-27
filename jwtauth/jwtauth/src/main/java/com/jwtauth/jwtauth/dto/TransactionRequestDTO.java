package com.jwtauth.jwtauth.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionRequestDTO {

    private String accountNo;
    private String nic;
    private String remark;
    private int amount;
}
