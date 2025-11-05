package com.example.websitebantuonggolumiwood.controller;

import com.example.websitebantuonggolumiwood.dto.LoginRequestDTO;
import com.example.websitebantuonggolumiwood.dto.RegisterRequestDTO;
import com.example.websitebantuonggolumiwood.entity.User;
import com.example.websitebantuonggolumiwood.security.jwt.JwtTokenProvider;
import com.example.websitebantuonggolumiwood.security.model.UserPrincipal;
import com.example.websitebantuonggolumiwood.security.service.UserDetailsServiceImpl;
import com.example.websitebantuonggolumiwood.service.EmailService;
import com.example.websitebantuonggolumiwood.service.OtpService;
import com.example.websitebantuonggolumiwood.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    // Bộ nhớ tạm để lưu thông tin đăng ký cho đến khi xác thực OTP (có thể thay bằng Redis, DB trạng thái...)
    private final Map<String, RegisterRequestDTO> pendingRegisterMap = new ConcurrentHashMap<>();

    /**
     * API đăng nhập: Xác thực và trả về JWT token nếu thành công.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateToken(userPrincipal);

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("role", userPrincipal.getAuthorities().iterator().next().getAuthority());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Tên đăng nhập hoặc mật khẩu không đúng");
        }
    }

    /**
     * Gửi OTP, lưu thông tin đăng ký tạm thời (chưa lưu user vào DB).
     * FE gọi endpoint này khi user điền form đăng ký và ấn "Gửi OTP".
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        // Kiểm tra username đã tồn tại chưa (vẫn giữ, vì lưu trong DB)
        if (userService.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại");
        }
        // KHÔNG kiểm tra email trong DB nữa
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Mật khẩu xác nhận không đúng");
        }
        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email không được để trống");
        }
        // Lưu thông tin đăng ký tạm theo email (ghi đè nếu user đăng ký lại)
        pendingRegisterMap.put(registerRequest.getEmail(), registerRequest);

        // Sinh OTP, gửi email
        String otp = otpService.generateOtp(registerRequest.getEmail());
        emailService.sendOtpEmail(registerRequest.getEmail(), otp);

        return ResponseEntity.ok("OTP đã được gửi tới email: " + registerRequest.getEmail());
    }

    /**
     * Xác thực OTP và chính thức tạo tài khoản trong DB.
     * FE gọi endpoint này khi user nhập đúng OTP.
     * Truyền email và otp trong body (có thể để các trường khác rỗng).
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody RegisterRequestDTO request) {
        boolean valid = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!valid) {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ hoặc đã hết hạn.");
        }
        RegisterRequestDTO pendingUser = pendingRegisterMap.get(request.getEmail());
        if (pendingUser == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy thông tin đăng ký tạm.");
        }
        // Kiểm tra lại username (vẫn bắt buộc) trước khi lưu vào DB
        if (userService.existsByUsername(pendingUser.getUsername())) {
            return ResponseEntity.badRequest().body("Tên đăng nhập đã tồn tại");
        }
        // KHÔNG kiểm tra email ở đây nữa

        User user = new User();
        user.setUsername(pendingUser.getUsername());
        user.setFullName(pendingUser.getFullName());
        user.setPassword(pendingUser.getPassword());
        user.setRole("CUSTOMER");
        // them email vào db khi da dki thanh cong
        user.setEmail(pendingUser.getEmail());

        userService.saveUser(user);
        pendingRegisterMap.remove(request.getEmail());

        return ResponseEntity.ok("Đăng ký thành công!");
    }
}
