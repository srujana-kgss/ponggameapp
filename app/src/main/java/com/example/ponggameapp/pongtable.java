package com.example.ponggameapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.content.ContextCompat;


public class pongtable extends SurfaceView implements SurfaceHolder.Callback {

public static final String TAG = pongtable.class.getSimpleName();

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
    //for AI
    private float mAImoveprobability;
    private boolean moving=false;
    private float mlasttouchY;


    public  void  initpongtable(Context Ctx,AttributeSet attr){
     mcontext=Ctx;
     mholder=getHolder();
     mholder.addCallback(this);

     //game thread or game loop initialize
     TypedArray a =Ctx.obtainStyledAttributes(attr,R.styleable.PongTable);
     int racketheight=a.getInteger(R.styleable.PongTable_racketHeight,340);
     int racketwidtt=a.getInteger(R.styleable.PongTable_racketWidth,100);
     int ballradius=a.getInteger(R.styleable.PongTable_ballRadius,25);

     // set player
     Paint playerpaint=new Paint();
     playerpaint.setAntiAlias(true);
     playerpaint.setColor(ContextCompat.getColor(mcontext,R.color.player_color));
     mplayer=new player(racketwidtt,racketheight,playerpaint);


     // set opponent
     Paint opponentpaint=new Paint();
     opponentpaint.setAntiAlias(true);
     opponentpaint.setColor(ContextCompat.getColor(mcontext,R.color.opponent_color));
     mopponent=new player(racketwidtt,racketheight,opponentpaint);


     // set ball
     Paint ballpaint=new Paint();
     ballpaint.setAntiAlias(true);
     ballpaint.setColor(ContextCompat.getColor(mcontext,R.color.ball_color));
     mball=new ball(ballradius,ballpaint);

     // draw middle line
     mnetpaint=new Paint();
     mnetpaint.setAntiAlias(true);
     mnetpaint.setColor(Color.WHITE);
     mnetpaint.setAlpha(80);
     mnetpaint.setStyle(Paint.Style.FILL_AND_STROKE);
     mnetpaint.setStrokeWidth(10.f);
     mnetpaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));


     // draw bounds
     mTableboundpaint=new Paint();
     mTableboundpaint.setAntiAlias(true);
     mTableboundpaint.setColor(Color.BLACK);
     mTableboundpaint.setStyle(Paint.Style.FILL);
     mTableboundpaint.setStrokeWidth(15.0f);


     mAImoveprobability=0.8f;

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
    protected  void onDraw(Canvas canvas){
     super.onDraw(canvas);
     canvas.drawColor(ContextCompat.getColor(mcontext,R.color.table_color));
     canvas.drawRect(0,0,mtablewidth,mtableheight,mTableboundpaint);


     int middle = mtablewidth/2;
     canvas.drawLine(middle,1,middle,mtableheight-1,mnetpaint);

     mplayer.draw(canvas);
     mopponent.draw(canvas);
     mball.draw(canvas);
    }



    private void doAI(){

     if(mopponent.bounds.top>mball.cy){
       moveplayer(mopponent,mopponent.bounds.left,mopponent.bounds.top-PHY_RACKET_SPEED);
     }else if(mopponent.bounds.top+mopponent.getRacketheight()< mball.cy){
      moveplayer(mopponent,mopponent.bounds.left,mopponent.bounds.top-PHY_RACKET_SPEED);
     }

    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged( SurfaceHolder surfaceholder, int format, int width, int height) {
      mtableheight=height;
      mtablewidth=width;
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder surfaceholder) {

    }


 @Override
 public boolean onTouchEvent(MotionEvent event) {
  return super.onTouchEvent(event);
 }



public void moverplayerracket(float dy,player mplayer){
synchronized (mholder){
 moveplayer(mplayer,mplayer.bounds.left,mplayer.bounds.top+dy);
}
}






public boolean istouchonracket(MotionEvent event,player mplayer){
     return mplayer.bounds.contains(event.getX(),event.getY());
}



public  synchronized void moveplayer(player mplayer,float left,float top){
     if(left<2){
      left=2;
     }else if(left + mplayer.getRacketwidth()>=mtablewidth-2){
      left = mtablewidth - mplayer.getRacketwidth() - 2;
     }

     if(top<0){
      top=0;
     }else if(top + mplayer.getRacketheight()>=mtableheight){
       top= mtableheight- mplayer.getRacketwidth()-1;
     }

     mplayer.bounds.offsetTo(left,top);
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
        mball.velocity_y=(mball.velocity_y/Math.abs(mball.velocity_y))*PHY_BALL_SPEED;
        mball.velocity_x=(mball.velocity_x/Math.abs(mball.velocity_x))*PHY_BALL_SPEED;
}


}
