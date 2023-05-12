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

//    // jwt token expiry time - currently 15 mins
//    private Long jwtTokenDurationMs= Long.valueOf(15 * 60 * 1000);

    // jwt token expiry time - currently 20 secs
    private Long jwtTokenDurationMs= Long.valueOf(1 * 20 * 1000);

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
//
//    public void extendAccessTokenValidity(String token) {
//        System.out.println("extended");
//        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
//        claims.setExpiration(new Date(System.currentTimeMillis() + jwtTokenDurationMs)); // extend by jwtTokenDurationMs
//    }

    public Map<String, String> validateJwtToken (String authToken) {
        Map<String, String> res = new HashMap<>();
        res.put("status", "false");
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            res.put("status", "true");
        }
//        try {
//            // Parse the token and get the email and roles
//            Jws<Claims> parsedToken = Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
//            String email = parsedToken.getBody().getSubject();
//            ArrayList role_names = (ArrayList) parsedToken.getBody().get("roles", ArrayList.class);
//
//            // Generate a new token with the same email and roles, but with an extended expiration time
//            String extendedToken = generateTokenFromEmail(email, role_names);
//
//            res.put("status", "true");
//            res.put("token", extendedToken); // Return the extended token in the response
//        }
        catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            res.put("exception", "Invalid JWT signature");
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            res.put("exception", "Invalid JWT token");
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            res.put("exception", "JWT token is expired");
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
            res.put("exception", "JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            res.put("exception", "JWT claims string is empty");
        }
        return res;
    }

}
