package com.example.ponggameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {
    gamethreadtwo ngameThread;
    Button button;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final pongtabletwo table = (pongtabletwo) findViewById(R.id.pongtabletwo);
        table.setScorePlayerfortwo((TextView)findViewById(R.id.playertext));
        table.setStatusviewfortwo((TextView)findViewById(R.id.scoretext));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        button=findViewById(R.id.home);
        button.setVisibility(View.VISIBLE);
        ngameThread=table.getGamestart();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity2.this,MainActivity.class);
                startActivity(intent);

            }
        });

    }
}