package com.medfactor.factorusers.repos;

import com.medfactor.factorusers.entities.User;
import com.medfactor.factorusers.entities.VerficationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerficationTokenRepository extends JpaRepository<VerficationToken,Long> {
    Optional<VerficationToken> findByToken(String token);
    Optional<VerficationToken> findByUser(User user);

    @Modifying
    @Query("DELETE FROM VerficationToken v WHERE v.user = :user")
    void deleteAllByUser(@Param("user") User user);
}
