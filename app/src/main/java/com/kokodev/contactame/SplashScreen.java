package com.kokodev.contactame;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
