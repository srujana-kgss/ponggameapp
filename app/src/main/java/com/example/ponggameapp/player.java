package com.example.ponggameapp;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class player {
    private int racketheight;
    private int racketwidth;
    private Paint paint;
    public int score;
    public RectF bounds;


    public player(int racketwidth, int racketheight, Paint paint) {
        this.racketheight = racketheight;
        this.racketwidth = racketwidth;
        this.paint = paint;
        score=0;
        bounds=new RectF(0,0,racketwidth,racketheight);
    }

    public void draw(Canvas canvas){
        canvas.drawRoundRect(bounds,5,5,paint);
    }

    public int getRacketheight() {
        return racketheight;
    }

    public int getRacketwidth() {
        return racketwidth;
    }

    @Override
    public String toString() {
        return "height=" + racketheight + ", width=" + racketwidth + ", score=" + score +
                ", top=" + bounds.top + " left="+bounds.left;
    }
}
