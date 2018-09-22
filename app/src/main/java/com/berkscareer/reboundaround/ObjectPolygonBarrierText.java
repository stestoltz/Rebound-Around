package com.berkscareer.reboundaround;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Point;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.List;

public class ObjectPolygonBarrierText extends ObjectPolygonBarrier {

    private float scaledTextSize;
    private Point textPadding;
    private boolean blnFullscreen = false;
    private double top;
    private Path screen;
    private TextPaint pntText;
    private String strText;

    private StaticLayout sl;

    public ObjectPolygonBarrierText(List<Point> vertices, String text) {
        super(vertices);

        top = lstVertices.get(0).y;

        textPadding = Menu.scale(.015, .01);
        scaledTextSize = (float)((Menu.size.x + Menu.size.y) / 55.0);

        strText = text;

        //build the fullscreen path
        screen = new Path();
        screen.moveTo(0, 0);
        screen.lineTo(Menu.size.x, 0);
        screen.lineTo(Menu.size.x, Menu.size.y);
        screen.lineTo(0, Menu.size.y);

        this.removeTexture();
        //build the text paint
        pntText = new TextPaint();
        pntText.setColor(Color.BLACK);
        pntText.setTextSize(scaledTextSize);

        //allows text wrapping
        sl = new StaticLayout(strText, pntText, (lstVertices.get(1).x - lstVertices.get(0).x) - textPadding.x, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

    }

    public void draw(Canvas canvas) {
        Path current;

        //set the current path
        if (blnFullscreen) {
            current = screen;
        } else {
            current = pathPolygon;
        }

        canvas.drawPath(current, paintColor);

        if (blnBorder) {
            canvas.drawPath(current, paintBorder);
        }

        canvas.save();
        if (blnFullscreen) {
            canvas.translate(textPadding.x, Menu.scale(0, .15).y + textPadding.y);
        } else {
            canvas.translate(lstVertices.get(0).x + textPadding.x, lstVertices.get(0).y + textPadding.y);
        }
        sl.draw(canvas);
        canvas.restore();
    }

    public double getTop() {
        return top;
    }

    public void changeSize(double change) {
        scaledTextSize *= change;
        pntText.setTextSize(scaledTextSize);
        sl = new StaticLayout(strText, pntText, (lstVertices.get(1).x - lstVertices.get(0).x) - textPadding.x, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
    }

    public void setFullscreen() {
        changeSize(1.25);
        blnFullscreen = true;
    }

    public void setNormal() {
        changeSize(.8);
        blnFullscreen = false;
    }
}
