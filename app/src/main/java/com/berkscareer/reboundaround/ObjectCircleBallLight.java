package com.berkscareer.reboundaround;

import android.graphics.Color;

public class ObjectCircleBallLight extends ObjectCircleBall {
    public ObjectCircleBallLight(double dia, double x, double y) {
        super(dia, x, y);
        paintColor.setColor(Color.rgb(255, 255, 0)); //Yellow

    }
}
