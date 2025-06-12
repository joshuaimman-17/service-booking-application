package com.team10.servicebooking.login.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String phonenumber;
    private long dob;
    private long doac;
    private String verificationToken;
    private boolean isVerified;
    private String role;
    @Column(name = "reset_token")
    private String resetToken;

    // ID getter
    public Long getId() {
        return id;
    }

    // Verification token methods
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    // Verified status methods
    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    // Username methods
    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    // Email methods
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Password methods
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Phone number methods
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    // Date of birth methods
    public long getDob() {
        return dob;
    }

    public void setDob(long dob) {
        this.dob = dob;
    }

    // Date of account creation methods
    public long getDoac() {
        return doac;
    }

    public void setDoac(long doac) {
        this.doac = doac;
    }

    // Reset token methods
    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void assignRoleBasedOnEmailOrUsername() {
        String password = this.getpassword().toLowerCase();

        if (password.contains("admin")) {
            this.role = "ADMIN";
        } else if (password.contains("provider")) {
            this.role = "PROVIDER";
        } else {
            this.role = "USER";
        }
    }

    private String getpassword() { return password;
    }


    public String getRole() {
        return role;
    }


}