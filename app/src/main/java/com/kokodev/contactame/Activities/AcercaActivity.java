package com.kokodev.contactame.Activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kokodev.contactame.R;

public class AcercaActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView ivTwiter,ivGitHub;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca);
        getSupportActionBar().setTitle("");
        ivTwiter = (ImageView) findViewById(R.id.ivTwitter);
        ivGitHub = (ImageView) findViewById(R.id.ivGithub);
        ivTwiter.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivTwitter:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://twitter.com/koko_dev"));
                startActivity(intent);
                break;
            case R.id.ivGithub:
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/kokoDev05"));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
