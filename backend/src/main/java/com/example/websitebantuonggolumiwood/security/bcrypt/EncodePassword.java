package com.example.websitebantuonggolumiwood.security.bcrypt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodePassword {
    public static void main(String[] args) {
        String rawPassword = "admin";
        String hashed = new BCryptPasswordEncoder().encode(rawPassword);
        System.out.println(hashed);
    }
}
