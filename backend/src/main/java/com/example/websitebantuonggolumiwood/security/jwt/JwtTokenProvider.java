package com.example.websitebantuonggolumiwood.security.jwt;

import com.example.websitebantuonggolumiwood.security.model.UserPrincipal;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Lớp cung cấp các chức năng tạo, giải mã và xác thực JWT token.
 * Sử dụng thư viện jjwt (io.jsonwebtoken) để xử lý JWT.
 */
@Component
public class JwtTokenProvider {

    /**
     * Bí mật dùng để ký token, lấy từ cấu hình (application.properties hoặc application.yml).
     * Nên là chuỗi dài, phức tạp để đảm bảo an toàn bảo mật.
     */
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    /**
     * Thời gian hết hạn token tính theo mili giây.
     * Ví dụ: 3600000 = 1 giờ
     */
    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    /**
     * Tạo JWT token dựa trên thông tin UserPrincipal.
     * Token chứa thông tin userId trong phần "subject".
     * Thiết lập thời gian phát hành và hết hạn, đồng thời ký bằng thuật toán HS512.
     *
     * @param userPrincipal đối tượng chứa thông tin user đã đăng nhập.
     * @return token JWT dạng String.
     */
    public String generateToken(UserPrincipal userPrincipal) {
        Date now = new Date(); // thời gian hiện tại
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs); // thời gian hết hạn token

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getUserId())) // userId được lưu trong subject
                .setIssuedAt(now) // thời điểm phát hành token
                .setExpiration(expiryDate) // thời điểm hết hạn token
                .signWith(SignatureAlgorithm.HS512, jwtSecret) // ký token với thuật toán HS512 và secret
                .compact(); // tạo chuỗi token
    }

    /**
     * Giải mã JWT token để lấy userId (được lưu trong subject).
     *
     * @param token token JWT
     * @return userId dạng Long
     */
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret) // sử dụng secret để giải mã token
                .parseClaimsJws(token) // parse và xác thực token
                .getBody(); // lấy phần payload (claims)

        return Long.parseLong(claims.getSubject()); // subject chứa userId dạng string, chuyển về Long
    }

    /**
     * Kiểm tra tính hợp lệ của token JWT.
     * Nếu token hợp lệ thì trả về true,
     * Nếu không hợp lệ sẽ ném ra các exception phù hợp.
     *
     * @param token token JWT cần kiểm tra
     * @return true nếu token hợp lệ
     * @throws JwtException các lỗi token như sai chữ ký, hết hạn, không hợp lệ, ...
     */
    public boolean validateToken(String token) {
        try {
            // parse token để xác thực chữ ký, thời hạn...
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true; // token hợp lệ
        } catch (SignatureException ex) {
            throw new JwtException("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            throw new JwtException("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            throw new JwtException("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            throw new JwtException("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            throw new JwtException("JWT claims string is empty.");
        }
    }
}
