package com.github.coco.util;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * JWT authentication
 *
 * @author db1995
 */
public final class JwtUtil {
    private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static Jws<Claims> claims;

    private JwtUtil() {
    }

    /**
     * Give token to the identity passed by the certificate
     *
     * @param subject
     * @return
     */
    public static String generateToken(String subject) {
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));
        String token = null;
        try {
            token = Jwts.builder()
                    .claim("exp", now + 60 * 60 * 8)
                    .signWith(SignatureAlgorithm.HS256, "https://github.com/db1995/coco".getBytes("UTF-8"))
                    .setSubject(subject)
                    .compact();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.info("Token authentication succeeded: " + token);
        return token;
    }

    /**
     * Check if the token is valid
     *
     * @param token
     * @return
     */
    public static boolean checkToken(String token) {
        try {
            JwtUtil.claims = Jwts.parser()
                    .setSigningKey("https://github.com/db1995/coco".getBytes("UTF-8"))
                    .parseClaimsJws(token);
            logger.info("Valid token");
            return true;
        } catch (SignatureException e) {
            logger.warn("Invalid token");
        } catch (ExpiredJwtException e) {
            logger.warn("Invalid token: expired");
        } catch (Exception e) {
            logger.warn("Invalid token: other reason");
        }
        return false;
    }

    public static Jws<Claims> getClaims() {
        return claims;
    }
}