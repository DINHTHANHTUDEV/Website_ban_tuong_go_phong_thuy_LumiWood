package com.example.websitebantuonggolumiwood.exception;

/**
 * Exception ném ra khi bậc khách hàng (tier) không hợp lệ
 */
public class InvalidTierException extends RuntimeException {
    public InvalidTierException(String message) {
        super(message);
    }
}
