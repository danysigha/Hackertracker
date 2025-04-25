package com.hackertracker.security.config;

import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    private final UserDAO userDao;

    public JwtService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        User user = userDao.getUserByUserName(userDetails.getUsername());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("tokenVersion", user.getTokenVersion())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigninKey())
                .compact();
    }

     public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

         // Get the token version from the JWT
         Integer tokenVersion = extractClaim(token, claims -> claims.get("tokenVersion", Integer.class));

         // Get the user from your DAO
         User user = userDao.getUserByUserName(username);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && tokenVersion != null && tokenVersion == user.getTokenVersion();
     }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigninKey() {
        byte[] bytes = Base64.getDecoder()
                .decode(secretKey.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(bytes, "HmacSHA256");
    }
}
