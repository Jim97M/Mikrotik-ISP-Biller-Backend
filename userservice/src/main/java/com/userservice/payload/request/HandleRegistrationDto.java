package com.userservice.payload.request;


import com.userservice.entity.User;

public class HandleRegistrationDto {
    User user;


    public HandleRegistrationDto(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
