package com.medfactor.factorusers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetFirstTimeRequest {
    private String email;
    private String password;
    private String confirmPassword;
    private String code;
}
