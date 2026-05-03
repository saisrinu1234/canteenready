package com.example.canteen.login;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
     Optional<RefreshToken> findByUser(User1 user);

    void deleteByUser(User1 user);
}