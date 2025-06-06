package com.example.websitebantuonggolumiwood.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    // Map lưu tạm OTP với key là email (hoặc phone), value là thông tin OTP + thời gian hết hạn
    private Map<String, OtpInfo> otpStorage = new ConcurrentHashMap<>();

    /**
     * Sinh mã OTP (6 số), lưu vào otpStorage với thời gian hết hạn 60 giây.
     * @param key: có thể là email hoặc số điện thoại
     * @return mã OTP sinh ra
     */
    public String generateOtp(String key) {
        // Random mã OTP gồm 6 chữ số (có thể có số 0 ở đầu)
        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        // Lưu vào Map, set thời gian hết hạn là 60 giây kể từ hiện tại
        otpStorage.put(key, new OtpInfo(otp, LocalDateTime.now().plusSeconds(60)));
        return otp;
    }

    /**
     * Kiểm tra mã OTP có hợp lệ và còn thời hạn không.
     * @param key: email hoặc số điện thoại
     * @param otpInput: mã OTP người dùng nhập
     * @return true nếu hợp lệ, false nếu sai hoặc hết hạn
     */
    public boolean validateOtp(String key, String otpInput) {
        OtpInfo info = otpStorage.get(key);
        if (info == null) return false; // Không có OTP tương ứng
        // Kiểm tra hết hạn
        if (LocalDateTime.now().isAfter(info.expiry)) {
            otpStorage.remove(key); // Hết hạn thì xoá luôn
            return false;
        }
        boolean isValid = info.otp.equals(otpInput); // So sánh mã OTP
        if (isValid) otpStorage.remove(key); // Đúng thì xoá OTP (1 lần dùng)
        return isValid;
    }

    /**
     * Class lưu thông tin OTP và thời gian hết hạn.
     */
    private static class OtpInfo {
        String otp;
        LocalDateTime expiry;
        OtpInfo(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }
}
