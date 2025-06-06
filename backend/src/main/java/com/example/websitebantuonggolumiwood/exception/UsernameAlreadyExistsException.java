package com.example.websitebantuonggolumiwood.exception;

/**
 * Exception ném ra khi username đã tồn tại trong hệ thống.
 * Giúp tách biệt lỗi nghiệp vụ đăng ký.
 */
public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
