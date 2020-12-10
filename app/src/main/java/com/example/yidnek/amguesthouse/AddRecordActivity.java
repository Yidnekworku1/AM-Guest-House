package com.example.yidnek.amguesthouse;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class AddRecordActivity extends AppCompatActivity {
    private EditText roomNumber,tempoRoom,normalRoom,name;
    Button add;
    private ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    DatabaseReference records;
    private String NAME_RECIEVED = "";
    private long total = 0;
    private long lastCreatedDate = 0;

    @Override
    protected void onStart() {
        super.onStart();
        getCurrentGebi();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);

        roomNumber = findViewById(R.id.room_number);
        tempoRoom = findViewById(R.id.tempo_room);
        normalRoom = findViewById(R.id.normal_room);
        name = findViewById(R.id.name);
        add = findViewById(R.id.add);
        progressBar = findViewById(R.id.progressbar1);

        NAME_RECIEVED = getIntent().getStringExtra("USER_NAME");

        firebaseAuth = FirebaseAuth.getInstance();
        records = FirebaseDatabase.getInstance().getReference("Reserves");

        DatabaseReference currDate = FirebaseDatabase.getInstance().getReference("CurrentDate");
        currDate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lastCreatedDate = (long) dataSnapshot.child("lastDate").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        progressBar.setVisibility(View.GONE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);

                final String roomNo = roomNumber.getText().toString().trim();
                final String tempRoom = tempoRoom.getText().toString().trim();
                final String norRoom = normalRoom.getText().toString().trim();
                final String Rname = name.getText().toString().trim();
                Validate(roomNo,tempRoom,norRoom,Rname);
                if ((Validate(roomNo, tempRoom, norRoom, Rname))){
                    if (tempRoom.equals("")){
                        AddRecord(roomNo, 0, Long.parseLong(norRoom), Rname);
                    }
                    else{
                        AddRecord(roomNo, Long.parseLong(tempRoom), 0, Rname);
                    }

                }
            }
        });
    }
    public Boolean Validate(String VroomNo, String VtempRoom, String VnorRoom, String Vrname){

        progressBar.setVisibility(View.GONE);

        if (VroomNo.isEmpty()){
            roomNumber.setError("Room Number Required");
        }
        else if (  VtempRoom.isEmpty()&& VnorRoom.isEmpty()    ){
            tempoRoom.setError("One Of them Required");
            normalRoom.setError("One Of them Required");
        }
        else if ( ! VtempRoom.isEmpty()&& ! VnorRoom.isEmpty()    ){
            tempoRoom.setError("Clear One of them");
            normalRoom.setError("Clear one of them");
        }
        else if (Vrname.isEmpty()){
            name.setError("Name Required");
        }
        else {
            return true;
        }
        return false;
    }

    public void AddRecord(String rNo, long tRoom, long nRoom, String rName) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Reserving...");
        progressDialog.show();
        TodayRecord todayRecord = new TodayRecord(rNo,tRoom,nRoom,rName,NAME_RECIEVED);
        String id = records.push().getKey();
       records.child(id).setValue(todayRecord);


        total = total + todayRecord.getReserveLong();
        total = total + todayRecord.getReserveShort();
        TotalGebi(ConvForFirebaseDate(lastCreatedDate));
        progressDialog.dismiss();
        Toast.makeText(this,"Successfully Reserved",Toast.LENGTH_SHORT).show();

        roomNumber.setText("");
        tempoRoom.setText("");
        normalRoom.setText("");
        name.setText("");

    }
    private void TotalGebi(String date){
        DatabaseReference totalGebi = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        totalGebi.child(date).child("TotalGebi").setValue(total);
        totalGebi.child(date).child("date").setValue(date);
    }
    private String ConvForFirebaseDate(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(time);
        return date;
    }
    private void getCurrentGebi(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        DatabaseReference currGebi = FirebaseDatabase.getInstance().getReference("TotalGebiWechi");
        currGebi.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

               if (dataSnapshot.child(ConvForFirebaseDate(lastCreatedDate)).child("TotalGebi").exists()){
                   total = (long) dataSnapshot.child(ConvForFirebaseDate(lastCreatedDate)).child("TotalGebi").getValue();
                   progressDialog.dismiss();
               }
               else {
                   progressDialog.dismiss();
               }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





}
