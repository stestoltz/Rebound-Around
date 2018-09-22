package com.berkscareer.reboundaround;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.Shader;

public class ObjectCircleBarrier extends ObjectCircle{

    protected ColorFilter color;
    protected int RGB;

    //public ObjectCircleBarrier(int diameter, Point location) {
    public ObjectCircleBarrier(double dia, double x, double y) {
        super();
        int diameter = (int)Menu.scale(dia);
        Point location = Menu.scale(x, y);

        radius = diameter / 2;
        mass = Integer.MAX_VALUE;

        xLoc = location.x;
        yLoc = location.y;

        RGB = 150;

        color = new LightingColorFilter(Color.rgb(RGB, RGB, RGB), 0);

        //get the texture and add it
        Bitmap bitmap = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_solid);
        Point pntSize = new Point((int)Menu.scale(.03), (int)Menu.scale(.03));
        bitmap = Bitmap.createScaledBitmap(bitmap, pntSize.x, pntSize.y, false);
        BitmapShader fillBMPshader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paintColor.setShader(fillBMPshader);
        paintColor.setColorFilter(color);
    }

    public void draw(Canvas canvas) {

        canvas.drawCircle((float) xLoc, (float) yLoc, radius, paintColor);
        if (blnBorder) {
            canvas.drawCircle((float) xLoc, (float) yLoc, radius, paintBorder);
        }
    }

}
