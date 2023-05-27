package com.frontendsourcecode.expenser.model;

public class User {
    String id;
    String phone;
    String name;
    String email;
    String image;

    public User() {
    }

    public User(String id, String phone) {
        this.id = id;
        this.phone = phone;
    }

    public User(String name, String email, String image) {
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
