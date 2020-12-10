package com.example.yidnek.amguesthouse;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Date;

/**
 * Created by Yidnek on 6/27/2018.
 */

public class TodayRecord {
    String roomNo,reserveName,reserveUser,mkey;
    public Object reserveDate;
    long reserveShort;
    long reserveLong;

    public TodayRecord() {
    }


    public TodayRecord(String roomNo, long reserveShort, long reserveLong, String reserveName, String reserveUser) {
        this.roomNo = roomNo;
        this.reserveShort = reserveShort;
        this.reserveLong = reserveLong;
        this.reserveName = reserveName;
        this.reserveUser = reserveUser;
        this.reserveDate = ServerValue.TIMESTAMP;

    }

    public String getRoomNo() {
        return roomNo;
    }

    public long getReserveShort() {
        return reserveShort;
    }

    public long getReserveLong() {
        return reserveLong;
    }

    public String getReserveName() {
        return reserveName;
    }

    public String getReserveUser() {
        return reserveUser;
    }

    @Exclude
    public long getReserveDate() {
        return (long) reserveDate;
    }

    @Exclude
    public String getTodayRecordKey(){
        return  mkey;
    }
    public void setTodayRecordKey(String key){
        mkey = key;
    }
}
