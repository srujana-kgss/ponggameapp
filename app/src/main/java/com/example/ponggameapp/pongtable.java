package com.example.ponggameapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.Random;
import android.os.Handler;



public class pongtable extends SurfaceView implements SurfaceHolder.Callback {

public static final String TAG = pongtable.class.getSimpleName();


private GameThread mGame;
private TextView mStatus;
private TextView mScorePlayer;
private TextView mScoreOpponent;
MediaPlayer mediaPlayer;
Integer[] integer = {1,-1};
 int x = randBetween(0,1);







   private player mplayer;
   private player mopponent;
   private ball mball;
   private Paint mnetpaint;
   private Paint mTableboundpaint;
   private int mtablewidth;
   private int mtableheight;
   private Context mcontext;


    SurfaceHolder mholder;
    public static float  PHY_RACKET_SPEED =15.0f;
    public static float  PHY_BALL_SPEED =15.0f;


    private float mAImoveprobability;
    private boolean moving=false;
    private float mlasttouchY;


    public  void  initpongtable(Context Ctx,AttributeSet attr){
     mcontext=Ctx;
     mholder=getHolder();
     mholder.addCallback(this);


     mGame=new GameThread(this.getContext(), mholder, this
             , new Handler(){
         @Override
         public void handleMessage(@NonNull Message msg) {
             super.handleMessage(msg);
             mStatus.setVisibility(msg.getData().getInt("visibility"));
             mStatus.setText(msg.getData().getString("text"));
         }
     }, new Handler() {
         @Override
         public void handleMessage(@NonNull Message msg) {
             super.handleMessage(msg);
             mScorePlayer.setText(msg.getData().getString("player"));
             mScoreOpponent.setText(msg.getData().getString("opponent"));
         }
     });





     TypedArray a =Ctx.obtainStyledAttributes(attr,R.styleable.PongTable);
     int racketheight=a.getInteger(R.styleable.PongTable_racketHeight,340);
     int racketwidtt=a.getInteger(R.styleable.PongTable_racketWidth,100);
     int ballradius=a.getInteger(R.styleable.PongTable_ballRadius,25);


     Paint playerpaint=new Paint();
     playerpaint.setAntiAlias(true);
     playerpaint.setColor(ContextCompat.getColor(mcontext,R.color.player_color));
     mplayer=new player(racketwidtt,racketheight,playerpaint);



     Paint opponentpaint=new Paint();
     opponentpaint.setAntiAlias(true);
     opponentpaint.setColor(ContextCompat.getColor(mcontext,R.color.opponent_color));
     mopponent=new player(racketwidtt,racketheight,opponentpaint);



     Paint ballpaint=new Paint();
     ballpaint.setAntiAlias(true);
     ballpaint.setColor(ContextCompat.getColor(mcontext,R.color.ball_color));
     mball=new ball(ballradius,ballpaint);

     mnetpaint=new Paint();
     mnetpaint.setAntiAlias(true);
     mnetpaint.setColor(Color.WHITE);
     mnetpaint.setAlpha(80);
     mnetpaint.setStyle(Paint.Style.FILL_AND_STROKE);
     mnetpaint.setStrokeWidth(10.f);
     mnetpaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));



     mTableboundpaint=new Paint();
     mTableboundpaint.setAntiAlias(true);
     mTableboundpaint.setColor(ContextCompat.getColor(mcontext,R.color.player_color));
     mTableboundpaint.setStyle(Paint.Style.STROKE);
     mTableboundpaint.setStrokeWidth(15.0f);


     mAImoveprobability=0.7f;

    }

    private static int randBetween(int start, int end){
        return start+ (int) Math.round(Math.random() * (end-start));
    }





    public pongtable(Context context, AttributeSet attrs) {
        super(context, attrs);
        initpongtable(context,attrs);
    }

    public pongtable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initpongtable(context,attrs);
    }


    @Override
    public  void draw(Canvas canvas){
     super.draw(canvas);
     canvas.drawColor(ContextCompat.getColor(mcontext,R.color.table_color));
     canvas.drawRect(0,0,mtablewidth,mtableheight,mTableboundpaint);


     int middle = mtablewidth/2;
     canvas.drawLine(middle,1,middle,mtableheight-1,mnetpaint);
     mGame.setscoretext(String.valueOf(mplayer.score),String.valueOf(mopponent.score));

     mplayer.draw(canvas);
     mopponent.draw(canvas);
     mball.draw(canvas);
    }



    private void doAI(){

     if(mopponent.bounds.top>mball.cy){
       moveplayer(mopponent,mopponent.bounds.left,mopponent.bounds.top-PHY_RACKET_SPEED);
     }else if(mopponent.bounds.top+mopponent.getRacketheight()< mball.cy){
      moveplayer(mopponent,mopponent.bounds.left,mopponent.bounds.top+PHY_RACKET_SPEED);
     }

    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
     mGame.setRunning(true);
     mGame.start();
    }

    @Override
    public void surfaceChanged( SurfaceHolder surfaceholder, int format, int width, int height) {
      mtableheight=height;
      mtablewidth=width;
      mGame.setUpNewRound();
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder surfaceholder) {
       boolean retry = true;
       mGame.setRunning(false);
       while (retry){
           try{
               mGame.join();
               retry=false;
           }catch (InterruptedException e){
               e.printStackTrace();
           }
       }
    }


 @Override
 public boolean onTouchEvent(MotionEvent event) {
        if(!mGame.SensorsOn()){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(mGame.isBetweenRounds()){
                        mGame.setState(GameThread.STATE_RUNNING);
                    }else {
                        if(istouchonracket(event,mplayer)){
                            moving=true;
                            mlasttouchY=event.getY();
                        }
                    }break;
                case  MotionEvent.ACTION_MOVE:
                    if(moving){
                        float y =event.getY();
                        float dy =y-mlasttouchY;
                        mlasttouchY =y;
                        moverplayerracket(dy,mplayer);
                    }break;
                case MotionEvent.ACTION_UP:
                    moving=false;
                    break;
            }
        }else {
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                if(mGame.isBetweenRounds()){
                    mGame.setState(GameThread.STATE_RUNNING);
                }
            }
        }
  return true;
 }

 public GameThread getGame(){
        return mGame;
 }



public void moverplayerracket(float dy,player player){
synchronized (mholder){
 moveplayer(player,player.bounds.left,player.bounds.top+dy);
}
}






public boolean istouchonracket(MotionEvent event,player player){
     return player.bounds.contains(event.getX(),event.getY());
}



public  synchronized void moveplayer(player player,float left,float top){
     if(left<2){
        left=2;
     }else if(left + player.getRacketwidth()>=mtablewidth-2){
      left = mtablewidth - player.getRacketwidth() - 2;
     }
     if(top<0){
      top=0;
     }else if(top + player.getRacketheight()>=mtableheight){
       top= mtableheight- player.getRacketheight()-1;
     }

     player.bounds.offsetTo(left,top);
    }



    public void update(Canvas canvas){


          if(CheckcollisionPlayer(mplayer,mball)){
               handlecollision(mplayer,mball);
               mediaPlayer=MediaPlayer.create(mcontext,R.raw.beep);
               mediaPlayer.setLooping(false);
               mediaPlayer.start();
         }else if (CheckcollisionPlayer(mopponent,mball)){
             handlecollision(mopponent,mball);
              mediaPlayer=MediaPlayer.create(mcontext,R.raw.beep);
              mediaPlayer.setLooping(false);
              mediaPlayer.start();
          }else if(checkcollisionwithtoporbottomwall()){
             mball.velocity_y=-mball.velocity_y;
          }else  if(checkcollisionwithleftwall()){
              mGame.setState(GameThread.STATE_LOSE);
              return;
         }else if(checkcollisionwithrightwall()){
              mGame.setState(GameThread.STATE_WIN);
              return;
          }


        if(new Random(System.currentTimeMillis()).nextFloat()<mAImoveprobability){
            doAI();
        }
       mball.moveball(canvas);

    }


   private boolean CheckcollisionPlayer(player players,ball balls){
     return players.bounds.intersects(
             balls.cx-balls.getRadius(),
             balls.cy- balls.getRadius(),
             balls.cx+balls.getRadius(),
             balls.cy+balls.getRadius()
    );
   }


    private  boolean checkcollisionwithtoporbottomwall(){
       return ((mball.cy<=mball.getRadius())||(mball.cy+mball.getRadius()>=mtableheight-1));
    }

    private  boolean checkcollisionwithleftwall(){  //crt
       return mball.cx<=mball.getRadius();
    }

    private boolean checkcollisionwithrightwall(){
        return mball.cx+mball.getRadius()>=mtablewidth-1;
    }


    private  void handlecollision(player player ,ball  ball){
    ball.velocity_x=-ball.velocity_x*1.05f;
    if(player==mplayer){
   ball.cx=mplayer.bounds.right+ball.getRadius();
    }else if (player==mopponent){
       ball.cx=mopponent.bounds.left-ball.getRadius();
        PHY_RACKET_SPEED=PHY_RACKET_SPEED*1.03f;

    }
    }

    public void setuptable(){
        placeball();
        placeplayers();
    }

    private void placeplayers(){
mplayer.bounds.offsetTo(2,(mtableheight-mplayer.getRacketheight())/2);
mopponent.bounds.offsetTo(mtablewidth-mopponent.getRacketwidth()-2,(mtableheight-mopponent.getRacketheight())/2);
    }



private void placeball(){
        mball.cx=mtablewidth/2;
        mball.cy=mtableheight/2;
        mball.velocity_y=integer[x]*((mball.velocity_y/Math.abs(mball.velocity_y))*PHY_BALL_SPEED);
        mball.velocity_x=-(mball.velocity_x/Math.abs(mball.velocity_x))*PHY_BALL_SPEED;
}


  public  player getplayer() {return mplayer;}
   public  player getopponent() {return mopponent;}



    public void setScorePlayer(TextView view){mScorePlayer=view;}
    public void setScoreOpponent(TextView view){mScoreOpponent=view;}
    public void setStatusview(TextView view){mStatus=view;}


}
