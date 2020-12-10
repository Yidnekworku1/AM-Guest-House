package com.example.yidnek.amguesthouse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText TextEmail;
    private Button ForgotBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        TextEmail = findViewById(R.id.email_reset_field);
        ForgotBtn = findViewById(R.id.reset_bitton);
        firebaseAuth = FirebaseAuth.getInstance();

        ForgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = TextEmail.getText().toString().trim();
                if (userEmail.equals("")){
                    TextEmail.setError("Enter email");
                }
                else{
                    progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
                    progressDialog.setMessage("Sending Email");
                    progressDialog.show();

                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ForgotPasswordActivity.this,"Password Reset Email sent",Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(new Intent(ForgotPasswordActivity.this,MainActivity.class));
                                progressDialog.dismiss();
                            }
                            else {
                                Toast.makeText(ForgotPasswordActivity.this,"Error in Sending password reset email",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }

            }
        });
    }
}
