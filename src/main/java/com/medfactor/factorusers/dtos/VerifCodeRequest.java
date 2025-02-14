package com.medfactor.factorusers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifCodeRequest {
    private String email;
    private String code;
}
