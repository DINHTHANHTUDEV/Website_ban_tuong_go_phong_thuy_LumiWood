package com.example.websitebantuonggolumiwood.security.jwt;

import com.example.websitebantuonggolumiwood.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter này được gọi mỗi lần có request tới server.
 * Nhiệm vụ chính là lấy token JWT từ header, validate token,
 * nếu hợp lệ thì lấy thông tin user từ token để xác thực người dùng.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor inject 2 bean: JwtTokenProvider để xử lý token,
     * UserDetailsServiceImpl để load thông tin user dựa trên userId.
     */
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, UserDetailsServiceImpl userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Phương thức xử lý chính của filter với mỗi request.
     * Kiểm tra header Authorization lấy token JWT, nếu hợp lệ
     * sẽ xác thực user và đặt Authentication vào SecurityContext.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // Lấy token JWT từ header Authorization (nếu có)
            String jwt = getJwtFromRequest(request);
            System.out.println("JWT token: " + jwt);

            // Nếu token tồn tại và hợp lệ
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Lấy userId từ token (được lưu trong subject của JWT)
                Long userId = tokenProvider.getUserIdFromJWT(jwt);
                System.out.println("UserId from token: " + userId);

                // Tải thông tin user từ DB theo userId
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                System.out.println("Loaded UserDetails username: " + userDetails.getUsername());

                // Tạo Authentication dựa trên userDetails (bao gồm roles, quyền hạn)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Gán thông tin chi tiết request vào Authentication (như IP, session)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đặt Authentication vào SecurityContext để các phần tiếp theo của Spring Security
                // biết user này đã được xác thực thành công
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Nếu xảy ra lỗi (token không hợp lệ, user không tồn tại,...)
            // Trả về lỗi 401 Unauthorized với thông báo lỗi
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
            return; // kết thúc filter chain, không cho tiếp tục request
        }

        // Cho phép các filter tiếp theo xử lý request
        filterChain.doFilter(request, response);
    }

    /**
     * Hàm lấy token JWT từ header Authorization của HTTP request.
     * Header có dạng: "Authorization: Bearer <token>"
     * Nếu tồn tại và đúng định dạng, trả về chuỗi token.
     * Nếu không, trả về null.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        // Kiểm tra header có giá trị và bắt đầu bằng "Bearer "
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            // Cắt bỏ phần "Bearer " trả về token thuần túy
            return bearer.substring(7);
        }
        // Không có token hợp lệ
        return null;
    }
}
