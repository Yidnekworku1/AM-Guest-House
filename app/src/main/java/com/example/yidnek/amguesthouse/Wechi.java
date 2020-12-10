package com.example.yidnek.amguesthouse;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;

/**
 * Created by Yidnek on 6/28/2018.
 */

public class Wechi {
    String item,name;
    String mkey;
    public Object timestamp;
    long price;

    public Wechi() {
    }

    public Wechi(String item, long price ,String name) {
        this.item = item;
        this.price = price;
        this.name = name;
//        this.date = date;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getItem() {
        return item;
    }

    public long getPrice() {return price;}

    public String getName(){
        return name;
    }

    @Exclude
    public long getDate(){return (long) timestamp;}

    @Exclude
    public String getKey(){
        return  mkey;
    }
    public void setKey(String key){
        mkey = key;
    }
}
