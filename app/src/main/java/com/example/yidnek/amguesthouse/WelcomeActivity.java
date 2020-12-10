package com.example.yidnek.amguesthouse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import static com.example.yidnek.amguesthouse.NetworkConfig.isConnected;

public class WelcomeActivity extends AppCompatActivity {

    private static int timeOut = 3000;
    ImageView imageView;
    ProgressBar simpleProgressBar;
//    int progress = 0;

    @Override
    protected void onStart() {
        super.onStart();

        if (prerun()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            },timeOut);

        }
        else {
            buildDialog(WelcomeActivity.this).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);




        imageView = (ImageView) findViewById(R.id.imageview1);
        Animation animationtra = new TranslateAnimation(0, 0,50, 100);
        animationtra.setDuration(7000);
        animationtra.setFillAfter(true);
        imageView.startAnimation(animationtra);
        imageView.setVisibility(View.VISIBLE);

        simpleProgressBar = (ProgressBar) findViewById(R.id.progressbar);
//        setProgressValue(progress);
    }

//    private void setProgressValue(final int progress) {
//
//        // set the progress
//        simpleProgressBar.setProgress(progress);
//        // thread is used to change the progress value
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (prerun()){
//                    setProgressValue(progress + 10);
//                }
//                else {
//                    setProgressValue(progress + 2);
//                }
//
//            }
//        });
//        thread.start();
//    }
    private boolean prerun(){
        if (!isConnected(WelcomeActivity.this)) {
//            buildDialog(WelcomeActivity.this).show();
        }
        else {
            CalculateDay calculateDay = new CalculateDay();
            calculateDay.setCurrentDate();
            return true;
        }
        return false;
    }
    private AlertDialog.Builder buildDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("You need to have Mobile data or wifi to see updated data. may you see nothing");

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("Show Offline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        return builder;
    }
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
