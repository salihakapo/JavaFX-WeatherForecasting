package com.example.weatherappdemo;

import java.io.Serializable;

// ObjectOutputStream ile yazılabilmesi için Serializable interface'ini uygulamalıdır!
public class User implements Serializable {
    // Nesne versiyon takibi için serialization kimliği
    private static final long serialVersionUID = 1L;

    private String username;
    private String passwordHash;

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}