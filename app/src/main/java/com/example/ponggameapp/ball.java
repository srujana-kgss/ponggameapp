package com.example.ponggameapp;

import android.graphics.Canvas;
import android.graphics.Paint;

public class ball {

    public float cx;
    public float cy;
    public float velocity_x;
    public float velocity_y;

    private int radius;
    private Paint paint;

    public ball(int radius,Paint paint) {
        this.paint=paint;
        this.radius=radius;
        this.velocity_x=pongtable.PHY_BALL_SPEED;
        this.velocity_y=pongtable.PHY_BALL_SPEED;

    }

    public void draw(Canvas canvas){
        canvas.drawCircle(cx,cy,radius,paint);
    }

    //for moving ball

    public void moveball(Canvas canvas){
      cx+=velocity_x;
      cy+=velocity_y;                                                //move ball
      if(cy<radius){
          cy=radius;
      }else if (cy+radius>canvas.getHeight()){
          cy= canvas.getHeight() - radius-1;
      }
    }


    public int getRadius() {
        return radius;
    }



    @Override
    public String toString() {
        return "cx="+cx+"cy="+cy+"velocity_x="+velocity_x+"velocity_y="+velocity_y;
    }
}
