package com.berkscareer.reboundaround;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Levels {

    //called to get the contents of each level
    public static List<List<Object>> getLevel(int intLevel) {
        //holds all floors for a level
        List<List<Object>> lstLevel = new ArrayList<>();
        //holds all objects for a floor
        List<Object> lstObjects = new ArrayList<>();
        //holds all points for a polygon
        List<Point> lstPoints = new ArrayList<>();

        //lstObjects and lstPoints are clearer after each usage

        switch (intLevel)   {
            case -3:
                //Tutorial 1 - Movement, Falling, Physics tutorial
                lstLevel.clear();

                //Floor 1

                //Pocket
                lstObjects.add(new ObjectCirclePocket(.15, .75, .25));

                //Ball
                lstObjects.add(new ObjectCircleBall(.12, .25, .25));

                //Text Barrier
                lstPoints.add(Menu.scale(0, .525));
                lstPoints.add(Menu.scale(1, .525));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "To move the ball, tap and hold inside the bounds of the ball, and " +
                        "drag out! The force is bigger the further you pull. However, bigger balls with more mass are harder to move than " +
                        "smaller balls. You can have multiple fingers on the screen at a time, in the same ball or in different balls. A projection " +
                        "of the resulting velocity from the force you apply will appear. When you release, its movement will appear. You can " +
                        "turn projections and dynamic movement predictions from the menu, under options. Hit the red ball into " +
                        "the black pocket to complete the floor."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 2

                //Pocket
                lstObjects.add(new ObjectCirclePocket(.15, .25, .25));

                //Text Barrier
                lstPoints.add(Menu.scale(0, .64));
                lstPoints.add(Menu.scale(1, .64));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "Some levels have multiple floors. You " +
                        "must complete all floors to complete the level. Balls that go in pockets fall onto the next floor. " +
                        "When you complete every floor in the level, your score is the total time balls moved. If you find " +
                        "yourself unable to complete the level, you can tap the background, and then tap restart in the top " +
                        "right corner to restart from the first floor."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 3

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.15, .25, .45));
                lstObjects.add(new ObjectCirclePocket(.09, .25, .15));

                //Ball
                lstObjects.add(new ObjectCircleBall(.075, .75, .5));

                //Text Barrier
                lstPoints.add(Menu.scale(0, .64));
                lstPoints.add(Menu.scale(1, .64));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "Usually, there are multiple balls and multiple pockets. Because smaller balls " +
                        "have less mass, applying the same force to them makes them move faster. All pockets must be filled before you can complete " +
                        "the level. This means you may find a situation in which the level cannot be completed, and you must restart the level from " +
                        "the first floor to complete it. After some time, a hint will appear. You can turn hints off under options."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 4

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.15, .75, .325));

                //Barriers
                lstObjects.add(new ObjectCircleBarrier(.125, .5, .15));

                lstPoints.add(Menu.scale(.45, .45));
                lstPoints.add(Menu.scale(.55, .45));
                lstPoints.add(Menu.scale(.55, .55));
                lstPoints.add(Menu.scale(.45, .55));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                //Text Barrier
                lstPoints.add(Menu.scale(0, .64));
                lstPoints.add(Menu.scale(1, .64));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "Gray or red brick circles or polygons are barriers. While some " +
                        "have special effects, their basic function is to block the balls. On this floor, when a ball collides with anything, " +
                        "the game will pause and show you the projected results of the collisions. You can turn this feature, collision pausing, " +
                        "on under options. Try bouncing off the barriers to see the physics, and fill the pocket."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();
                break;
            case -2:
                //Tutorial 2 - Combine, Split tutorial
                lstLevel.clear();

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.1, .8, .175));
                lstObjects.add(new ObjectCirclePocket(.1, .8, .4));
                lstObjects.add(new ObjectCirclePocket(.1, .8, .625));

                //Balls
                lstObjects.add(new ObjectCircleBallSplit(.2, .225, .4));
                lstObjects.add(new ObjectCircleBallCombine(.11, .5, .175));
                lstObjects.add(new ObjectCircleBallCombine(.11, .5, .4));
                lstObjects.add(new ObjectCircleBallCombine(.11, .5, .625));

                //Text Barrier
                lstPoints.add(Menu.scale(0, .8));
                lstPoints.add(Menu.scale(1, .8));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "While the red ball is the default ball, balls with additional properties " +
                        "have different colors. Blue balls split when tapped (until they are too small to split), while " +
                        "orange balls attempt to combine when they collide."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();
                break;

            case -1:
                //Tutorial 3 - Gravity, Antigravity, Breakables tutorial
                lstLevel.clear();

                //Floor 1

                //Pocket
                lstObjects.add(new ObjectCirclePocket(.15, .25, .25));

                //Ball
                lstObjects.add(new ObjectCircleBall(.13, .75, .6));

                //Barrier
                lstObjects.add(new ObjectCircleBarrierGravity(.15, .25, .6));

                //Text Barrier
                lstPoints.add(Menu.scale(0, .8));
                lstPoints.add(Menu.scale(1, .8));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "Gravity barriers pull balls towards them. However, the force from gravity " +
                        "may not be strong enough to overcome the ambient friction."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 2

                //Pocket
                lstObjects.add(new ObjectCirclePocket(.15, .75, .6));

                //Barrier
                lstObjects.add(new ObjectCircleBarrierAntiGravity(.15, .25, .6));

                //Text Barrier
                lstPoints.add(Menu.scale(0, .8));
                lstPoints.add(Menu.scale(1, .8));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "Antigravity barriers push balls away from themselves, but they also may not " +
                        "be able to overcome friction."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 3

                //Pocket
                lstObjects.add(new ObjectCirclePocket(.15, .25, .6));

                //Barriers
                for (double yVal = .06; yVal <= .86; yVal += .097) {
                    lstObjects.add(new ObjectCircleBarrierBreakable(.04, .55, yVal));
                }

                lstPoints.add(Menu.scale(.4, 0));
                lstPoints.add(Menu.scale(.5, 0));
                lstPoints.add(Menu.scale(.5, 1));
                lstPoints.add(Menu.scale(.4, 1));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                //Text Barrier
                lstPoints.add(Menu.scale(0, .9));
                lstPoints.add(Menu.scale(1, .9));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "Breakable barriers break after one ball bounces off of them."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();
                break;
            case 0:
                //Tutorial 4 - Heavy, Light, Dud tutorial
                lstLevel.clear();

                //Floor 1

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.1, .75, .325));

                //Balls
                lstObjects.add(new ObjectCircleBall(.11, .25, .325));
                lstObjects.add(new ObjectCircleBallDud(.09, .5, .325));

                //Barriers
                lstPoints.add(Menu.scale(.7, 0));
                lstPoints.add(Menu.scale(1, 0));
                lstPoints.add(Menu.scale(1, .65));
                lstPoints.add(Menu.scale(.7, .65));
                lstPoints.add(Menu.scale(.85, .45));
                lstPoints.add(Menu.scale(.85, .2));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                //Text Barrier
                lstPoints.add(Menu.scale(0, .65));
                lstPoints.add(Menu.scale(1, .65));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "The green ball is a dud ball, and can only be moved by hitting other balls into it. " +
                        "Try to avoid getting it stuck in a corner. Knock it into the pocket - if you miss your first shot, you can try to bank it in."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 2

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.1, .35, .325));

                //Balls
                lstObjects.add(new ObjectCircleBallLight(.11, .55, .325));

                //Barriers
                lstPoints.add(Menu.scale(0, .2));
                lstPoints.add(Menu.scale(0, .65));
                lstPoints.add(Menu.scale(.2, .65));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                //Text Barrier
                lstPoints.add(Menu.scale(0, .65));
                lstPoints.add(Menu.scale(1, .65));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "Mass is equal to volume times density. Yellow balls have less density, " +
                        "and therefore less mass, than most balls of their size. Because of this, the same force moves them more, but " +
                        "they hit softer in collisions than other balls of the same size and speed."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 3

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.1, .75, .325));

                //Balls
                lstObjects.add(new ObjectCircleBallHeavy(.11, .175, .325));

                //Barriers
                lstPoints.add(Menu.scale(.75, 0));
                lstPoints.add(Menu.scale(1, 0));
                lstPoints.add(Menu.scale(1, .65));
                lstPoints.add(Menu.scale(.75, .65));
                lstPoints.add(Menu.scale(.965, .35));
                lstPoints.add(Menu.scale(.965, .3));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                //Text Barrier
                lstPoints.add(Menu.scale(0, .65));
                lstPoints.add(Menu.scale(1, .65));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrierText(lstPoints, "Purple balls are denser, and therefore have more mass, than most balls " +
                        "of their size. This makes the same force move them less, but makes them hit harder in collisions than other balls " +
                        "of the same size and speed. If you are having trouble moving the purple ball, try bouncing it off the wall, because " +
                        "you can apply more force the further you pull, and you cannot pull very far close to the wall."));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();
                break;
            case 1:
                //Level 1
                lstLevel.clear();

                //Floor 1

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.13, .75, .75));
                lstObjects.add(new ObjectCirclePocket(.105, .75, .5));
                lstObjects.add(new ObjectCirclePocket(.08, .75, .25));

                //Balls
                lstObjects.add(new ObjectCircleBall(.075, .25, .75));
                lstObjects.add(new ObjectCircleBall(.1, .25, .5));
                lstObjects.add(new ObjectCircleBall(.125, .25, .25));

                //Ball Barrier
                lstObjects.add(new ObjectCircleBarrier(.1, .5, .5));

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                break;

            case 2:
                //Level Two
                lstLevel.clear();

                //Floor One

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.2, .85, .5));

                //Balls
                lstObjects.add(new ObjectCircleBallSplit(.45, .285, .5));

                //Wall
                //Triangle
                lstPoints.add(Menu.scale(.5, 0));
                lstPoints.add(Menu.scale(.6, .3));
                lstPoints.add(Menu.scale(.6, 0));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.5, 1));
                lstPoints.add(Menu.scale(.6, .7));
                lstPoints.add(Menu.scale(.6, 1));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor Two

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.1, .25, .25));
                lstObjects.add(new ObjectCirclePocket(.1, .25, .5));
                lstObjects.add(new ObjectCirclePocket(.1, .25, .75));

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();
                break;
            case 3:
                //Level Three
                lstLevel.clear();

                //Floor One

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.15, .75, .5));

                //Balls
                lstObjects.add(new ObjectCircleBall(.1, .25, .5));

                //Barriers
                lstObjects.add(new ObjectCircleBarrierGravity(.175, 0, .5));

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();


                //Floor Two

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.15, .25, .5));

                //Barriers
                lstObjects.add(new ObjectCircleBarrierAntiGravity(.2, 0, .5));

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                break;
            case 4:
                //Level 4
                lstLevel.clear();

                //Floor 1

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.11, .16, .25));
                lstObjects.add(new ObjectCirclePocket(.11, .125, .5));
                lstObjects.add(new ObjectCirclePocket(.11, .16, .75));

                //Balls
                for (double one = .525; one <= .875; one += .175) {
                    for (double two = .15; two <= .85; two += .175) {
                        lstObjects.add(new ObjectCircleBallCombine(.075, one, two));
                    }
                }

                //Barriers
                lstPoints.add(Menu.scale(.38, 0));
                lstPoints.add(Menu.scale(.48, 0));
                lstPoints.add(Menu.scale(.38, .424));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.38, 1));
                lstPoints.add(Menu.scale(.48, 1));
                lstPoints.add(Menu.scale(.38, .576));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 2

                //Pockets
                for (double y = .25; y <= .75; y += .25) {
                    lstObjects.add(new ObjectCirclePocket(.11, .85, y));
                }

                //Barriers
                lstPoints.add(Menu.scale(.3, 0));
                lstPoints.add(Menu.scale(.4, .3));
                lstPoints.add(Menu.scale(.7, .3));
                lstPoints.add(Menu.scale(.8, 0));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.3, 1));
                lstPoints.add(Menu.scale(.4, .7));
                lstPoints.add(Menu.scale(.7, .7));
                lstPoints.add(Menu.scale(.8, 1));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                for (double x = .45; x <= .651; x += .05) {
                    for (double y = .35; y <= .65; y += .1) {
                        lstObjects.add(new ObjectCircleBarrierBreakable(.025, x, y));
                    }
                }


                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                break;
            case 5:
                //Level 5
                lstLevel.clear();

                //Pockets
                for (double one = .7; one <= .9; one += .2) {
                    for (double two = .2; two <= .8; two += .2)
                        lstObjects.add(new ObjectCirclePocket(.08, one, two));
                }

                //Balls
                for (double one = .3; one <= .5; one += .2) {
                    for (double two = .2; two <= .8; two += .2)
                        lstObjects.add(new ObjectCircleBallDud(.07, one, two));
                }

                lstObjects.add(new ObjectCircleBallHeavy(.1, .15, .2));
                lstObjects.add(new ObjectCircleBall(.1, .15, .5));
                lstObjects.add(new ObjectCircleBallLight(.1, .15, .8));

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                break;
            case 6:
                //Level 6
                lstLevel.clear();

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.1, .775, .5));

                //Balls
                lstObjects.add(new ObjectCircleBallDud(.075, .25, .5));
                lstObjects.add(new ObjectCircleBall(.1, .1, .5));

                //Barriers
                lstPoints.add(Menu.scale(.25, .424));
                lstPoints.add(Menu.scale(.5, 0));
                lstPoints.add(Menu.scale(1, 0));
                lstPoints.add(Menu.scale(1, 1));
                lstPoints.add(Menu.scale(.5, 1));
                lstPoints.add(Menu.scale(.25, .576));
                lstPoints.add(Menu.scale(.5, .85));
                lstPoints.add(Menu.scale(.9, .75));
                lstPoints.add(Menu.scale(.9, .25));
                lstPoints.add(Menu.scale(.5, .15));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstObjects.add(new ObjectCircleBarrier(.05, .5, .5));

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                break;
            case 7:
                //Level 7
                lstLevel.clear();

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.125, .9, .5));

                //Balls
                lstObjects.add(new ObjectCircleBall(.075, .10, .15));
                lstObjects.add(new ObjectCircleBallCombine(.1, .35, .9));
                lstObjects.add(new ObjectCircleBallCombine(.1, .35, .7));
                lstObjects.add(new ObjectCircleBallHeavy(.120, .6, .5));

                //Barriers
                lstPoints.add(Menu.scale(0, .3));
                lstPoints.add(Menu.scale(.25, .3));
                lstPoints.add(Menu.scale(.25, 1));
                lstPoints.add(Menu.scale(0, 1));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.2, 0));
                lstPoints.add(Menu.scale(.25, 0));
                lstPoints.add(Menu.scale(.25, .3));
                lstPoints.add(Menu.scale(.2, .3));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.45, 0));
                lstPoints.add(Menu.scale(.5, 0));
                lstPoints.add(Menu.scale(.5, .8));
                lstPoints.add(Menu.scale(.45, .8));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.45, .8));
                lstPoints.add(Menu.scale(.5, .8));
                lstPoints.add(Menu.scale(.5, 1));
                lstPoints.add(Menu.scale(.45, 1));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.7, 0));
                lstPoints.add(Menu.scale(.75, 0));
                lstPoints.add(Menu.scale(.75, .424));
                lstPoints.add(Menu.scale(.7, .424));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.7, .424));
                lstPoints.add(Menu.scale(.75, .424));
                lstPoints.add(Menu.scale(.75, .576));
                lstPoints.add(Menu.scale(.7, .576));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.7, .576));
                lstPoints.add(Menu.scale(.75, .576));
                lstPoints.add(Menu.scale(.75, 1));
                lstPoints.add(Menu.scale(.7, 1));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                break;
            case 8:
                //Level 8
                lstLevel.clear();

                //Floor 1

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.12, .8, .5));

                //Balls
                lstObjects.add(new ObjectCircleBallLight(.1, .15, .75));

                //Barriers
                lstObjects.add(new ObjectCircleBarrierAntiGravity(.2, .35, 0));
                lstObjects.add(new ObjectCircleBarrierAntiGravity(.2, .85, 0));

                lstPoints.add(Menu.scale(.25, 1));
                lstPoints.add(Menu.scale(.325, .525));
                lstPoints.add(Menu.scale(.525, .475));
                lstPoints.add(Menu.scale(.75, .8));
                lstPoints.add(Menu.scale(.6, 1));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();


                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();
                break;
            case 9:
                //Level 9
                lstLevel.clear();

                //Floor One

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.11, .16, .5));
                lstObjects.add(new ObjectCirclePocket(.11, .75, .75));

                //Balls
                lstObjects.add(new ObjectCircleBallDud(.1, .35, .5));
                lstObjects.add(new ObjectCircleBallLight(.075, .75, .25));

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor Two

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.11, .25, .75));
                lstObjects.add(new ObjectCirclePocket(.11, .75, .5));

                //Balls
                lstObjects.add(new ObjectCircleBallHeavy(.115, .25, .23));

                //Walls
                lstPoints.add(Menu.scale(.48, 1));
                lstPoints.add(Menu.scale(.52, 1));
                lstPoints.add(Menu.scale(.52, 0));
                lstPoints.add(Menu.scale(.48, 0));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                //Floor 3

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.11, .75, .75));
                lstObjects.add(new ObjectCirclePocket(.11, .25, .25));

                //Balls
                lstObjects.add(new ObjectCircleBallHeavy(.115, .75, .25));

                //Walls
                lstPoints.add(Menu.scale(.48, 1));
                lstPoints.add(Menu.scale(.52, 1));
                lstPoints.add(Menu.scale(.52, 0));
                lstPoints.add(Menu.scale(.48, 0));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();
                break;
            case 10:
                //Level 10
                lstLevel.clear();

                //Pockets
                lstObjects.add(new ObjectCirclePocket(.12, .1, .165));

                //Balls
                lstObjects.add(new ObjectCircleBall(.1, .9, .835));

                //Barriers
                lstPoints.add(Menu.scale(.175, 0));
                lstPoints.add(Menu.scale(.225, 0));
                lstPoints.add(Menu.scale(.225, .33));
                lstPoints.add(Menu.scale(.175, .33));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.375, 0));
                lstPoints.add(Menu.scale(.425, 0));
                lstPoints.add(Menu.scale(.425, .33));
                lstPoints.add(Menu.scale(.375, .33));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.575, 0));
                lstPoints.add(Menu.scale(.625, 0));
                lstPoints.add(Menu.scale(.625, .33));
                lstPoints.add(Menu.scale(.575, .33));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.775, 0));
                lstPoints.add(Menu.scale(.825, 0));
                lstPoints.add(Menu.scale(.825, .33));
                lstPoints.add(Menu.scale(.775, .33));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(0, .3));
                lstPoints.add(Menu.scale(.2, .3));
                lstPoints.add(Menu.scale(.2, .35));
                lstPoints.add(Menu.scale(0, .35));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.2, .3));
                lstPoints.add(Menu.scale(.4, .3));
                lstPoints.add(Menu.scale(.4, .35));
                lstPoints.add(Menu.scale(.2, .35));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.4, .3));
                lstPoints.add(Menu.scale(.6, .3));
                lstPoints.add(Menu.scale(.6, .35));
                lstPoints.add(Menu.scale(.4, .35));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.6, .3));
                lstPoints.add(Menu.scale(.8, .3));
                lstPoints.add(Menu.scale(.8, .35));
                lstPoints.add(Menu.scale(.6, .35));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.8, .3));
                lstPoints.add(Menu.scale(1, .3));
                lstPoints.add(Menu.scale(1, .35));
                lstPoints.add(Menu.scale(.8, .35));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.175, .33));
                lstPoints.add(Menu.scale(.225, .33));
                lstPoints.add(Menu.scale(.225, .66));
                lstPoints.add(Menu.scale(.175, .66));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.375, .33));
                lstPoints.add(Menu.scale(.425, .33));
                lstPoints.add(Menu.scale(.425, .66));
                lstPoints.add(Menu.scale(.375, .66));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.575, .33));
                lstPoints.add(Menu.scale(.625, .33));
                lstPoints.add(Menu.scale(.625, .66));
                lstPoints.add(Menu.scale(.575, .66));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.775, .33));
                lstPoints.add(Menu.scale(.825, .33));
                lstPoints.add(Menu.scale(.825, .66));
                lstPoints.add(Menu.scale(.775, .66));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(0, .65));
                lstPoints.add(Menu.scale(.2, .65));
                lstPoints.add(Menu.scale(.2, .7));
                lstPoints.add(Menu.scale(0, .7));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.2, .65));
                lstPoints.add(Menu.scale(.4, .65));
                lstPoints.add(Menu.scale(.4, .7));
                lstPoints.add(Menu.scale(.2, .7));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.4, .65));
                lstPoints.add(Menu.scale(.6, .65));
                lstPoints.add(Menu.scale(.6, .7));
                lstPoints.add(Menu.scale(.4, .7));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.6, .65));
                lstPoints.add(Menu.scale(.8, .65));
                lstPoints.add(Menu.scale(.8, .7));
                lstPoints.add(Menu.scale(.6, .7));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.8, .65));
                lstPoints.add(Menu.scale(1, .65));
                lstPoints.add(Menu.scale(1, .7));
                lstPoints.add(Menu.scale(.8, .7));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.175, .66));
                lstPoints.add(Menu.scale(.225, .66));
                lstPoints.add(Menu.scale(.225, 1));
                lstPoints.add(Menu.scale(.175, 1));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.375, .66));
                lstPoints.add(Menu.scale(.425, .66));
                lstPoints.add(Menu.scale(.425, 1));
                lstPoints.add(Menu.scale(.375, 1));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.575, .66));
                lstPoints.add(Menu.scale(.625, .66));
                lstPoints.add(Menu.scale(.625, 1));
                lstPoints.add(Menu.scale(.575, 1));
                lstObjects.add(new ObjectPolygonBarrier(lstPoints));
                lstPoints.clear();

                lstPoints.add(Menu.scale(.775, .66));
                lstPoints.add(Menu.scale(.825, .66));
                lstPoints.add(Menu.scale(.825, 1));
                lstPoints.add(Menu.scale(.775, 1));
                lstObjects.add(new ObjectPolygonBarrierBreakable(lstPoints));
                lstPoints.clear();

                lstLevel.add(new ArrayList<>(lstObjects));
                lstObjects.clear();

                break;
            default:
                break;

        }
        return lstLevel;
    }

    public static int getNumLevels() {
        return 10;
    }

    //gets the ID from strings for each level, if it is a valid level number
    public static String getLeaderboardID(int intLevel) {
        String id = null;

        switch (intLevel) {
            case 1:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_1);
                break;
            case 2:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_2);
                break;
            case 3:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_3);
                break;
            case 4:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_4);
                break;
            case 5:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_5);
                break;
            case 6:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_6);
                break;
            case 7:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_7);
                break;
            case 8:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_8);
                break;
            case 9:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_9);
                break;
            case 10:
                id = GameGUI.ga.getResources().getString(R.string.lead_level_10);
                break;
        }

        return id;
    }

    //gets the hint for each level, if it is a valid level number
    public static String getHint(int intLevel) {
        switch (intLevel) {
            case -3:
                return "Tap inside the red balls, hold, and drag outside to move them. Make sure you fill all of the black pockets.";
            case -2:
                return "Combine the orange balls to get them out of the way, then split the blue ball and hit the three little ones towards the pockets.";
            case -1:
                return "Try bouncing off of the walls to get more leverage to overcome gravity or antigravity. The further you are from the ball, the stronger the force added to the ball is.";
            case 0:
                return "To get the green dud ball in the pocket, hit it with the other balls! Try to get on the other side of it and hit it towards the pocket.";
            case 1:
                return "You have to get each ball in its own pocket. Start with the biggest one if you are getting them in the wrong holes. Try bouncing them off the walls towards their pockets.";
            case 2:
                return "Make sure you send a ball into the pocket that can still split. If you split while moving, the smaller balls retain speed. See if you can split it and make them go straight in.";
            case 3:
                return "Bounce the ball off of the barriers or the walls in order to have enough power to overcome gravity or antigravity. Remember, gravity and antigravity are much stronger the closer you are to their source.";
            case 4:
                return "Use the middle balls and be accurate. If too many combine, they can get in your way. Give less power so the breakable don't rebound the balls into each other.";
            case 5:
                return "Hit the green dud balls in with the other balls. Be aware of their collision strengths. Remember - red is regular density, purple is denser, and yellow is less dense. Make sure to keep the other balls to the left of the dud balls.";
            case 6:
                return "Good luck, you've got one shot. If you miss, you probably should restart. If you are having trouble banking, try turning on collision pausing.";
            case 7:
                return "Put the purple and orange balls in corners once the red ball is far enough through to squeeze the red ball in.";
            case 8:
                return "Get the yellow ball over the hump, then try to hit it just right to fight antigravity and go in the hole. Try turning on movement directions to see how antigravtiy affects the ball.";
            case 9:
                return "Break the wall in the second floor, and try to get the green dud ball into the right side pocket in order to be able to complete the next floor.";
            case 10:
                return "Some of the walls are breakable - break your way through the maze!";
            default:
                return "";
        }
    }

}
