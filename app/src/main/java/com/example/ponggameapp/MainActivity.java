package com.example.ponggameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
MediaPlayer gamestartsound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gamestartsound=MediaPlayer.create(MainActivity.this,R.raw.gamestart);
    }

    public void startgame(View view) {
        Intent intent=new Intent(MainActivity.this,pongactivity.class);
        startActivity(intent);
        gamestartsound.start();

    }

    public void startgametwo(View view) {
        Intent intent=new Intent(MainActivity.this,MainActivity2.class);
        startActivity(intent);
        gamestartsound.start();

    }

    public void scoring(View view) {

        Intent intent = new Intent(MainActivity.this,scoreforSingleplayer.class);
        startActivity(intent);
    }
}