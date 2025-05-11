package com.medfactor.factorusers.controllers;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.medfactor.factorusers.dtos.*;
import com.medfactor.factorusers.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Value("${cookie.maxAge}")
    private int maxAge;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = userService.login(loginRequest);
        Cookie cookie = new Cookie("JWT_TOKEN", loginResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true); // Change to false if not using HTTPS
        cookie.setDomain("localhost");
        response.addCookie(cookie);
        return ResponseEntity.ok(loginResponse);
    }
    @PostMapping("/loginMobile")
    public ResponseEntity<LoginMobileResponse> loginMobile(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginMobileResponse loginResponse = userService.loginMobile(loginRequest);
        Cookie cookie = new Cookie("JWT_TOKEN", loginResponse.getToken());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true); // Change to false if not using HTTPS
        cookie.setDomain("localhost");
        response.addCookie(cookie);
        System.out.println("Cookie set with token: " + loginResponse.getToken());
        return ResponseEntity.ok(loginResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        Cookie cookie = new Cookie("JWT_TOKEN", null); // Clear the cookie
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Set expiration to 0 to delete the cookie
        cookie.setSecure(true); // or false depending on your setup
        cookie.setDomain("localhost"); // Same as when you set it
        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/reset-password-email")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email"); // Extract email from JSON
        System.out.println("Received email: " + email); // Debugging log

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        userService.codeVerficationCreation(email);
        return ResponseEntity.ok("Email sent");
    }
    @PostMapping("/confirm-code")
    public ResponseEntity<?> confirmCode(@RequestBody VerifCodeRequest verifCodeRequest) {
        String email = verifCodeRequest.getEmail();
        String code = verifCodeRequest.getCode();
        System.out.println("Received email: " + email); // Debugging log
        System.out.println("Received code: " + code); // Debugging log

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        if (code == null || code.isEmpty()) {
            return ResponseEntity.badRequest().body("Code is required");
        }

        if (userService.verifyCode(email, code)) {
            return ResponseEntity.ok("Code is correct");
        } else {
            return ResponseEntity.badRequest().body("Code is incorrect");
        }

    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetRequest resetRequest) {
        userService.changePassword(resetRequest);
        return ResponseEntity.ok("Password changed");
    }
    @PostMapping("/change-password-first-time")
    public ResponseEntity<?> changePasswordFirstTime(@RequestBody ResetFirstTimeRequest resetRequest, Principal principal) {
        resetRequest.setEmail(principal.getName());
        userService.changePasswordFirstTime(resetRequest);
        return ResponseEntity.ok("Password changed");
    }
}
