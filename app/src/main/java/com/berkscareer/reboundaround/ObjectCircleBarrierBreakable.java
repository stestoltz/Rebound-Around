package com.berkscareer.reboundaround;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Point;
import android.graphics.Shader;

public class ObjectCircleBarrierBreakable extends ObjectCircleBarrier {
    public ObjectCircleBarrierBreakable(double dia, double x, double y) {
        super(dia, x, y);


        //get the texture and add it
        Bitmap bitmap = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_breakable);
        Point pntSize = new Point((int)Menu.scale(.03), (int)Menu.scale(.03));
        bitmap = Bitmap.createScaledBitmap(bitmap, pntSize.x, pntSize.y, false);
        BitmapShader fillBMPshader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paintColor.setShader(fillBMPshader);
        paintColor.setColorFilter(color);
    }



}
