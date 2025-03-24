package g06.ecnu.heartbridge.utils;

import g06.ecnu.heartbridge.exception.InvalidTokenException;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * <p>
 * Jwt工具类
 * </p>
 *
 * @author Tennsai Minamoto
 * @since 2025/3/12
 */
public class JwtUtil {
    private static final long EXPIRATION_TIME = 86400000; // 1 天
    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    // 生成 JWT
    public static String generateToken(String username, int userType) {
        return Jwts.builder()
                .subject(username)
                .claim("userType", userType)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // 解析并验证 JWT
    public static Claims parseToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            if (claims.getExpiration().before(new Date())){
                throw new InvalidTokenException("Expired or invalid JWT token");
            }
            return claims;
        }
        catch (Exception e) {
            System.err.println("Error parsing jwt: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
