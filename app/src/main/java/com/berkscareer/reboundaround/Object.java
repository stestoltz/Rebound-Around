package com.berkscareer.reboundaround;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

abstract public class Object {

    //Whether an object is still in play
    public boolean enabled = true;

    protected Paint paintBorder;
    protected Paint paintColor;
    protected boolean blnBorder = true;

    public Object() {
        //create the paints
        paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintBorder = new Paint();
        paintBorder.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintBorder.setColor(Color.rgb(0, 0, 0));
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth((float)Menu.scale(.001));
    }

    //methods for all objects
    abstract public void draw(Canvas c);
    public void disable() {
        enabled = false;
    }
    public boolean getEnabled() {
        return enabled;
    }
    public void removeBorder() {
        blnBorder = false;
    }
}
