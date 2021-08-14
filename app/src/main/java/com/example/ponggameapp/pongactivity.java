package com.example.ponggameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class pongactivity extends AppCompatActivity {
    GameThread mgameThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pongactivity);
        final pongtable table = (pongtable) findViewById(R.id.pongtable);
        table.setScoreOpponent((TextView)findViewById(R.id.scoreopponent));
        table.setScorePlayer((TextView)findViewById(R.id.scoreplayer));
        table.setStatusview((TextView)findViewById(R.id.gamestatus));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mgameThread=table.getGame();


    }


}