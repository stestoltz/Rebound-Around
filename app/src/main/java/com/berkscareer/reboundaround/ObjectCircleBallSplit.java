package com.berkscareer.reboundaround;

import android.graphics.Color;
import android.graphics.Point;

import com.google.android.gms.games.Games;

import java.util.List;

public class ObjectCircleBallSplit extends ObjectCircleBall{

    private final double FULL_CIRCLE = 2.0 * Math.PI;

    //initial constructor
    public ObjectCircleBallSplit(double dia, double x, double y) {
        super(dia, x, y);
        paintColor.setColor(Color.rgb(0, 0, 128));
    }

    //split constructor
    public ObjectCircleBallSplit(int diameter, Point location, double xMove, double yMove) {
        super(diameter, location);
        paintColor.setColor(Color.rgb(0, 0, 128));
        this.xMove = xMove;
        this.yMove = yMove;
    }

    //if diameter is greater than .175 of the screen, the split balls will be greater than .07 of the screen
    public boolean getSplit() {
        return (2 * this.getRadius() > Menu.scale(.175));
    }

    public boolean event(List<Object> lstAdd) {
        //called onTouch

        if (this.getSplit()) {
            //if we can split, split

            //new diameter
            int splitDiameter = (int)Math.floor(this.getRadius() * .8);

            //angle of movement
            double angle = Math.atan2(this.getYMove(), this.getXMove());

            //split, retaining movement and putting the first ball pointing in the direction of the current movement
            for (int counter = 1; counter <= 3; counter += 1) {

                lstAdd.add(new ObjectCircleBallSplit(splitDiameter, new Point(
                        (int) (this.getXLoc() + (int)((this.getRadius() * Math.cos(angle)) / 2.0)),
                        (int) (this.getYLoc() + (int)((this.getRadius() * Math.sin(angle)) / 2.0))),
                        this.getXMove() + Math.cos(angle), this.getYMove() + Math.sin(angle)));

                angle += FULL_CIRCLE / 3.0;
                if (angle >= FULL_CIRCLE) {
                    angle -= FULL_CIRCLE;
                }
            }

            this.disable();

            if (GameGUI.ga.getOptions()[GameGUI.ga.getOptions().length - 1] && Menu.googleAPI != null && Menu.googleAPI.isConnected()) {
                Games.Achievements.increment(Menu.googleAPI, GameGUI.ga.getResources().getString(R.string.ach_split_forever), 1);
            }
        }

        return this.getSplit();
    }
}
