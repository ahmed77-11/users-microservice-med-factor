package com.medfactor.factorusers.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginMobileResponse {
    private Long id;
    private String email;
    private String rne;
    private String firstName;
    private String lastName;
    //    private Collection<? extends GrantedAuthority> authorities;
    private List<String> roles;
    private String token;
    private boolean forceChangePassword;
    private String profilePicture;
    private Double limiteAuto;
    private Double disponible;
    private int contratId;
    private Double utilise;
    private Long adherentId;
    public LoginMobileResponse(Long id, String email, String rne, String firstName, String lastName, Collection<? extends GrantedAuthority> authorities, String token, boolean forceChangePassword, String profilePicture, Double limiteAuto, Double disponible, Double utilise,Long adherentId,int contratId) {
        this.id=id;
        this.email=email;
        this.rne=rne;
        this.firstName=firstName;
        this.lastName=lastName;
        this.roles = authorities.stream()
                .map(GrantedAuthority::getAuthority) // Extract authority name
                .collect(Collectors.toList());
        this.token=token;
        this.forceChangePassword = forceChangePassword;
        this.profilePicture=profilePicture;
        this.limiteAuto=limiteAuto;
        this.disponible=disponible;
        this.utilise=utilise;
        this.adherentId=adherentId;
        this.contratId=contratId;


    }


}
