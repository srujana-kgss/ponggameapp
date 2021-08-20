package com.example.ponggameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

public class scoreforSingleplayer extends AppCompatActivity {
    TextView textView1,textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorefor_singleplayer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        textView1=findViewById(R.id.recentscore);
        textView2=findViewById(R.id.highscore);


        SharedPreferences sharedPreferences=getApplication().getSharedPreferences("s",MODE_PRIVATE);
        int lastscore = sharedPreferences.getInt("thread", 0);
        int highscore = sharedPreferences.getInt("highscore",0);

        if (lastscore > highscore) {
            highscore = lastscore;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highscore", highscore);
            editor.apply();
        }
        textView2.setText("lastscore"+lastscore);
        textView1.setText("highscore"+highscore);



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(scoreforSingleplayer.this, MainActivity.class);
        startActivity(intent);
    }
}