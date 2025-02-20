package com.jwtauth.jwtauth.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);

    private final SecretKey secretKey;

    public JWTService() {
        try {
            logger.info("Initializing JWTService and generating secret key...");
            SecretKey k = KeyGenerator.getInstance("HmacSHA256").generateKey();
            secretKey = Keys.hmacShaKeyFor(k.getEncoded());
            logger.info("Secret key successfully generated.");

        }catch (Exception e){
            logger.error("Error generating secret key for JWTService", e);
            throw new RuntimeException(e);
        }
    }

    public String getJWTToken(String username, Map<String, Object> claims) {

        logger.info("Generating JWT token for user: {}", username);

        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*15))// 15 minutes expiration
                .signWith(secretKey)
                .compact();
        logger.info("JWT token generated successfully for user: {}", username);
        return token;
    }



    //want to get username from the token
    public String getUserName(String token) {
      Claims data =getTokenData(token);
              if(data == null) return null;
              return data.getSubject();
    }

    public Object getFieldFromToken(String token, String key) {
        Claims data = getTokenData(token);
        if(data == null) return null;
        return data.get(key);
    }


    private Claims getTokenData(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e){
            return null;
        }
    }



}
