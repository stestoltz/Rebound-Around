package com.berkscareer.reboundaround;

import android.graphics.Color;
import android.graphics.Point;

import com.google.android.gms.games.Games;

import java.util.ArrayList;
import java.util.List;

public class ObjectCircleBallCombine extends ObjectCircleBall {

    //initial constructor
    public ObjectCircleBallCombine(double dia, double x, double y) {
        super(dia, x, y);
        paintColor.setColor(Color.rgb(255, 128, 0));
    }

    //combination constructor
    public ObjectCircleBallCombine(double dia, double x, double y, double xMove, double yMove) {
        super((int)Menu.scale(dia), new Point((int)x, (int)y));
        this.xMove = xMove;
        this.yMove = yMove;
        paintColor.setColor(Color.rgb(255, 128, 0)); //Orange
    }

    public boolean event(ObjectCircleBallCombine ocbc) {
        boolean blnCombined = false;

        //distance from ball to ball
        double distance = Math.sqrt(Math.pow(ocbc.getXLoc() - this.getXLoc(), 2) + Math.pow(ocbc.getYLoc() - this.getYLoc(), 2));

        //if inside or almost inside each other
        if (distance <= Math.sqrt(this.getRadius() + ocbc.getRadius()) || (distance <= getRadius() - ocbc.getRadius() && getRadius() > ocbc.getRadius())) {
            //combine

            //diameter of the new ball
            double newDiameter = 2.0 * Math.sqrt((this.getMass() + ocbc.getMass()) / (Math.PI * (this.getDensity())));

            //create the new ball with the new diameter and combined speeds
            ObjectCircleBallCombine ocbcNew = new ObjectCircleBallCombine(newDiameter, this.getXLoc(), this.getYLoc(),
                    ((this.getXMove() * this.getMass()) + (ocbc.getXMove() * ocbc.getMass())) / (this.getMass() + ocbc.getMass()),
                    ((this.getYMove() * this.getMass()) + (ocbc.getYMove() * ocbc.getMass())) / (this.getMass() + ocbc.getMass()));

            GameGUI.lstAdd.add(ocbcNew);

            this.disable();
            ocbc.disable();

            //retain fingers through balls
            List<GameGUI.pointerFinger> lstNewPFs = new ArrayList<>();
            for (GameGUI.pointerFinger myPF : this.getPointerFingers(GameGUI.getPF())) {
                lstNewPFs.add(myPF);
            }
            for (GameGUI.pointerFinger myPF : ocbc.getPointerFingers(GameGUI.getPF())) {
                lstNewPFs.add(myPF);
            }
            for (GameGUI.pointerFinger myPF : lstNewPFs) {
                myPF.ocbPoints.add(ocbcNew);
            }

            if (GameGUI.ga.getOptions()[GameGUI.ga.getOptions().length - 1] && Menu.googleAPI != null && Menu.googleAPI.isConnected()) {
                Games.Achievements.increment(Menu.googleAPI, GameGUI.ga.getResources().getString(R.string.ach_combomination), 1);
            }

            blnCombined = true;
        } else {
            //balls are touching but not close enough to combine

            double percentDif = 1.0 - Math.pow((distance - ocbc.getRadius()) / (this.getRadius()), 2);

            //moves combine balls towards each other
            this.xMove += percentDif * (((ocbc.getXLoc() - this.getXLoc()) * Math.sqrt(ocbc.getMass())) / (distance * Math.sqrt(this.getMass())));
            this.yMove += percentDif * (((ocbc.getYLoc() - this.getYLoc()) * Math.sqrt(ocbc.getMass())) / (distance * Math.sqrt(this.getMass())));

        }

        return blnCombined;
    }
}
