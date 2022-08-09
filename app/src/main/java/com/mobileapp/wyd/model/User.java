package com.mobileapp.wyd.model;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{
    String id;
    String name;
    String email;
    String image;
    String description;
    String phone;

    public User()
    {
        this.name="";
        this.email="";
        this.phone="";
        this.description="";
        this.image="";
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
