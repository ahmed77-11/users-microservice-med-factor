package com.medfactor.factorusers.dtos;

import com.medfactor.factorusers.entities.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String cin;
    private String firstName;
    private String lastName;
//    private Collection<? extends GrantedAuthority> authorities;
    private List<Role> roles;


}
