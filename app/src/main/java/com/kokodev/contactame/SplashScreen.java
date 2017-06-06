package com.kokodev.contactame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.kokodev.contactame.Activities.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

import static android.view.animation.AnimationUtils.loadAnimation;

public class SplashScreen extends AppCompatActivity {

    ImageView imageView;
    Animation animation;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("mis_preferencias", Context.MODE_PRIVATE);
        String ok = sharedPreferences.getString("inicio","");
        if (ok.equals("ok")){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }else{
            setContentView(R.layout.activity_splash_screen);
            imageView = (ImageView) findViewById(R.id.ivLogo);
            animation = loadAnimation(getApplicationContext(),R.anim.blink);
            imageView.startAnimation(animation);

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            };

            Timer timer = new Timer();
            timer.schedule(task,2500);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor sp = sharedPreferences.edit();
        sp.putString("inicio","ok");
        sp.commit();
    }
}
