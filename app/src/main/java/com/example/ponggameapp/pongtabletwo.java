package com.example.ponggameapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Random;

public class pongtabletwo extends SurfaceView implements SurfaceHolder.Callback {

    public static final String TAG = pongtabletwo.class.getSimpleName();


    private gamethreadtwo nGame;
    private TextView nStatus;
    private TextView nScorePlayer;
    MediaPlayer mp;

    Integer[] integervalue = {1,-1};
    int y = randBetween(0,1);


    private player nplayer;
    private player nopponent;
    private ball nball;
    private Paint nnetpaint;
    private Paint nTableboundpaint;
    private int ntablewidth;
    private int ntableheight;
    private Context ncontext;


    SurfaceHolder nholder;
    public static float  PHY_RACKET_SPEED =15.0f;
    public static float  PHY_BALL_SPEED =15.0f;

    private boolean moving=false;
    private float mlasttouchY;



    public  void  initpongtabletwo(Context Ctx,AttributeSet attr){
        ncontext=Ctx;
        nholder=getHolder();
        nholder.addCallback(this);



        nGame=new gamethreadtwo(this.getContext(), nholder, this
                , new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
               nStatus.setVisibility(msg.getData().getInt("visibility"));
                nStatus.setText(msg.getData().getString("text"));
            }
        }, new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                nScorePlayer.setText(msg.getData().getString("player"));

            }
        });




        TypedArray a =Ctx.obtainStyledAttributes(attr,R.styleable.PongTable);
        int racketheightplayer=a.getInteger(R.styleable.PongTable_racketHeight,340);
        int racketwidthplayer=a.getInteger(R.styleable.PongTable_racketWidth,100);
        int ballradius=a.getInteger(R.styleable.PongTable_ballRadius,25);




        Paint playerpaint=new Paint();
        playerpaint.setAntiAlias(true);
        playerpaint.setColor(ContextCompat.getColor(ncontext,R.color.player_color));
        nplayer=new player(racketwidthplayer,racketheightplayer,playerpaint);





        Paint opponentpaint=new Paint();
        opponentpaint.setAntiAlias(true);
        opponentpaint.setColor(ContextCompat.getColor(ncontext,R.color.opponent_color));
        nopponent=new player(racketwidthplayer,2000,opponentpaint);



        Paint ballpaint=new Paint();
        ballpaint.setAntiAlias(true);
        ballpaint.setColor(ContextCompat.getColor(ncontext,R.color.ball_color));
        nball=new ball(ballradius,ballpaint);


        nnetpaint=new Paint();
        nnetpaint.setAntiAlias(true);
        nnetpaint.setColor(Color.WHITE);
        nnetpaint.setAlpha(80);
        nnetpaint.setStyle(Paint.Style.FILL_AND_STROKE);
        nnetpaint.setStrokeWidth(10.f);
        nnetpaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));



        nTableboundpaint=new Paint();
        nTableboundpaint.setAntiAlias(true);
        nTableboundpaint.setColor(ContextCompat.getColor(ncontext,R.color.player_color));
        nTableboundpaint.setStyle(Paint.Style.STROKE);
        nTableboundpaint.setStrokeWidth(15.0f);











    }






    public pongtabletwo(Context context, AttributeSet attrs) {
        super(context, attrs);
        initpongtabletwo(context,attrs);
    }

    public pongtabletwo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initpongtabletwo(context,attrs);
    }

    private static int randBetween(int start, int end){
        return start+ (int) Math.round(Math.random() * (end-start));
    }




    @Override
    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawColor(ContextCompat.getColor(ncontext,R.color.table_color));
        canvas.drawRect(0,0,ntablewidth,ntableheight,nTableboundpaint);


        int middle = ntablewidth/2;
        canvas.drawLine(middle,1,middle,ntableheight-1,nnetpaint);
        nGame.setscoretextfortwo(String.valueOf(nplayer.score));



        nplayer.draw(canvas);
        nopponent.draw(canvas);
        nball.draw(canvas);
    }

    public void updating(Canvas canvas){
        if(CheckcollisionPlayerfortwo(nplayer,nball)){
            handlecollision(nplayer,nball);

            mp=MediaPlayer.create(ncontext,R.raw.beep);
            mp.setLooping(false);
            synchronized (mp) {
                mp.start(); }
        }else if (CheckcollisionPlayerfortwo(nopponent,nball)){
            handlecollision(nopponent,nball);
            getplayerfortwo().score++;
            getplayerfortwo().score++;
            mp.start();
        }else if(checkcollisionwithtoporbottomwallfortwo()){
            nball.velocity_y=-nball.velocity_y;
        }else  if(checkcollisionwithleftwallfortwo()){
            nGame.setStatefortwo(gamethreadtwo.STATETWO_LOSE);
            nScorePlayer.setText("score:"+getplayerfortwo().score);
         return;
        }

        nball.moveball(canvas); }

    @Override
    public void surfaceCreated( SurfaceHolder holder) {
        nGame.setRunningfortwo(true);
        nGame.start();

    }

    @Override
    public void surfaceChanged( SurfaceHolder holder, int format, int width, int height) {
     ntablewidth=width;
     ntableheight=height;
        nGame.setUpNewRoundfortwo();
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder holder) {
        boolean retry = true;
        nGame.setRunningfortwo(false);
        while (retry){
            try{
                nGame.join();
                retry=false;
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }





    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!nGame.SensorsOnfortwo()){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(nGame.isBetweenRoundsfortwo()){
                        nGame.setStatefortwo(gamethreadtwo.STATETWO_RUNNING);
                    }else {
                        if(istouchonracketfortwo(event,nplayer)){
                            moving=true;
                            mlasttouchY=event.getY();
                        }
                    }break;
                case  MotionEvent.ACTION_MOVE:
                    if(moving){
                        float y =event.getY();
                        float dy =y-mlasttouchY;
                        mlasttouchY =y;
                        moverplayerracketfortwo(dy,nplayer);
                    }break;
                case MotionEvent.ACTION_UP:
                    moving=false;
                    break;
            }
        }else {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                if(nGame.isBetweenRoundsfortwo()){
                    nGame.setStatefortwo(gamethreadtwo.STATETWO_RUNNING);
                }
            }
        }
        return true;
    }




    private boolean CheckcollisionPlayerfortwo(player players,ball balls){
        return players.bounds.intersects(
                balls.cx-balls.getRadius(),
                balls.cy- balls.getRadius(),
                balls.cx+balls.getRadius(),
                balls.cy+balls.getRadius()
        );
    }


    private  boolean checkcollisionwithtoporbottomwallfortwo(){
        return ((nball.cy<=nball.getRadius())||(nball.cy+nball.getRadius()>=ntableheight-1));
    }

    private  boolean checkcollisionwithleftwallfortwo(){  //crt
        return nball.cx<=nball.getRadius();
    }




    private  void handlecollision(player player ,ball  ball){
        ball.velocity_x=-ball.velocity_x*1.05f;
        if(player==nplayer){
            ball.cx=nplayer.bounds.right+ball.getRadius();
        }else if (player==nopponent){
            ball.cx=nopponent.bounds.left-ball.getRadius();
            PHY_RACKET_SPEED=PHY_RACKET_SPEED*1.03f;


        }
    }


    public void moverplayerracketfortwo(float dy,player player){
        synchronized (nholder){
            moveplayerfortwo(player,player.bounds.left,player.bounds.top+dy);
        }
    }

    public boolean istouchonracketfortwo(MotionEvent event, player player){
        return player.bounds.contains(event.getX(),event.getY());
    }

    public  synchronized void moveplayerfortwo(player player,float left,float top){
        if(left<2){
            left=2;
        }else if(left + player.getRacketwidth()>=ntablewidth-2){
            left = ntablewidth - player.getRacketwidth() - 2;
        }
        if(top<0){
            top=0;
        }else if(top + player.getRacketheight()>=ntableheight){
            top= ntableheight- player.getRacketheight()-1;
        }

        player.bounds.offsetTo(left,top);
    }

    public void setuptablefortwo(){
        placeballfortwo();
        placeplayersfortwo();
    }

    private void placeplayersfortwo(){
        nplayer.bounds.offsetTo(2,(ntableheight-nplayer.getRacketheight())/2);
        nopponent.bounds.offsetTo(ntablewidth-nopponent.getRacketwidth()-2,(ntableheight-nopponent.getRacketheight())/2);
    }

    public gamethreadtwo getGamestart(){
        return nGame;
    }

    private void placeballfortwo(){
        nball.cx=ntablewidth/2;
        nball.cy=ntableheight/2;
        nball.velocity_y=integervalue[y]*(nball.velocity_y/Math.abs(nball.velocity_y))*PHY_BALL_SPEED;
        nball.velocity_x=-(nball.velocity_x/Math.abs(nball.velocity_x))*PHY_BALL_SPEED;
    }

    public  player getplayerfortwo() {return nplayer;}

    //public  ball getBallfortwo() {return nball;}


    public void setScorePlayerfortwo(TextView view){nScorePlayer=view;}   //crt

    public void setStatusviewfortwo(TextView view){nStatus=view;}
}
