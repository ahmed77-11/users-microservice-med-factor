package com.medfactor.factorusers.repos;

import com.medfactor.factorusers.entities.Role;
import com.medfactor.factorusers.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findAllByArchiver(boolean archiver);

    Optional<User> findByCin(String username);
    Optional<User> findByAdherentIdAndArchiver(Long id, boolean archiver);
    List<User> findUsersByRolesContaining(Role role);

    Long countAllByArchiver(boolean archiver);
}
