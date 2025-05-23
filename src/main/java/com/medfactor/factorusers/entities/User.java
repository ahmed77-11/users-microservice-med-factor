package com.medfactor.factorusers.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "cin", nullable = true)
    private String cin;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    @Column(name = "profile_picture", nullable = true)
    private String profilePicture;
    @Column(name = "archiver", nullable = true)
    private boolean archiver=false;

    @Column(name="adherent", nullable = true)
    private Long adherentId;
    @Column(name="isAdherent", nullable = true)
    private boolean isAdherent=false;

    @Column(name = "force_change_password", nullable = true)
    private boolean forceChangePassword=true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_role", joinColumns = @JoinColumn(name="user_id"), inverseJoinColumns = @JoinColumn(name="role_id"))
    private List<Role> roles;

    public boolean getArchiver() {
        return archiver;
    }
}
