package com.medfactor.factorusers.service;

import com.medfactor.factorusers.entities.Role;
import com.medfactor.factorusers.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class UserDetailsImpl  implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String firstname;
    private String lastname;
    private String cin;
    private String email;
    private String password;
    private boolean forceChangePassword; // new field
    private String profilePicture;
    private Long adherentId;
    private boolean isAdherent=false;


    private Collection<? extends GrantedAuthority> authorities;

    private  UserDetailsImpl(Long id, String firstname,String lastname, String email,String cin, String password, Collection<? extends GrantedAuthority> authorities,boolean forceChangePassword,String profilePicture,Long adherentId,boolean isAdherent) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.cin=cin;
        this.password = password;
        this.authorities = authorities;
        this.forceChangePassword=forceChangePassword;
        this.profilePicture=profilePicture;
        this.adherentId=adherentId;
        this.isAdherent=isAdherent;

    }
    public static UserDetailsImpl build(User user) {
        // Convert roles to a collection of GrantedAuthority
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                        user.getId(),
                        user.getFirstname(),
                        user.getLastname(),
                        user.getEmail(),
                        user.getCin(),
                        user.getPassword(),
                authorities,
                user.isForceChangePassword(), // Pass the list of authorities
                user.getProfilePicture(),
                user.getAdherentId(),
                user.isAdherent()
                );
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }


}
