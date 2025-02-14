package com.medfactor.factorusers.repos;

import com.medfactor.factorusers.entities.User;
import com.medfactor.factorusers.entities.VerficationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerficationTokenRepository extends JpaRepository<VerficationToken,Long> {
    Optional<VerficationToken> findByToken(String token);
    Optional<VerficationToken> findByUser(User user);

    void deleteAllByUser(User user);
}
