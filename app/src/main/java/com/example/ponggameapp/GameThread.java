package com.example.ponggameapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.view.SurfaceHolder;

import android.os.Handler;
import android.view.View;

public class GameThread extends Thread {


    public static final int STATE_READY=0;
    public static final int STATE_PAUSE=1;
    public static final int STATE_RUNNING=2;
    public static final int STATE_WIN=3;
    public static final int STATE_LOSE=4;


    private boolean mSenorsOn;
    private final Context mCtx;
    private final SurfaceHolder mSurfaceHolder;
    private final Handler mGameStatusHandler;
    private final pongtable mpongtable;
    private final Handler mScoreHandler;

    private boolean mRun=false;
    private int mGameState;
    private Object mRunLock;

    private static final int PHYS_FPS=60;

    public GameThread(Context mCtx, SurfaceHolder mSurfaceHolder,pongtable mpongtable, Handler mGameStatusHandler,  Handler mScoreHandler) {
        this.mCtx = mCtx;
        this.mSurfaceHolder = mSurfaceHolder;
        this.mGameStatusHandler = mGameStatusHandler;
        this.mpongtable = mpongtable;
        this.mScoreHandler = mScoreHandler;
        this.mRunLock=new Object();
    }


    @Override
    public void run(){
        long mNextGameTick = SystemClock.uptimeMillis();
        int skipTicks=1000/PHYS_FPS;

        while (mRun){
            Canvas c =null;
            try{
               c= mSurfaceHolder.lockCanvas(null);
               if(c!=null){
                   synchronized (mSurfaceHolder){
                       if(mGameState==STATE_RUNNING){
                           mpongtable.update(c);
                       }
                       synchronized (mRunLock){
                           if(mRun){
                               mpongtable.draw(c);
                           }
                       }
                   }
               }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(c!=null){
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }

        mNextGameTick +=skipTicks;
        long sleepTime=mNextGameTick-SystemClock.uptimeMillis();
        if(sleepTime>0){
            try{Thread.sleep(sleepTime);

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }


    public void setState(int state){
        synchronized (mSurfaceHolder){
            mGameState=state;
            Resources res=mCtx.getResources();
            switch (mGameState){
                case STATE_READY:
                   setUpNewRound();
                   break;
                case STATE_RUNNING:
                    hidestatustext();
                    break;
                case STATE_WIN:
                    setstatustext(res.getString(R.string.mode_win));
                    mpongtable.getplayer().score++;
                    setUpNewRound();
                    break;
                case STATE_LOSE:
                    setstatustext(res.getString(R.string.mode_lose));
                    mpongtable.getopponent().score++;
                    setUpNewRound();
                    break;
                case STATE_PAUSE:
                   setstatustext(res.getString(R.string.mode_pause));
                    break;
            }
        }
    }


    public void setUpNewRound(){
        synchronized (mSurfaceHolder){
            mpongtable.setuptable();
        }

    }

    public void setRunning(boolean running){
         synchronized (mRunLock){
             mRun=running;
         }
    }

    public boolean SensorsOn(){   //ctr
       return mSenorsOn;
    }

    public boolean isBetweenRounds(){
        return mGameState!=STATE_RUNNING;
    }

   public  void setstatustext(String text){
       Message msg =mGameStatusHandler.obtainMessage();
        Bundle b =new Bundle();
        b.putString("text",text);
        b.putInt("visibility", View.VISIBLE);
        msg.setData(b);
       mGameStatusHandler.sendMessage(msg);
   }


    private void hidestatustext(){
        Message msg =mGameStatusHandler.obtainMessage();
       Bundle b =new Bundle();
        b.putInt("visibility",View.INVISIBLE);
        msg.setData(b);
        mGameStatusHandler.sendMessage(msg);
    }

    public void setscoretext(String playerscore,String opponentscore){
        Message msg = mScoreHandler.obtainMessage();
        Bundle b =new Bundle();
        b.putString("player",playerscore);
        b.putString("opponent",opponentscore);
        msg.setData(b);
        mScoreHandler.sendMessage(msg);
    }
}
