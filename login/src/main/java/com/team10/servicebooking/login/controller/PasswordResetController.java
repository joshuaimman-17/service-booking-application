package com.team10.servicebooking.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.team10.servicebooking.login.model.Users;
import com.team10.servicebooking.login.repository.UserRepository;
import com.team10.servicebooking.login.service.EmailService;
import com.team10.servicebooking.login.utils.JwtTokenUtil;

@Controller
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get mapping for forgot password page
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token) {
        // Validate token before showing the form
        try {
            String email = jwtUtil.extractEmail(token);
            Users user = userRepository.findByEmail(email);

            if (user == null || user.getResetToken() == null ||
                    !user.getResetToken().equals(token) || !jwtUtil.validateToken(token)) {
                return "redirect:/auth/login?error=invalid_token";
            }

            return "reset-password";
        } catch (Exception e) {
            return "redirect:/auth/login?error=invalid_token";
        }
    }

    // Process forgot password request
    @PostMapping("/forgot-password")
    @ResponseBody
    public ResponseEntity<String> processForgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            String email = request.getEmail();
            Users user = userRepository.findByEmail(email);

            if (user == null) {
                // For security reasons, don't reveal whether the email exists
                return new ResponseEntity<>("Password reset instructions sent if email exists", HttpStatus.OK);
            }

            // Generate reset token
            String resetToken = jwtUtil.generateToken(email);
            user.setResetToken(resetToken);
            userRepository.save(user);

            // Send password reset email
            emailService.sendForgotPasswordEmail(email, resetToken);

            return new ResponseEntity<>("Password reset instructions sent to your email", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Process password reset
    @PostMapping("/reset-password")
    @ResponseBody
    public ResponseEntity<String> processResetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            String token = request.getToken();
            String email = jwtUtil.extractEmail(token);
            Users user = userRepository.findByEmail(email);

            if (user == null || user.getResetToken() == null ||
                    !user.getResetToken().equals(token) || !jwtUtil.validateToken(token)) {
                return new ResponseEntity<>("Invalid or expired token", HttpStatus.BAD_REQUEST);
            }

            // Validate password
            if (request.getPassword().length() < 8) {
                return new ResponseEntity<>("Password must be at least 8 characters", HttpStatus.BAD_REQUEST);
            }

            // Update password and clear reset token
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setResetToken(null);
            userRepository.save(user);

            return new ResponseEntity<>("Password has been reset successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing password reset", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Request classes
    public static class ForgotPasswordRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class ResetPasswordRequest {
        private String token;
        private String password;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}