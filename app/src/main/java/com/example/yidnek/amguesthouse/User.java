package com.example.yidnek.amguesthouse;

import com.google.firebase.database.Exclude;

/**
 * Created by Yidnek on 6/21/2018.
 */

public class User {
    String name,phone,userType,email ,mkey;
    public User(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public User(String name, String phone, String userType, String email) {
        this.name = name;
        this.phone = phone;

        this.userType = userType;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserType() {
        return userType;
    }

    public String getEmail() {
        return email;
    }

    @Exclude
    public String getUserKey(){
        return  mkey;
    }
    public void setUserKey(String key){
        mkey = key;
    }
}
