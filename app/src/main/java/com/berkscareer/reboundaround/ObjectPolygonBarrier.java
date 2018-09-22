package com.berkscareer.reboundaround;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.List;

public class ObjectPolygonBarrier extends ObjectPolygon {

    protected List<Point> lstVertices;
    protected Path pathPolygon;

    protected ColorFilter color;

    private int RGB;

    public ObjectPolygonBarrier(List<Point> vertices) {
        super();
        constructPolygon(vertices);
    }

    public void constructPolygon(List<Point> vertices) {
        lstVertices = new ArrayList<>(vertices);

        RGB = 150;
        color = new LightingColorFilter(Color.rgb(RGB, RGB, RGB), 0);

        //get the texture
        Bitmap bitmap = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_solid);
        Point pntSize = new Point((int)Menu.scale(.03), (int)Menu.scale(.03));
        bitmap = Bitmap.createScaledBitmap(bitmap, pntSize.x, pntSize.y, false);
        BitmapShader fillBMPshader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paintColor.setShader(fillBMPshader);
        paintColor.setColorFilter(color);

        //build the polygon path from the points
        pathPolygon = new Path();
        pathPolygon.setFillType(Path.FillType.EVEN_ODD);
        boolean first = true;
        for (Point point: lstVertices) {
            if (first) {
                pathPolygon.moveTo(point.x, point.y);
                first = false;
            } else {
                pathPolygon.lineTo(point.x, point.y);
            }
        }
        pathPolygon.close();
    }

    public void draw(Canvas canvas) {
        //draw the path
        canvas.drawPath(pathPolygon, paintColor);

        if (blnBorder) {
            canvas.drawPath(pathPolygon, paintBorder);
        }
    }

    public List<Point> getVertices() {
        return lstVertices;
    }

    public void removeTexture() {
        //remove the texture
        paintColor.reset();
        paintColor.setColor(Color.rgb(RGB, RGB, RGB));
    }

}
