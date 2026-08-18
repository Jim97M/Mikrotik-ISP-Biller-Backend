package com.userservice.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;




@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${token.secret}")
    private String jwtSecret;

    @Value("${token.expiration}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return jwtBuilder(userDetails.getUsername(), jwtExpirationMs, jwtSecret).compact();
    }

    public JwtBuilder jwtBuilder(String jwtSubject, int jwtExp, String jwtSecret){
        return Jwts.builder()
                .setSubject(jwtSubject)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExp))
                .signWith(SignatureAlgorithm.HS512, jwtSecret);
    }

    public String getUserNameFromJwtToken(String token){
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
        String username = claims.getSubject();

        logger.info("Username From JWT Token Is {}", username);

        return username;
    }


    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date((new Date()).getTime()))
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }



    public boolean validateJwtToken(String authToken){

        try {
            Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT Signature: {}", e.getMessage());
            throw new BadCredentialsException("Invalid JWT Signature");
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT Token: {}", e.getMessage());
            throw new BadCredentialsException("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            logger.error("JWT Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is Unsupported: {}", e.getMessage());
            throw new BadCredentialsException("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            logger.error("JWT String Claims is empty", e.getMessage());
            throw new BadCredentialsException("JWT String Claims is empty");
        }
        return false;

    }

    private Date expirationDate(String token){return extractClaim(token, Claims::getExpiration);}

    private <T>T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
    }
}
