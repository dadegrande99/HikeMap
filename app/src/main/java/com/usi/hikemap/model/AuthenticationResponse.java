package com.usi.hikemap.model;

import androidx.annotation.NonNull;

public class AuthenticationResponse {

    private boolean success;
    private String message;

    public AuthenticationResponse() {

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }

}
