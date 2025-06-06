package com.example.websitebantuonggolumiwood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender; // Inject đối tượng gửi mail

    /**
     * Gửi OTP về địa chỉ email chỉ định.
     * @param to  Địa chỉ email nhận OTP
     * @param otp Mã OTP (6 số)
     */
    public void sendOtpEmail(String to, String otp) {
        // Tạo email đơn giản (text)
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to); // Địa chỉ nhận
        message.setSubject("Mã xác thực OTP của bạn"); // Tiêu đề
        message.setText("Mã OTP của bạn là: " + otp + "\nMã này chỉ có hiệu lực trong 60 giây."); // Nội dung

        // Gửi mail đi
        mailSender.send(message);
    }
}
