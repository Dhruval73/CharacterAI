package com.vaaniai.characterai.DTO;

public class AuthResponse {
    private String token;
    private int loginCount;

    public AuthResponse(String token, int loginCount) {
        this.token = token;
        this.loginCount = loginCount;
    }

    public String getToken() {
        return token;
    }

    public int getLoginCount(){
        return loginCount;
    }

}