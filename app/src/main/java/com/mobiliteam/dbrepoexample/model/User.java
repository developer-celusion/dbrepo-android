package com.mobiliteam.dbrepoexample.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by administrator on 20/12/17.
 */
@DatabaseTable
public class User {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String userName;

    @DatabaseField
    private String email;

    @DatabaseField
    private String mobile;

    public User() {
    }

    public User(String name, String userName, String email, String mobile) {
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }

}
