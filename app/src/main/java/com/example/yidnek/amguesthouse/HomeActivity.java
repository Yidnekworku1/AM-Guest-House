package com.example.yidnek.amguesthouse;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import static com.example.yidnek.amguesthouse.NetworkConfig.isConnected;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;

    Button add_record,liquors,wechi,today_records,history,add_user;
    private TextView userType, userName;
    private ImageView userTypeImage;
    private String userTypeValue ="";
    private final String NAME = "NAME";
    private final String USER = "USER";
    private String userValue ="";
    private  Object currentTimestamp;
    private long  SEND_DATE;
    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        getUserValue();

        add_record = findViewById(R.id.add_record);
        liquors = findViewById(R.id.liquors);
        wechi = findViewById(R.id.wechi);
        today_records = findViewById(R.id.today_records);
        history = findViewById(R.id.history);
        add_user = findViewById(R.id.add_user);

        userName = findViewById(R.id.user_name);
        userType = findViewById(R.id.user_type);
        userTypeImage = findViewById(R.id.user_type_image);

        add_record.setOnClickListener(this);
        liquors.setOnClickListener(this);
        wechi.setOnClickListener(this);
        today_records.setOnClickListener(this);
        history.setOnClickListener(this);
        add_user.setOnClickListener(this);

        userType.setOnClickListener(this);
        userTypeImage.setOnClickListener(this);

//    DeleteAll();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_record:
                Intent add_record = new Intent(HomeActivity.this,AddRecordActivity.class);
                add_record.putExtra("USER_NAME",userValue);
                startActivity(add_record);
                break;
            case R.id.liquors:
                Toast.makeText(HomeActivity.this,"liquors",Toast.LENGTH_SHORT).show();
                break;
            case R.id.wechi:
                Intent wechi = new Intent(HomeActivity.this,WechiActivity.class);
                wechi.putExtra("USER_NAME",userValue);
                wechi.putExtra("USER_TYPE",userTypeValue);
                startActivity(wechi);
                break;
            case R.id.today_records:
                Intent today_records = new Intent(HomeActivity.this,TodayRecordsActivity.class);
                today_records.putExtra("USER_NAME",userValue);
                today_records.putExtra("USER_TYPE",userTypeValue);
                startActivity(today_records);
                break;
            case R.id.history:
                Intent history = new Intent(HomeActivity.this,HistoryActivity.class);
                startActivity(history);
                break;
            case R.id.add_user:
                Intent AddUser = new Intent(HomeActivity.this,AddUserActivity.class);
                startActivity(AddUser);
                break;
            case R.id.user_type:
                Toast.makeText(HomeActivity.this,"log out",Toast.LENGTH_SHORT).show();
                Logoout();
                break;
            case R.id.user_type_image:
                Toast.makeText(HomeActivity.this,"log out",Toast.LENGTH_SHORT).show();
                Logoout();
                break;

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void Logoout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(HomeActivity.this,MainActivity.class));
    }

    private void getUserValue(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();

        currentTimestamp = ServerValue.TIMESTAMP;

        DatabaseReference databaseReference = firebaseDatabase.getReference("Users").child(uid);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userType.setText(user.getUserType());
                userTypeValue = user.getUserType();
                userValue = user.getName();

                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString(NAME, userTypeValue);
                editor.putString(USER,userValue);
                editor.apply();

                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                userTypeValue = sharedPreferences.getString(NAME,null);
                userValue = sharedPreferences.getString(USER,null);
                if (userTypeValue.equals("Guest")){
                    add_user.setEnabled(false);
                    history.setEnabled(false);
                }
                userName.setText(user.getName());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this,databaseError.getCode(),Toast.LENGTH_LONG).show();
            }
        });
        progressDialog.dismiss();
    }

    private void DeleteAll(){
        DatabaseReference dd = FirebaseDatabase.getInstance().getReference("Wechi");
        dd.removeValue();
    }
}
