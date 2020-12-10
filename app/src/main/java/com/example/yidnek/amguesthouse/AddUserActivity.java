package com.example.yidnek.amguesthouse;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserActivity extends AppCompatActivity {
    private EditText Email,Userfield,Phonefield,Passwordfield,Confirmpasswordfield;
    private RadioGroup radioGroup;
    private Button adduser, userList;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private RadioButton radioButton;
    private String userType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        Email = findViewById(R.id.emailfield);
        Userfield = findViewById(R.id.userfield);
        Phonefield = findViewById(R.id.phonefield);
        Passwordfield = findViewById(R.id.passwordfield);
        Confirmpasswordfield = findViewById(R.id.confirmpasswordfield);
        adduser = findViewById(R.id.add_user);
        userList = findViewById(R.id.users_list);
        progressBar = findViewById(R.id.add_progress);

        firebaseAuth = FirebaseAuth.getInstance();

        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
                radioGroup = (RadioGroup) findViewById(R.id.radio_group);
                int id = radioGroup.getCheckedRadioButtonId();
                final String userEmail = Email.getText().toString().trim();
                final String userfield = Userfield.getText().toString().trim();
                final String phonefield = Phonefield.getText().toString().trim();
                final String userPassword = Passwordfield.getText().toString().trim();
                final String confirmationpassword = Confirmpasswordfield.getText().toString().trim();
                if (userEmail.isEmpty()){
                    Email.setError("Email Required");
                    progressBar.setVisibility(View.GONE);
                }
                else if (userfield.isEmpty()){
                    Userfield.setError("Username Required");
                    progressBar.setVisibility(View.GONE);
                }
                else if (phonefield.isEmpty()){
                    Phonefield.setError("Phone field Required");
                    progressBar.setVisibility(View.GONE);
                }
                 else if (userPassword.isEmpty()){
                    Passwordfield.setError("Password Required");
                    progressBar.setVisibility(View.GONE);
                }
                else if (confirmationpassword.isEmpty()){
                    Confirmpasswordfield.setError("Confirmation Password Required");
                    progressBar.setVisibility(View.GONE);
                }
                else if (!(userPassword.equals(confirmationpassword))){
                    Passwordfield.setError("password must be the same with Confirmation password");
                    Confirmpasswordfield.setError("Retype the confirmation password");
                    progressBar.setVisibility(View.GONE);
                }
                else if ((id != R.id.adminType)&& (id != R.id.guestType) ){
                    Toast.makeText(AddUserActivity.this,"please select user type",Toast.LENGTH_SHORT).show();
                }
                else if (id == R.id.guestType){
                    userType = "Guest";
                    CreateUser(userEmail,userPassword,userfield,phonefield,userType);
                }
                else if (id == R.id.adminType){
                    userType = "Admin";
                    CreateUser(userEmail,userPassword,userfield,phonefield,userType);
                }

            }
        });
        userList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent UserList = new Intent(AddUserActivity.this,UserListActivity.class);
                startActivity(UserList);
            }
        });
    }

    public void CreateUser(final String e, String p, final String n, final String ph, final String ut){
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){

                    User user = new User(n,ph,ut,e);
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                    Toast.makeText(AddUserActivity.this,"Successfully Added",Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddUserActivity.this,"Failed" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
