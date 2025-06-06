package com.example.websitebantuonggolumiwood.exception;

/**
 * Exception ném ra khi không tìm thấy địa chỉ của người dùng
 */
public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String message) {
        super(message);
    }
}
