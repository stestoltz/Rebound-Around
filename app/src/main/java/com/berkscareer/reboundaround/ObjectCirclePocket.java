package com.berkscareer.reboundaround;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.gms.games.Games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectCirclePocket extends ObjectCircle {

    //shrinking
    private boolean shrink;
    private double shrinkrament;

    //color carrying when filled
    private double red, green, blue;
    private double redInc, greenInc, blueInc;

    //the ball that fell
    private ObjectCircleBall myOCB;

    //used for textures
    int counter;
    int textureNum;
    Rect rect;
    List<RoundedBitmapDrawable> lstCircles;
    List<Bitmap> lstTextures;

    //texture without animation
    Bitmap noAnimationBitmap;

    public ObjectCirclePocket(double dia, double x, double y) {
        super();
        int diameter = (int)Menu.scale(dia);
        Point loc = new Point(Menu.scale(x, y));

        radius = diameter / 2;
        xLoc = loc.x;
        yLoc = loc.y;
        shrinkrament = radius * .02;

        this.removeBorder();


        //slightly randomize color
        Random pocketRNG = new Random();
        int pocketRGB = 50 + pocketRNG.nextInt(30);

        paintColor.setColor(Color.rgb(pocketRGB, pocketRGB, pocketRGB));
        red = Color.red(paintColor.getColor());
        green = Color.green(paintColor.getColor());
        blue = Color.blue(paintColor.getColor());

        //randomize first texture
        Random locationRNG = new Random();
        textureNum = locationRNG.nextInt(2);

        switch (textureNum) {
            case 0: counter = 1; break;
            case 1: counter = 21; break;
            default: counter = 1; break;
        }

        rect = new Rect((int)(this.getXLoc() - this.getRadius()), (int)(this.getYLoc() - this.getRadius()), (int)(this.getXLoc() + this.getRadius()), (int)(this.getYLoc() + this.getRadius()));

        //create animated textures
        if (GameGUI.ga.getOptions()[2]) {
            lstCircles = new ArrayList<>();
            lstTextures = new ArrayList<>();

            //get textures
            lstTextures.add(BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_pocket_1));
            lstTextures.add(BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_pocket_2));

            for (Bitmap texture: lstTextures) {
                //scale bitmaps
                texture = Bitmap.createScaledBitmap(texture, this.getRadius() * 2, this.getRadius() * 2, false);
                lstCircles.add(getCircle(texture));
            }

        } else {

            //randomize no animation bitmap

            switch (textureNum) {
                case 0:
                    noAnimationBitmap = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_pocket_1); break;
                case 1:
                    noAnimationBitmap = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_pocket_2); break;
                default:
                    noAnimationBitmap = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_pocket_1); break;
            }
            noAnimationBitmap = Bitmap.createScaledBitmap(noAnimationBitmap, this.getRadius() * 2, this.getRadius() * 2, false);
        }

        blnBorder = false;
    }

    //for textures
    private RoundedBitmapDrawable getCircle(Bitmap bitmap) {
        //convert bitmap to rounded bitmap
        RoundedBitmapDrawable circleBit = RoundedBitmapDrawableFactory.create(GameGUI.ga.getResources(), bitmap);
        circleBit.setCornerRadius(this.getRadius());
        circleBit.setBounds(rect);

        return circleBit;
    }

    private RoundedBitmapDrawable getNewCircle(int currentNum) {
        RoundedBitmapDrawable circleBit;

        if (shrink && radius > currentNum) {
            lstTextures.set(currentNum, Bitmap.createScaledBitmap(lstTextures.get(currentNum), this.getRadius() * 2, this.getRadius() * 2, false));
            lstCircles.set(currentNum, getCircle(lstTextures.get(currentNum)));
        }
        circleBit = lstCircles.get(currentNum);
        counter += 1;

        return circleBit;
    }

    public void draw(Canvas canvas) {

        //if shrinking, increment color and get smaller
        if (shrink && radius > 0) {
            radius -= shrinkrament;
            red += redInc;
            green += greenInc;
            blue += blueInc;
            paintColor.setColor(Color.rgb((int) red, (int) green, (int) blue));
            rect.set((int)(this.getXLoc() - this.getRadius()), (int)(this.getYLoc() - this.getRadius()), (int)(this.getXLoc() + this.getRadius()), (int)(this.getYLoc() + this.getRadius()));
        } else if (shrink && radius <= 0) {
            //if done shrinking, disable
            this.disable();
        }

        RoundedBitmapDrawable circleBit;

        if (GameGUI.ga.getOptions()[2]) {

            //switches every 20 ticks
            if (counter > 40) {
                counter = 1;
            }

            if (counter >= 1 && counter <= 20) {
                circleBit = getNewCircle(0);
            } else if (counter >= 21 && counter <= 40) {
                circleBit = getNewCircle(1);
            } else {
                circleBit = getNewCircle(0);
            }
        } else {
            if (shrink && radius > 0) {
                noAnimationBitmap = Bitmap.createScaledBitmap(noAnimationBitmap, this.getRadius() * 2, this.getRadius() * 2, false);
            }
            circleBit = getCircle(noAnimationBitmap);
        }

        //draw the current bitmap on top of the circle
        canvas.drawCircle((float) xLoc, (float) yLoc, (float) radius, paintColor);
        circleBit.draw(canvas);

        if (blnBorder) {
            canvas.drawCircle((float) xLoc, (float) yLoc, (float) radius, paintBorder);
        }

    }

    public boolean fillPocket(ObjectCircleBall ocb) {
        if (myOCB == null) {

            myOCB = ocb;

            GameGUI.lstNextFloorAdd.add(myOCB);

            double redDest, greenDest, blueDest;

            shrink = true;
            Paint paintBall = new Paint(myOCB.getPaint());
            redDest = Color.red(paintBall.getColor());
            greenDest = Color.green(paintBall.getColor());
            blueDest = Color.blue(paintBall.getColor());

            redInc = (redDest - red) * .02;
            greenInc = (greenDest - green) * .02;
            blueInc = (blueDest - blue) * .02;

            if (GameGUI.ga.getOptions()[GameGUI.ga.getOptions().length - 1] && Menu.googleAPI != null && Menu.googleAPI.isConnected()) {

                Games.Achievements.increment(Menu.googleAPI, GameGUI.ga.getResources().getString(R.string.ach_fallin), 1);
                Games.Achievements.increment(Menu.googleAPI, GameGUI.ga.getResources().getString(R.string.ach_gotta_sink_em_all), 1);
            }

            return true;
        }

        return false;

    }

    public boolean getShrink() {
        return shrink;
    }
}
