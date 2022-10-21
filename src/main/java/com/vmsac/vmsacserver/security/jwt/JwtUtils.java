package com.vmsac.vmsacserver.security.jwt;

import com.vmsac.vmsacserver.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // create your own fixed secret and place here
    private Key jwtSecret;

    // jwt token expiry time - currently 15 mins
    private Long jwtTokenDurationMs= Long.valueOf(900000);

    @PostConstruct
    public void init() {
        jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateJwtToken(UserDetailsImpl userPrincipal) {

        ArrayList role_names = (ArrayList) userPrincipal.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return generateTokenFromEmail(userPrincipal.getUsername(), role_names);
    }

    public String generateTokenFromEmail(String email, ArrayList role_names) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", role_names);
        return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtTokenDurationMs)).signWith(jwtSecret)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
