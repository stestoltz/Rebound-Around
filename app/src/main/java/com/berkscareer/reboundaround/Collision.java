package com.berkscareer.reboundaround;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

//An instance of this class is created on each collision to display data about the collision to the user while the game is paused
public class Collision {

    //the width of all collision related draws
    private final float STROKE_WIDTH = (float)Menu.scale(.003);

    //the color of all collision related draws
    private int collisionColor = Color.rgb(35, 243, 142);

    //the ball that is colliding
    private ObjectCircleBall ocb;
    //the object it is colliding with
    private Object obj = null;

    //inital vars - must be set because they change after a collision
    private double initialXMove;
    private double initialYMove;

    //if colliding with a polygon, the closest point to the ball
    private Point pntClosest;

    //the paint used for the path
    private Paint pathPaint;

    //constructor for circle-circle collisions
    public Collision(ObjectCircleBall ocb, ObjectCircle oc) {
        this.ocb = ocb;
        this.obj = oc;
        buildPaint();
    }

    //constructor for circle-polygon collisions
    public Collision(ObjectCircleBall ocb, Point closest) {
        this.ocb = ocb;
        pntClosest = closest;
        buildPaint();
    }

    public void buildPaint() {
        //create the paint object
        pathPaint = new Paint();
        pathPaint.setColor(collisionColor);
        pathPaint.setStrokeWidth(STROKE_WIDTH);
        pathPaint.setStyle(Paint.Style.STROKE);
    }

    public void setInitialSpeeds(double x, double y) {
        //set initial moves to draw previous movement
        initialXMove = x;
        initialYMove = y;
    }

    public void draw(Canvas canvas) {

        //if the ball was moving, show previous movement
        if (ocb.getMoving(initialXMove, initialYMove)) {

            //angle of movement
            double angleMove = Math.atan2(initialYMove, initialXMove);

            //added distance in the radius
            double xRadMove = Math.cos(angleMove) * ocb.getRadius();
            double yRadMove = Math.sin(angleMove) * ocb.getRadius();

            Path previous = new Path();

            //draw an increased move so it is visible
            initialYMove *= 10.0;
            initialXMove *= 10.0;

            //set the start point and end point of the path
            previous.moveTo((float) ocb.getXLoc(), (float) ocb.getYLoc());
            previous.lineTo((float) (ocb.getXLoc() - initialXMove - xRadMove), (float) (ocb.getYLoc() - initialYMove - yRadMove));
            previous.close();

            //draw the path
            canvas.drawPath(previous, pathPaint);
        }

        //circle-circle collision
        if (obj instanceof ObjectCircle) {
            //typecast the ball
            ObjectCircle oc = (ObjectCircle)obj;

            //length of ball to ball line
            double length = Math.sqrt(Math.pow(ocb.getXLoc() - oc.getXLoc(), 2.0) + Math.pow(ocb.getYLoc() - oc.getYLoc(), 2.0));

            //distance between in both dimensions
            double xDiff = ocb.getXLoc() - oc.getXLoc();
            double yDiff = ocb.getYLoc() - oc.getYLoc();

            //getting the angle of the line between the two circles
            double angleCollision = Math.atan2(yDiff, xDiff);

            //call the sub, passing the half the ball to ball line to reach the midpoint, and the angle
            Path path = getLinePath(length / 2.0, angleCollision);

            //with the path returned, draw it
            canvas.drawPath(path, pathPaint);

        //circle-polygon collisions
        } else if (pntClosest != null) {

            //length of ball to point line
            double length = Math.sqrt(Math.pow(ocb.getXLoc() - (double)pntClosest.x, 2.0) + Math.pow(ocb.getYLoc() - (double)pntClosest.y, 2.0));

            //distance between in both dimensions
            double xDiff = ocb.getXLoc() - (double)pntClosest.x;
            double yDiff = ocb.getYLoc() - (double)pntClosest.y;

            //Getting angle of a line between two circles
            double angleCollision = Math.atan2(yDiff, xDiff);

            //call the sub, passing the ball to point line to reach the midpoint, and the angle
            Path path = getLinePath(length, angleCollision);

            //with the path returned, draw it
            canvas.drawPath(path, pathPaint);

        }

        //the projected movement is continuously drawn from GameGUI while movement predictions is on;
        //if it is off, it is drawn just this tick from GameGUI
    }

    //build the line between the ball and the object
    private Path getLinePath(double length, double angleCollision) {
        Path path = new Path();

        //added distance in the radius
        double xRadCollide = Math.cos(angleCollision) * ocb.getRadius();
        double yRadCollide = Math.sin(angleCollision) * ocb.getRadius();

        //add 90 degrees (PI / 2) to make a perpendicular angle
        angleCollision += (Math.PI) / 2.0;

        //get the point of collision
        Point pntCollision = new Point((int)(ocb.getXLoc() - xRadCollide), (int)(ocb.getYLoc() - yRadCollide));

        //draw a line through the point of collision that is twice as long as the length
        path.moveTo((float)(pntCollision.x + Math.cos(angleCollision) * (length)),
                (float)(pntCollision.y + Math.sin(angleCollision) * (length)));
        path.lineTo((float)(pntCollision.x - Math.cos(angleCollision) * (length)),
                (float)(pntCollision.y - Math.sin(angleCollision) * (length)));
        path.close();

        return path;
    }
}
