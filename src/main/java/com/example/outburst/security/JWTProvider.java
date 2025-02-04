package com.example.outburst.security;


import com.example.outburst.model.entity.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTProvider {

    public static final Logger LOG = LoggerFactory.getLogger(JWTProvider.class);


    public String generateToken(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expiryTime = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);
        String userId = Long.toString(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .setSubject(userId)
                .addClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryTime)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SecurityConstants.SECRET)
                    .parseClaimsJws(token);
            return true;
        }
        catch (
                SignatureException |
                MalformedJwtException |
                ExpiredJwtException |
                UnsupportedJwtException |
                IllegalArgumentException ex){

            LOG.error(ex.getMessage());
            return false;
        }
    }


    public long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.SECRET)
                .parseClaimsJws(token)
                .getBody();
        String userId = (String) claims.get("id");

        return Long.parseLong(userId);
    }
}
