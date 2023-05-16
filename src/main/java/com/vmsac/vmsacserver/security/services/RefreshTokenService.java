package com.vmsac.vmsacserver.security.services;

import com.vmsac.vmsacserver.exception.TokenRefreshException;
import com.vmsac.vmsacserver.model.RefreshToken;
import com.vmsac.vmsacserver.repository.RefreshTokenRepository;
import com.vmsac.vmsacserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    // change the expiry time of refreshToken - currently 15mins
    private Long refreshTokenDurationMs= Long.valueOf(15 * 60 * 1000);

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(String email) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findByDeletedFalseAndEmail(email).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshTokenRepository.save(token);
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
