package com.vmsac.vmsacserver.security.services;

import com.vmsac.vmsacserver.exception.TokenRefreshException;
import com.vmsac.vmsacserver.model.RefreshToken;
import com.vmsac.vmsacserver.model.User;
import com.vmsac.vmsacserver.repository.RefreshTokenRepository;
import com.vmsac.vmsacserver.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link RefreshTokenService} using Mockito.
 *
 * Covers:
 * <ul>
 *   <li>{@code createRefreshToken} — produces a UUID-format token (36 chars) with a
 *       future expiry date, persisted via {@code save()}</li>
 *   <li>{@code verifyExpiration} — extends the expiry of a live token and saves it;
 *       throws {@link com.vmsac.vmsacserver.exception.TokenRefreshException} and deletes
 *       an expired token</li>
 *   <li>{@code checkExpiration} — read-only variant: does NOT extend expiry for live
 *       tokens; throws and deletes for expired tokens</li>
 * </ul>
 *
 * No Spring context is loaded — pure unit tests with mocked repositories.
 */
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void createRefreshToken_savesTokenWithUUID() {
        User user = mock(User.class);
        when(userRepository.findByDeletedFalseAndEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken("test@example.com");

        assertNotNull(token.getToken());
        // Token should be a UUID (36 chars with dashes)
        assertEquals(36, token.getToken().length());
        assertNotNull(token.getExpiryDate());
        assertTrue(token.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository).save(any());
    }

    @Test
    void verifyExpiration_validToken_extendsExpiryAndSaves() {
        RefreshToken token = new RefreshToken();
        token.setToken("valid-uuid");
        Instant originalExpiry = Instant.now().plusSeconds(300);
        token.setExpiryDate(originalExpiry);
        when(refreshTokenRepository.save(any())).thenReturn(token);

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        // Expiry must be extended beyond the original
        assertTrue(result.getExpiryDate().isAfter(originalExpiry));
        verify(refreshTokenRepository).save(token);
    }

    @Test
    void verifyExpiration_expiredToken_throwsAndDeletes() {
        RefreshToken token = new RefreshToken();
        token.setToken("expired-uuid");
        token.setExpiryDate(Instant.now().minusSeconds(1));

        assertThrows(TokenRefreshException.class,
                () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository).delete(token);
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void checkExpiration_validToken_doesNotExtendExpiry() {
        RefreshToken token = new RefreshToken();
        token.setToken("valid-uuid");
        Instant expiry = Instant.now().plusSeconds(300);
        token.setExpiryDate(expiry);

        RefreshToken result = refreshTokenService.checkExpiration(token);

        assertEquals(expiry, result.getExpiryDate()); // unchanged
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void checkExpiration_expiredToken_throwsAndDeletes() {
        RefreshToken token = new RefreshToken();
        token.setToken("expired-uuid");
        token.setExpiryDate(Instant.now().minusSeconds(1));

        assertThrows(TokenRefreshException.class,
                () -> refreshTokenService.checkExpiration(token));
        verify(refreshTokenRepository).delete(token);
    }
}
