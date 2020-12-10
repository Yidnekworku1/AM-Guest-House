package com.example.yidnek.amguesthouse;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Yidnek on 7/6/2018.
 */

public class CalculateDay {
    Object timestamp ;
    String key;
    private long lastCreatedDate;

    public CalculateDay() {
        this.timestamp = ServerValue.TIMESTAMP;
    }

    @Exclude


    public void setCurrentDate(){
        DatabaseReference currDate = FirebaseDatabase.getInstance().getReference("CurrentDate");
        currDate.removeValue();
        String id = currDate.push().getKey();
        currDate.child("lastDate").setValue(timestamp);
    }

}
