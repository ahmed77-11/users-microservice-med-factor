package com.medfactor.factorusers.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdherentRequest {
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private Long adherentId;
    private List<String> roles;
    private String profilePicture;
    private boolean forceChangePassword;
    private boolean archiver;
}
