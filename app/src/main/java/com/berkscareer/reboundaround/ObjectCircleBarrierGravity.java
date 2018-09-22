package com.berkscareer.reboundaround;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

public class ObjectCircleBarrierGravity extends ObjectCircleBarrier {

    private final double G = 6.673E-11; //Newton's gravitational constant
    private final double DENSITY = 1E7 * 2.5;

    private double objectMass;

    RoundedBitmapDrawable first;
    RoundedBitmapDrawable second;
    RoundedBitmapDrawable third;

    Rect rect;

    int counter = 1;

    public ObjectCircleBarrierGravity(double dia, double x, double y) {
        super(dia, x, y);

        // MASS OF CYLINDER = PI * RADIUS ^ 2 * HEIGHT * DENSITY
        objectMass = (Math.PI * Math.pow(Menu.getPercent(radius), 2)) * 1.0 * DENSITY;

        Bitmap texture1;
        Bitmap texture2;
        Bitmap texture3;

        //get the three textures
        if (GameGUI.ga.getOptions()[2]) {
            if (this instanceof ObjectCircleBarrierAntiGravity) {
                texture1 = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_antigravity_1);
                texture2 = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_antigravity_2);
                texture3 = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_antigravity_3);
            } else {
                texture1 = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_gravity_1);
                texture2 = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_gravity_2);
                texture3 = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_gravity_3);
            }

            //scale the three textures
            rect = new Rect((int) (this.getXLoc() - this.getRadius()), (int) (this.getYLoc() - this.getRadius()), (int) (this.getXLoc() + this.getRadius()), (int) (this.getYLoc() + this.getRadius()));
            texture1 = Bitmap.createScaledBitmap(texture1, this.getRadius() * 2, this.getRadius() * 2, false);
            texture2 = Bitmap.createScaledBitmap(texture2, this.getRadius() * 2, this.getRadius() * 2, false);
            texture3 = Bitmap.createScaledBitmap(texture3, this.getRadius() * 2, this.getRadius() * 2, false);

            //set the bitmaps with the textures
            first = getCircle(texture1);
            second = getCircle(texture2);
            third = getCircle(texture3);
        } else {
            //if not animating, set it to the second one
            if (this instanceof ObjectCircleBarrierAntiGravity) {
                texture2 = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_antigravity_2);
            } else {
                texture2 = BitmapFactory.decodeResource(GameGUI.ga.getResources(), R.drawable.texture_gravity_2);
            }

            rect = new Rect((int) (this.getXLoc() - this.getRadius()), (int) (this.getYLoc() - this.getRadius()), (int) (this.getXLoc() + this.getRadius()), (int) (this.getYLoc() + this.getRadius()));
            texture2 = Bitmap.createScaledBitmap(texture2, this.getRadius() * 2, this.getRadius() * 2, false);
            second = getCircle(texture2);
        }

    }

    public void draw(Canvas canvas) {

        RoundedBitmapDrawable circleBit;

        //if we are animating, continue
        if (GameGUI.ga.getOptions()[2]) {

            if (counter > 30) {
                counter = 1;
            }

            //increment through one on each tick - switch every 10 ticks
            if (counter >= 1 && counter <= 10) {
                circleBit = first;
                counter += 1;
            } else if (counter >= 11 && counter <= 20) {
                circleBit = second;
                counter += 1;
            } else if (counter >= 21 && counter <= 30) {
                circleBit = third;
                counter += 1;
            } else {
                circleBit = first;
                counter = 1;
            }
        } else {
            circleBit = second;
        }

        circleBit.draw(canvas);

        if (blnBorder) {
            canvas.drawCircle((float) xLoc, (float) yLoc, radius, paintBorder);
        }
    }

    private RoundedBitmapDrawable getCircle(Bitmap bitmap) {
        //convert a bitmap to a rounded bitmap
        RoundedBitmapDrawable circleBit = RoundedBitmapDrawableFactory.create(GameGUI.ga.getResources(), bitmap);
        circleBit.setCornerRadius(this.getRadius());
        circleBit.setBounds(rect);
        circleBit.getPaint().setColorFilter(color);

        return circleBit;
    }

    public void event(ObjectCircleBall ocb) {

        //direction of pull, distance from ball
        double direction = Math.atan2((this.getYLoc() - ocb.getYLoc()), (this.getXLoc() - ocb.getXLoc()));
        double distance = Menu.getPercent(Math.sqrt(Math.pow((ocb.getXLoc() - this.getXLoc()), 2.0) + Math.pow((ocb.getYLoc() - this.getYLoc()), 2.0)));

        //forceofgravity = (G * MASS) / DISTANCE^2
        double forceOfGravity = (G * objectMass) / Math.pow(distance, 2.0);

        if (this instanceof ObjectCircleBarrierAntiGravity) {

            //push ball away
            ocb.xMove -= Menu.scale(forceOfGravity) * Math.cos(direction);
            ocb.yMove -= Menu.scale(forceOfGravity) * Math.sin(direction);

        } else {
            double xM = ocb.xMove;
            double yM = ocb.yMove;

            //increment move with pull
            xM += Menu.scale(forceOfGravity) * Math.cos(direction);
            yM += Menu.scale(forceOfGravity) * Math.sin(direction);

            //not touching barrier after movement - move
            if ((Math.pow(ocb.getXLoc() + xM - this.getXLoc(), 2) + Math.pow(ocb.getYLoc() + yM - this.getYLoc(), 2) > Math.pow(this.getRadius() + ocb.getRadius(), 2))) {

                ocb.xMove = xM;
                ocb.yMove = yM;
            } else {

                //we are touching - offset from barrier, eventually stop the ball

                distance = Math.sqrt(Math.pow(ocb.getXLoc() - this.getXLoc(), 2) + Math.pow(ocb.getYLoc() - this.getYLoc(), 2));

                double collisionDirection = Math.atan2(this.getYLoc() - ocb.getYLoc(), this.getXLoc() - ocb.getXLoc());

                double minDistance = this.getRadius() + ocb.getRadius();
                double displacement = minDistance - distance;

                //offset
                if (Math.pow(ocb.getXLoc() + xM - this.getXLoc(), 2) + Math.pow(ocb.getYLoc() + yM - this.getYLoc(), 2) <= Math.pow(this.getRadius() + ocb.getRadius(), 2)) {
                    ocb.xLoc -= displacement * Math.cos(collisionDirection);
                    ocb.yLoc -= displacement * Math.sin(collisionDirection);
                    if (!ocb.getMoving()) {
                        ocb.xMove = 0.0;
                        ocb.yMove = 0.0;
                    }
                }
            }
        }
    }
}
