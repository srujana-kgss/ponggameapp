package com.example.ponggameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
MediaPlayer gamestartsound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gamestartsound=MediaPlayer.create(MainActivity.this,R.raw.gamestart);
    }

    public void startgame(View view) {
        Intent intent=new Intent(MainActivity.this,pongactivity.class);
        startActivity(intent);
        gamestartsound.start();
    }
}