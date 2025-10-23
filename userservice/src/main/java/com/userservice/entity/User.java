package com.userservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.Table;
import lombok.*;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 254, unique = true)
    private String email;

    @Column(length = 254, unique = false)
    private String username;

    @Column(name = "firstname", length = 254, unique = false)
    private String firstname;

    @Column(name = "lastname", length = 254, unique = false)
    private String lastname;

    @Column(nullable = false)
    private boolean activated = false;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(name = "password_hash", length = 60, nullable = false)
    private String password;

    @Column(name = "phone_number", length = 60, nullable = true)
    private String phoneNumber;

    private String userType;

    @Column(name = "first_authentication", nullable = true)
    private Boolean first_authentication;

    @Column(name = "initial_password", nullable = true)
    private Boolean initial_password;


    @Column(name = "verification_otp", length = 60, nullable = true)
    private String verificationOtp;

    @Column(name = "forgot_password_otp", length = 60, nullable = true)
    private String forgotPasswordOtp;

    @Column(name = "otp_created_at", length = 60, nullable = true)
    private Long otpCreatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles_mapping",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<UserRoles> roles = new ArrayList<>();

    @Column(name = "verification_otp_created", length = 60, nullable = true)
    private Long verificationOtpCreatedAt;

    @Column(name = "reset_password_otp_created", length = 60, nullable = true)
    private Long resetPasswordOtpCreatedAt;

    @Transient
    private String rawPassword;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public static class UserBuilder {
        private String email;
        private String password;
        private String rawPassword;
        private String username;
        private String firstname;
        private String lastname;
        private boolean activated = false;
        private Boolean first_authentication;
        private Boolean initial_password;
        private List<UserRoles> roles;
        private String phoneNumber;
        private String userType;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }
        public UserBuilder rawPassword(String rawPassword){
            this.rawPassword = rawPassword;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder firstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public UserBuilder lastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public UserBuilder activated(boolean activated) {
            this.activated = activated;
            return this;
        }

        public UserBuilder first_authentication(Boolean first_authentication) {
            this.first_authentication = first_authentication;
            return this;
        }

        public UserBuilder initial_password(Boolean initial_password) {
            this.initial_password = initial_password;
            return this;
        }

        public UserBuilder roles(List<UserRoles> roles){
            this.roles = roles;
            return this;
        }

        public UserBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }



        public UserBuilder userType(String userType) {
            this.userType = userType;
            return this;
        }


        public UserBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setRawPassword(rawPassword);
            user.setUsername(username);
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setActivated(activated);
            user.setFirst_authentication(first_authentication);
            user.setInitial_password(initial_password);
            user.setRoles(roles);
            user.setPhoneNumber(phoneNumber);
            user.setUserType(userType);
            user.setCreatedAt(createdAt);
            user.setUpdatedAt(updatedAt);
            return user;
        }
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getForgotPasswordOtp() {
        return forgotPasswordOtp;
    }

    public void setForgotPasswordOtp(String forgotPasswordOtp) {
        this.forgotPasswordOtp = forgotPasswordOtp;
    }

    public Long getResetPasswordOtpCreatedAt() {
        return resetPasswordOtpCreatedAt;
    }

    public void setResetPasswordOtpCreatedAt(Long resetPasswordOtpCreatedAt) {
        this.resetPasswordOtpCreatedAt = resetPasswordOtpCreatedAt;
    }

    public String getRawPassword() {
        return rawPassword;
    }

    public void setRawPassword(String rawPassword) {
        this.rawPassword = rawPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Boolean getFirst_authentication() {
        return first_authentication;
    }

    public void setFirst_authentication(Boolean first_authentication) {
        this.first_authentication = first_authentication;
    }

    public Boolean getInitial_password() {
        return initial_password;
    }

    public void setInitial_password(Boolean initial_password) {
        this.initial_password = initial_password;
    }


    public String getVerificationOtp() {
        return verificationOtp;
    }

    public void setVerificationOtp(String verificationOtp) {
        this.verificationOtp = verificationOtp;
    }

    public Long getOtpCreatedAt() {
        return otpCreatedAt;
    }

    public void setOtpCreatedAt(Long otpCreatedAt) {
        this.otpCreatedAt = otpCreatedAt;
    }

    public List<UserRoles> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRoles> roles) {
        this.roles = roles;
    }

    public Long getVerificationOtpCreatedAt() {
        return verificationOtpCreatedAt;
    }

    public void setVerificationOtpCreatedAt(Long verificationOtpCreatedAt) {
        this.verificationOtpCreatedAt = verificationOtpCreatedAt;
    }



    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


}