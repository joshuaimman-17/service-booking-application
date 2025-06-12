package com.team10.servicebooking.login.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.team10.servicebooking.login.model.Users;
import com.team10.servicebooking.login.repository.UserRepository;
import com.team10.servicebooking.login.service.EmailService;
import com.team10.servicebooking.login.utils.JwtTokenUtil;

@Controller
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    // View endpoints
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/index")
    public String home() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Optional<Users> userOptional = userRepository.findByUsername(username);

            if (userOptional.isPresent()) {
                String role = userOptional.get().getRole();
                if ("ADMIN".equalsIgnoreCase(role)) {
                    return "redirect:/admin/dashboard";
                } else {
                    return "redirect:/services";
                }
            }
        }

        return "redirect:/auth/login";
    }

    // New method to handle role-based redirection after login
    @GetMapping("/login-success")
    public String loginSuccess() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            Optional<Users> userOptional = userRepository.findByUsername(username);

            if (userOptional.isPresent()) {
                String role = userOptional.get().getRole();
                if ("ADMIN".equalsIgnoreCase(role)) {
                    return "redirect:/admin/dashboard";
                } else if ("PROVIDER".equalsIgnoreCase(role)) {
                return "redirect:/provider/dashboard";
                    
                } else {
                    return "redirect:/services";
                }
            }
        }

        return "redirect:/auth/login";
    }

    // Registration endpoint
    @PostMapping(value = "/register", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody Users user) {
        return registerUser(user);
    }

    // Additional endpoint to match form submission
    @PostMapping(value = "/signup", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<?> signupUser(@RequestBody Users user) {
        return registerUser(user);
    }

    private ResponseEntity<?> registerUser(Users user) {
        Map<String, String> response = new HashMap<>();

        try {
            // Validate input
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                response.put("message", "Username is required");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                response.put("message", "Email is required");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                response.put("message", "Password is required");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Check if user exists by email
            Users existingUserByEmail = userRepository.findByEmail(user.getEmail());
            if (existingUserByEmail != null) {
                if (existingUserByEmail.isVerified()) {
                    response.put("message", "Email already registered");
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                } else {
                    // Resend verification email
                    String verificationToken = jwtUtil.generateToken(existingUserByEmail.getEmail());
                    existingUserByEmail.setVerificationToken(verificationToken);
                    userRepository.save(existingUserByEmail);

                    emailService.sendVerificationEmail(existingUserByEmail.getEmail(), verificationToken);

                    response.put("message", "Verification email resent. Please check your inbox.");
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            }

            // Check if username is taken
            if (userRepository.existsByUsername(user.getUsername())) {
                response.put("message", "Username already taken");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Set creation timestamp
            user.setDoac(System.currentTimeMillis());

            // ✅ Assign role before saving
            user.assignRoleBasedOnEmailOrUsername();

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Generate verification token
            String verificationToken = jwtUtil.generateToken(user.getEmail());
            user.setVerificationToken(verificationToken);
            user.setVerified(false);

            // Save user to database
            Users savedUser = userRepository.save(user);

            if (savedUser == null || savedUser.getId() == null) {
                response.put("message", "Failed to save user data");
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Send verification email
            emailService.sendVerificationEmail(user.getEmail(), verificationToken);

            response.put("message", "Registration successful! Please verify your email.");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Registration failed: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Email verification endpoint
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            Users user = userRepository.findByEmail(email);

            if (user == null || user.getVerificationToken() == null) {
                return "redirect:/auth/login?error=invalid_token";
            }

            if (!jwtUtil.validateToken(token) || !user.getVerificationToken().equals(token)) {
                return "redirect:/auth/login?error=invalid_token";
            }

            user.setVerificationToken(null);
            user.setVerified(true);
            userRepository.save(user);

            return "redirect:/auth/login?verified=true";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/auth/login?error=verification_error";
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> user) {
        try {
            String username = user.get("username");
            String password = user.get("password");

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            String token = jwtUtil.generateToken(username);

            // Fetch user details from repository
            Optional<Users> userOptional = userRepository.findByUsername(username);

            // Prepare response map
            Map<String, String> response = new HashMap<>();
            response.put("token", token);

            // If user found, send POST request to /services with user id and name
            if (userOptional.isPresent()) {
                Users foundUser = userOptional.get();

                // Prepare headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Prepare body with user id and name
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("id", foundUser.getId());
                requestBody.put("name", foundUser.getUsername());

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                try {
                    String servicesUrl = "http://localhost:8082/services";
                    restTemplate.postForEntity(servicesUrl, request, String.class);
                } catch (Exception e) {
                    // Log error but do not fail login
                    e.printStackTrace();
                }
            }

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }


}
