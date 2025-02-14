package com.medfactor.factorusers.dtos;

import com.medfactor.factorusers.entities.Role;
import com.medfactor.factorusers.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String email;
    private String cin;
    private String firstName;
    private String lastName;
//    private Collection<? extends GrantedAuthority> authorities;
    private List<String> roles;
    private String token;
    public LoginResponse(String email,String cin,String firstName,String lastName,  Collection<? extends GrantedAuthority> authorities,String token){
        this.email=email;
        this.cin=cin;
        this.firstName=firstName;
        this.lastName=lastName;
        this.roles = authorities.stream()
                .map(GrantedAuthority::getAuthority) // Extract authority name
                .collect(Collectors.toList());
        this.token=token;
    }

}
