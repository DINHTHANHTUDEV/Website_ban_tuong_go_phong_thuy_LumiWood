package com.example.websitebantuonggolumiwood.exception;

import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler dùng để xử lý toàn bộ lỗi phát sinh trong ứng dụng.
 * Gồm lỗi của module AUTH, USERPROFILE, và USERMANAGEMENT.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // ========================== MODULE AUTH ==============================

    /**
     * Xử lý lỗi validate DTO của module auth, usermanagement (annotation @NotBlank, @Size...)
     * Áp dụng cho @Valid trong @RequestBody
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý lỗi validate từ @Validated (cho @RequestParam, @PathVariable)
     * Áp dụng cho các tham số như userId, page
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations()
                .forEach(violation -> errors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý lỗi khi username đã tồn tại (đăng ký tài khoản)
     */
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameExistsException(UsernameAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý lỗi xác thực sai username/password
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return new ResponseEntity<>("Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Xử lý lỗi không có quyền truy cập tài nguyên
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return new ResponseEntity<>("Bạn không có quyền truy cập tài nguyên này", HttpStatus.FORBIDDEN);
    }

    /**
     * Xử lý lỗi JWT token không hợp lệ hoặc hết hạn
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // ========================== MODULE USERPROFILE ==============================

    /**
     * Xử lý lỗi khi không tìm thấy người dùng
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Xử lý lỗi khi mật khẩu không hợp lệ
     */
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPassword(InvalidPasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý lỗi nghiệp vụ liên quan đến hồ sơ người dùng
     */
    @ExceptionHandler(UserProfileException.class)
    public ResponseEntity<String> handleUserProfile(UserProfileException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // ========================== MODULE USERMANAGEMENT ==============================

    /**
     * Xử lý lỗi khi tier không hợp lệ
     */
    @ExceptionHandler(InvalidTierException.class)
    public ResponseEntity<String> handleInvalidTierException(InvalidTierException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Xử lý lỗi khi không tìm thấy địa chỉ của người dùng
     */
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<String> handleAddressNotFoundException(AddressNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // ========================== COMMON / FALLBACK ==============================

    /**
     * Xử lý tất cả các lỗi khác không dự kiến (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Lỗi hệ thống: " + ex.getMessage());
    }
}