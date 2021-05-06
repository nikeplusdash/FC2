package com.foodmgmt.dontstarve.onboarding;

public class Users {
    public String email, regno, name;
    public Boolean verified;

    public Users() {
    }

    public Users(String email, String regno, String name, Boolean verified) {
        this.email = email;
        this.regno = regno;
        this.name = name;
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}