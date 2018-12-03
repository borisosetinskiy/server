/*
 * Decompiled with CFR 0_132.
 */
package com.ob.server;

public class UserDetails {
    private String username;
    private String password;

    public UserDetails(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}

