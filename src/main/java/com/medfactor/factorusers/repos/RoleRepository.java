package com.medfactor.factorusers.repos;

import com.medfactor.factorusers.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long>{
    Optional<Role> findByRole(String role);

}
