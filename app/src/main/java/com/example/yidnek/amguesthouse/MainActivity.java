package com.example.yidnek.amguesthouse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.instacart.library.truetime.TrueTime;
import com.instacart.library.truetime.extensionrx.TrueTimeRx;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;

import java.util.Date;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private PowerManager powerManager;
//    private rx.Observable<Date> dateObservable;
    private Observable<Date> dataObserver;

    private EditText Email;
    private EditText Passsword;
    private Button Login;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    TextView forgotPassword , attemtts;
    private int counter = 5;

     String TIME_SERVER = "wwv.nist.gov";
    String TIME_SERVER1 = "time-c.nist.gov";
    private static final String TAG = "MainActivity";


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();
        Email = findViewById(R.id.emailfield);
        Passsword =  findViewById(R.id.passwordfield);
        Login = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressbar1);
        forgotPassword = findViewById(R.id.forgot);
        progressBar.setVisibility(View.GONE);
        attemtts = findViewById(R.id.attempt);

        FirebaseUser user =  firebaseAuth.getCurrentUser();
        if (user !=null){
           finish();
           startActivity(new Intent(MainActivity.this,HomeActivity.class));
        }


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                final String userEmail = Email.getText().toString().trim();
                final String userPassword = Passsword.getText().toString().trim();

                if (userEmail.isEmpty()){
                    Email.setError("Email Required");
                    progressBar.setVisibility(View.GONE);
                }
                else if (userPassword.isEmpty()){
                    Passsword.setError("Password Required");
                    progressBar.setVisibility(View.GONE);
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Email.setError("enter a valid email");
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    Validate(userEmail,userPassword);
                }



            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgot = new Intent(MainActivity.this,ForgotPasswordActivity.class);
                startActivity(forgot);
            }
        });

    }


    public void Validate(String e, String p){
        firebaseAuth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"Login Successfully",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                }
                else if (!NetworkConfig.isConnected(MainActivity.this)){
                    progressBar.setVisibility(View.GONE);
                    buildDialog(MainActivity.this).show();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    counter--;
                    attemtts.setText("Atempts remaining : " + String.valueOf(counter));
                    Toast.makeText(MainActivity.this,"Failed to login",Toast.LENGTH_SHORT).show();
                    if (counter==0){
                        Login.setEnabled(false);

                    }
                }
            }
        });
    }
    public AlertDialog.Builder buildDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile data or wifi. Press ok to exit");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        return builder;
    }


}


