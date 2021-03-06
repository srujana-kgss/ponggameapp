package com.example.ponggameapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;

public class gamethreadtwo extends Thread {





    public static final int STATETWO_READY=0;
    public static final int STATETWO_PAUSE=1;
    public static final int STATETWO_RUNNING=2;
    public static final int STATETWO_WIN=3;
    public static final int STATETWO_LOSE=4;

    int lastscores;


    private boolean nSenorsOn;
    private final Context nCtx;
    private final SurfaceHolder nSurfaceHolder;
    private final Handler nGameStatusHandler;
    private final  pongtabletwo npongtable;
    private final Handler nScoreHandler;

    public boolean nRun=false;
    private int nGameState;
    private Object nRunLock;

    private static final int PHYS_FPS=60;





    public gamethreadtwo( Context nCtx, SurfaceHolder nSurfaceHolder,pongtabletwo npongtable, Handler nGameStatusHandler,  Handler nScoreHandler) {

        this.nCtx = nCtx;
        this.nSurfaceHolder = nSurfaceHolder;
        this.nGameStatusHandler = nGameStatusHandler;
        this.npongtable = npongtable;
        this.nScoreHandler = nScoreHandler;
        this.nRunLock=new Object();
    }
      @Override
              public void run(){

        long nNextGameTick = SystemClock.uptimeMillis();
        int skipTicks=1000/PHYS_FPS;

        while (nRun){
            Canvas c =null;
            try{
                c= nSurfaceHolder.lockCanvas(null);
                if(c!=null){
                    synchronized (nSurfaceHolder){
                        if(nGameState==STATETWO_RUNNING){
                            npongtable.updating(c);
                        }
                        synchronized (nRunLock){
                            if(nRun){
                                npongtable.draw(c);
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(c!=null){
                    nSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
        }

        nNextGameTick +=skipTicks;
        long sleepTime=nNextGameTick-SystemClock.uptimeMillis();
        if(sleepTime>0){
            try{Thread.sleep(sleepTime);

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }


    public void setStatefortwo(int state){
        synchronized (nSurfaceHolder){
            nGameState=state;
            Resources res=nCtx.getResources();
            switch (nGameState){
                case STATETWO_READY:
                    setUpNewRoundfortwo();
                    break;
                case STATETWO_RUNNING:
                    hidestatustextfortwo();
                    break;
                case STATETWO_WIN:
                    setstatustextfortwo(res.getString(R.string.mode_win));

                    setUpNewRoundfortwo();
                    break;
                case STATETWO_LOSE:
                    setstatustextfortwo("score:"+npongtable.getplayerfortwo().score);
                     lastscores=npongtable.getplayerfortwo().score;
                     SharedPreferences sharedPreferences= nCtx.getSharedPreferences("s",Context.MODE_PRIVATE);
                     sharedPreferences.edit().putInt("thread",lastscores).apply();
                     Intent i = new Intent(nCtx,scoreforSingleplayer.class);
                     nCtx.startActivity(i);
                     break;
                case STATETWO_PAUSE:
                    setstatustextfortwo(res.getString(R.string.mode_pause));
                    break;
            }
        }
    }

    public void setRunningfortwo(boolean running){
        synchronized (nRunLock){
            nRun=running;
        }
    }



    public boolean SensorsOnfortwo(){   //ctr
        return nSenorsOn;
    }

    public boolean isBetweenRoundsfortwo(){
        return nGameState!=STATETWO_RUNNING;
    }


    public void setUpNewRoundfortwo(){
        synchronized (nSurfaceHolder){
            npongtable.setuptablefortwo();
        }

    }


    public  void setstatustextfortwo(String text){
        Message msg =nGameStatusHandler.obtainMessage();
        Bundle b =new Bundle();
        b.putString("text",text);
        b.putInt("visibility", View.VISIBLE);
        msg.setData(b);
        nGameStatusHandler.sendMessage(msg);
    }


    private void hidestatustextfortwo(){
        Message msg =nGameStatusHandler.obtainMessage();
        Bundle b =new Bundle();
        b.putInt("visibility",View.INVISIBLE);
        msg.setData(b);
        nGameStatusHandler.sendMessage(msg);
    }

    public void setscoretextfortwo(String playerscore){
        Message msg = nScoreHandler.obtainMessage();
        Bundle b =new Bundle();
        b.putString("player",playerscore);
        msg.setData(b);
        nScoreHandler.sendMessage(msg);
    }
}
