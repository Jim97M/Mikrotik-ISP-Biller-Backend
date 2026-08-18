package com.userservice.security.service;

import com.userservice.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = new HashSet<>(authorities);;
    }

    public static UserDetailsImpl build(User user, Collection<? extends GrantedAuthority> authorities) {
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                new HashSet<>(authorities)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Adjust based on your logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Adjust based on your logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Adjust based on your logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Adjust based on your logic
    }

    // Optional: Getter for ID if needed elsewhere
    public Long getId() {
        return id;
    }
}
