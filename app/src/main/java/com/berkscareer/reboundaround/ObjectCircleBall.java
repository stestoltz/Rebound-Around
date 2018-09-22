package com.berkscareer.reboundaround;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.gms.games.Games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObjectCircleBall extends ObjectCircle {

    //Properties of a disk (physics)
    protected double xMove, yMove;
    private double density;

    //collision - do not move for one tick, to show movement
    //            and because we have already displaced ourselves
    //            into the wall
    private boolean blnNoMove = false;

    //Time is a part of some equations, but we assume everything is on a basis of a one second interval.
    private final double TIME_INTERVAL = 1.0;

    //Friction with the arena (slows the ball down)
    private double forceOfFriction;

    public ObjectCircleBall(double dia, double x, double y) {
        super();
        constructor((int)Menu.scale(dia), Menu.scale(x, y));
    }

    public ObjectCircleBall(int diameter, Point location) {
        super();
        constructor(diameter, location);
    }

    private void constructor(int diameter, Point location) {

        radius = diameter / 2;
        xLoc = location.x;
        yLoc = location.y;

        //AREA OF CIRCLE = PI * RADIUS ^ 2
        double areaOfCircle = Math.PI * Math.pow(Menu.getPercent(radius), 2);
        //MASS OF CYLINDER = AoC * HEIGHT * DENSITY
        density = 4000.0;
        if (this instanceof ObjectCircleBallHeavy) {
            density *= (4.0/3.0);
        } else if (this instanceof ObjectCircleBallLight) {
            density *= (2.0/3.0);
        }
        double height = 1.0;
        mass = areaOfCircle * height * density;

        //Friction Values
        double coefficientOfFriction = 1.0 / 50000.0; //Friction given off by the field. EDITABLE
        double forceOfGravity = 9.81; //Assuming horizontal plane up = down force. This is also the normal force due to this reason. NOT EDITABLE

        forceOfFriction = coefficientOfFriction * TIME_INTERVAL * forceOfGravity; //This will always be the same number, it is the true force of friction applied.

        paintColor.setColor(Color.rgb(128, 0, 0));
        //paintColor.setAlpha(0);

    }

    public void launch(Point pntFinger) {

        double direction = Math.atan2((yLoc - pntFinger.y), (xLoc - pntFinger.x));
        double distance = Menu.getPercent(Math.sqrt(Math.pow(this.getXLoc() - pntFinger.x, 2.0) + Math.pow(this.getYLoc() - pntFinger.y, 2.0)) - this.getRadius());

        //Force is 1:1 with distance.
        //This is our decision on how
        //powerful launch should be.
        double forceOfApplied = distance;

        //CHANGE IN VELOCITY = (FORCE APPLIED * TIME) / MASS
        double changeInVelocity = (forceOfApplied * TIME_INTERVAL) / mass;

        //Apply speed in opposite direction of finger
        xMove += Menu.scale(changeInVelocity) * Math.cos(direction);
        yMove += Menu.scale(changeInVelocity) * Math.sin(direction);
    }

    public void applyFriction() {

        //Fun Fact #02: Friction only exists if an object is or wants to move
        //              against another object's surface. It does not exist
        //              when an object has no relative velocity.

        if (getMoving()) {

            //We separate our velocity into two parts
            double direction = Math.atan2(yMove, xMove); //Our direction
            //noinspection SuspiciousNameCombination
            double speed = Menu.getPercent(Math.sqrt(Math.pow(xMove, 2.0) + Math.pow(yMove, 2.0))); //Our speed

            //Fun Fact #01: The force of friction will always be the same with dry environments,
            //              but with wet environments it increases as you go faster. This only
            //              happens because you're colliding with more molecules per second,
            //              but we see air resistance as negligible.

            speed -= forceOfFriction;

            //This will only fire if friction has effectively brought the object to a stop.
            if (speed < 0) {
                speed = 0;
            }

            //Reapply the speed into the direction it was heading.
            xMove = Menu.scale(speed) * Math.cos(direction);
            yMove = Menu.scale(speed) * Math.sin(direction);

        }
    }

    public boolean move() {

        boolean isMoving = getMoving();
        if (isMoving) {
            if (blnNoMove) {
                //This is only used when we displace the
                //ball to show that it has collided with
                //the object, this displacement counts
                //as movement.
                blnNoMove = false;
            } else {
                xLoc += xMove;
                yLoc += yMove;
            }

        }

        //Returns whether the ball is moving
        return isMoving;
    }

    public boolean getMoving() {
        //noinspection SuspiciousNameCombination
        double speedSquared = (Math.pow(xMove, 2.0) + Math.pow(yMove, 2.0));
        return (Menu.getPercent(speedSquared) > 0);
    }

    //used to check if ball was moving with previous movements
    public boolean getMoving(double x, double y) {
        //noinspection SuspiciousNameCombination
        double speedSquared = (Math.pow(x, 2.0) + Math.pow(y, 2.0));
        return (Menu.getPercent(speedSquared) > 0);
    }

    public void draw(Canvas canvas) {

        //Draws redefines visual for objects, then places it on the form
        canvas.drawCircle((float) xLoc, (float) yLoc, radius, paintColor);
        if (blnBorder) {
            canvas.drawCircle((float) xLoc, (float) yLoc, radius, paintBorder);
        }
    }

    public boolean isColliding(List<Object> lstObjects, boolean blnAction) {

        boolean blnCollide = false;

        //Outside walls
        if (isCollidingWalls(blnAction)) {
            blnCollide = true;
        }

        //Find this ball in the list of objects
        int elementLoc = lstObjects.indexOf(this);

        //Test collision with all objects
        for (Object obj : lstObjects) {
            if (obj.getEnabled()) {

                if (obj instanceof ObjectCirclePocket) { //Pockets are special circle in that they do not bounce balls off

                    ObjectCirclePocket ocp = (ObjectCirclePocket) obj;
                    this.collideObjectCirclePocket(ocp);

                } else if (obj instanceof ObjectCircle) { //Any other circle

                    //We don't want to hit balls we've already gone through or ourselves
                    int elementLocBall = lstObjects.indexOf(obj);

                    if (elementLoc < elementLocBall || !(obj instanceof ObjectCircleBall)) {
                        if (this.isCollidingCircle(((ObjectCircle) obj), blnAction)) {
                            blnCollide = true;
                        }
                    }

                } else if (obj instanceof ObjectPolygonBarrier && !blnNoMove) { //Polygons

                    ObjectPolygonBarrier opb = (ObjectPolygonBarrier) obj;
                    if (this.isCollidingPolygon(opb.getVertices(), blnAction)) {
                        blnCollide = true;

                        if (opb instanceof ObjectPolygonBarrierBreakable) {
                            opb.disable(); //Break

                            if (GameGUI.ga.getOptions()[GameGUI.ga.getOptions().length - 1] && Menu.googleAPI != null && Menu.googleAPI.isConnected()) {
                                Games.Achievements.increment(Menu.googleAPI, GameGUI.ga.getResources().getString(R.string.ach_barrier_buster), 1); //Achievement
                            }
                        }
                    }
                }
            }
        }

        if (blnCollide && blnAction) {
            this.applyFriction();
        } //Collision is not perfect, we lost some speed to friction

        return blnCollide;
    }

    public boolean isCollidingWalls(boolean blnAction) {
        boolean blnCollide = false;

        //Outer Horizontal Walls
        if (xLoc + xMove - radius < 0) {
            blnCollide = true;
            if (blnAction) {
                xMove = Math.abs(xMove);
                xLoc = radius;
            }
        } else if (xLoc + xMove + radius > Menu.size.x) {
            blnCollide = true;
            if (blnAction) {
                xMove = -Math.abs(xMove);
                xLoc = Menu.size.x - radius;
            }
        }

        //Outer Vertical Walls
        if (yLoc + yMove - radius < 0) {
            blnCollide = true;
            if (blnAction) {
                yMove = Math.abs(yMove);
                yLoc = radius;
            }
        } else if (yLoc + yMove + radius > Menu.size.y) {
            blnCollide = true;
            if (blnAction) {
                yMove = -Math.abs(yMove);
                yLoc = Menu.size.y - radius;
            }
        }

        return blnCollide;
    }

    private boolean isCollidingCircle(ObjectCircle oc, boolean blnAction) {

        //Where we will be next turn
        double xLocNext = xLoc + xMove;
        double yLocNext = yLoc + yMove;

        //Where the other ball will be next turn
        double xLocNext2 = oc.getXLoc();
        double yLocNext2 = oc.getYLoc();

        //Only could move if it is an ObjectCircleBall
        if (oc instanceof ObjectCircleBall) {

            xLocNext2 += ((ObjectCircleBall)oc).getXMove();
            yLocNext2 += ((ObjectCircleBall)oc).getYMove();

        }

        //Distance between next turn
        double xDiffNext = xLocNext - xLocNext2;
        double yDiffNext = yLocNext - yLocNext2;

        //If the distance between the points is less than the sum of both radii than they are colliding
        //noinspection SuspiciousNameCombination
        boolean blnCollide = (Math.pow(xDiffNext, 2) + Math.pow(yDiffNext, 2) <= Math.pow(radius + oc.getRadius(), 2));
        //if they're colliding, it's not colliding with itself, & it's allowed to act upon collision
        if (blnCollide && this != oc && blnAction) {

            if (this instanceof ObjectCircleBallCombine && oc instanceof ObjectCircleBallCombine) {
                boolean blnCombined;

                //Pull on each other
                blnCombined = ((ObjectCircleBallCombine)this).event((ObjectCircleBallCombine)oc);
                if (!blnCombined) {
                    ((ObjectCircleBallCombine)oc).event((ObjectCircleBallCombine)this);
                }

                //This doesn't count as a collision
                blnCollide = false;

            } else { //everything else can collide as a regular circle
                this.collideCircle(oc, true);
            }

            if (oc instanceof ObjectCircleBarrierBreakable) {
                oc.disable(); //"Breaks" the barrier
                if (GameGUI.ga.getOptions()[GameGUI.ga.getOptions().length - 1] && Menu.googleAPI != null && Menu.googleAPI.isConnected()) {
                    Games.Achievements.increment(Menu.googleAPI, GameGUI.ga.getResources().getString(R.string.ach_barrier_buster), 1); //Achievement
                }
            }
        }
        return blnCollide;
    }

    private void collideCircle(ObjectCircle oc, boolean blnFirst) {

        //Getting distance traveled ^2
        double speed = Math.sqrt(xMove * xMove + yMove * yMove);

        //Distance between now
        double xDiff = xLoc - oc.getXLoc();
        double yDiff = yLoc - oc.getYLoc();

        //Getting angle of a line between two circles
        double angleCollision = Math.atan2(yDiff, xDiff);

        //Getting direction of travel
        double direction = Math.atan2(yMove, xMove);

        //Getting separate x and y velocities
        double xVel = speed * Math.cos(direction - angleCollision);
        double yVel = speed * Math.sin(direction - angleCollision);
        double xVelFinal;

        //If the oc is another ball, we must take their momentum into account
        if (oc instanceof ObjectCircleBall) {
            ObjectCircleBall ocb = (ObjectCircleBall) oc;

            //Getting distance traveled ^2
            double speed2 = Math.sqrt(ocb.getXMove() * ocb.getXMove() + ocb.getYMove() * ocb.getYMove());

            //Getting direction of travel
            double direction2 = Math.atan2(ocb.getYMove(), ocb.getXMove());

            double xSpeed2 = speed2 * Math.cos(direction2 - angleCollision);

            //Take the other balls momentum into account
            xVelFinal = ((mass - ocb.getMass()) * xVel + (ocb.getMass() + ocb.getMass()) * xSpeed2) / (mass + ocb.getMass());

            //We need to apply this to the other ball to, but only this first ball calls the second
            if (blnFirst) {
                ocb.collideCircle(this, false);
            }

        } else {
            //Solid, non moving object
            xVelFinal = ((mass - oc.getMass()) * xVel) / (mass + oc.getMass());
        }

        //Moves the balls together so that we can collide properly and draw one tick touching
        xLoc += xMove;
        yLoc += yMove;

        //create a collision, unless we are colliding with a gravity barrier
        if (!(oc instanceof ObjectCircleBarrierGravity && !(oc instanceof ObjectCircleBarrierAntiGravity))) {
            Collision collision = new Collision(this, oc);
            collision.setInitialSpeeds(xMove, yMove);
            GameGUI.lstCollisions.add(collision);
        }

        //Reapply speed to new directions
        xMove = Math.cos(angleCollision) * xVelFinal + Math.cos(angleCollision + Math.PI / 2.0) * yVel;
        yMove = Math.sin(angleCollision) * xVelFinal + Math.sin(angleCollision + Math.PI / 2.0) * yVel;

        //If the object "clips" with another object,
        //it should move back in the direction
        //from which it came.
        if (!(oc instanceof ObjectCircleBarrierGravity)) {
            double distance = Math.sqrt(Math.pow(oc.getXLoc() - this.getXLoc(), 2) + Math.pow(oc.getYLoc() - this.getYLoc(), 2));
            double collisionDirection = Math.atan2(this.getYLoc() - oc.getYLoc(), this.getXLoc() - oc.getXLoc());
            double minDistance = this.getRadius() + oc.getRadius();
            if ((distance) <= minDistance) {
                this.xLoc += (minDistance - distance) * Math.cos(collisionDirection);
                this.yLoc += (minDistance - distance) * Math.sin(collisionDirection);
                this.xMove += Math.cos(collisionDirection);
                this.yMove += Math.sin(collisionDirection);
            }
        }

        blnNoMove = true;
    }

    private boolean isCollidingPolygon(List<Point> pntVertices, boolean blnAction) {
        boolean blnCollide = false;

        List<Double> lenDists = new ArrayList<>();
        List<Point> pntClosests = new ArrayList<>();
        double lenDist;
        Point vecDist;
        Point pntClosest = null;

        //Need two points for line segment
        int point2 = pntVertices.size() - 1;

        //Check that it isn't colliding with any of our line segments
        for (int CurrentPoint = 0; CurrentPoint <= pntVertices.size() - 1; CurrentPoint += 1) {

            Point vecSegA = new Point(pntVertices.get(point2).x, pntVertices.get(point2).y);
            Point vecSegB = new Point(pntVertices.get(CurrentPoint).x, pntVertices.get(CurrentPoint).y);
            Point vecSegment = new Point(vecSegB.x - vecSegA.x, vecSegB.y - vecSegA.y);
            double lenSegment = Math.sqrt(vecSegment.x * vecSegment.x + vecSegment.y * vecSegment.y);
            double xuSegment = vecSegment.x / lenSegment;
            double yuSegment = vecSegment.y / lenSegment;

            //Point vecToCircle = new Point((int)(this.getXLoc() + this.getXMove() - vecSegA.x), (int)(this.getYLoc() + this.getYMove() - vecSegA.y));
            Point vecToCircle = new Point((int)(this.getXLoc() - vecSegA.x), (int)(this.getYLoc() - vecSegA.y));

            //Point closest is our collision point
            double lenProj = vecToCircle.x * xuSegment + vecToCircle.y * yuSegment;
            if (lenProj <= 0) {
                pntClosest = vecSegA;
            } else if (lenProj >= lenSegment) {
                pntClosest = vecSegB;
            } else {
                Point vecProj = new Point((int) (xuSegment * lenProj), (int) (yuSegment * lenProj));
                pntClosest = new Point(vecProj.x + vecSegA.x, vecProj.y + vecSegA.y);
            }

            //Point vecDistNext = new Point((int)(this.getXLoc() + this.getXMove() - pntClosest.x), (int)(this.getYLoc() + this.getYMove() - pntClosest.y));
            vecDist = new Point((int)(this.getXLoc() - pntClosest.x), (int)(this.getYLoc() - pntClosest.y));

            //double lenDistNext = Math.sqrt(vecDistNext.x * vecDistNext.x + vecDistNext.y * vecDistNext.y);
            lenDist = Math.sqrt(vecDist.x * vecDist.x + vecDist.y * vecDist.y);
            if (lenDist < this.getRadius()) {
                lenDists.add(lenDist);
                pntClosests.add(pntClosest);
            }

            point2 = CurrentPoint;
        }

        //if ***NOT*** empty
        if (!lenDists.isEmpty()) {
            //This was done because there was a bug where you could
            //collide with multiple

            //Find the closest point to collide with
            lenDist = lenDists.get(0);
            for (int index = 0; index < lenDists.size(); index++) {
                if (lenDist >= lenDists.get(index)) {
                    lenDist = lenDists.get(index);
                    pntClosest = pntClosests.get(index);
                }
            }

            if (blnAction) {

                collidePolygon(pntClosest);
            }

            blnCollide = true;
        }

        return blnCollide;
    }

    private void collidePolygon(Point pntClosest) {

        double distance = Math.sqrt(Math.pow(pntClosest.x - this.getXLoc(), 2) + Math.pow(pntClosest.y - this.getYLoc(), 2));
        double collisionDirection = Math.atan2(this.getYLoc() - pntClosest.y, this.getXLoc() - pntClosest.x);
        double offset = Menu.scale(.0025); //Move it just a little outward so it doesn't think it wants to collide again :(
        double minDistance = this.getRadius() + offset;
        if ((distance) < minDistance) {
            this.xLoc += (minDistance - distance) * Math.cos(collisionDirection);
            this.yLoc += (minDistance - distance) * Math.sin(collisionDirection);
        }

        //Distance between now
        double xDiff = xLoc - pntClosest.x;
        double yDiff = yLoc - pntClosest.y;

        //Getting distance traveled ^2
        double speed = Math.sqrt(xMove * xMove + yMove * yMove);
        //double magnitude2 = Math.sqrt(ocb.getXMove() * ocb.getXMove() + ocb.getYMove() * ocb.getYMove());

        //Getting direction of travel
        double direction = Math.atan2(yMove, xMove);

        //Getting angle of a line between the circle and point closest
        double angleCollision = Math.atan2(yDiff, xDiff);

        //Collide
        double xVel = speed * Math.cos(direction - angleCollision);
        double yVel = speed * Math.sin(direction - angleCollision);
        double infinity = Integer.MAX_VALUE;
        double xVelFinal = ((mass - infinity) * xVel) / (mass + infinity);

        Collision collision = new Collision(this, pntClosest);
        collision.setInitialSpeeds(xMove, yMove);
        GameGUI.lstCollisions.add(collision);

        //Calculate new velocities
        xMove = Math.cos(angleCollision) * xVelFinal + Math.cos(angleCollision + Math.PI / 2.0) * yVel;
        yMove = Math.sin(angleCollision) * xVelFinal + Math.sin(angleCollision + Math.PI / 2.0) * yVel;

        blnNoMove = true;
    }

    private void collideObjectCirclePocket(ObjectCirclePocket ocp) {

        double distance = Math.sqrt(Math.pow(ocp.getXLoc() - this.getXLoc(), 2) + Math.pow(ocp.getYLoc() - this.getYLoc(), 2));

        if (distance < ocp.getRadius() - this.getRadius() && !ocp.getShrink()) {

            //Turn off pocket and ball
            ocp.fillPocket(this);
            this.disable();

        } else if (distance < ocp.getRadius()) {

            //Ball fall towards the center of the hole
            double percentDifference = (ocp.getRadius() - distance) / ocp.getRadius();
            double direction = Math.atan2((ocp.getYLoc() - yLoc), (ocp.getXLoc() - xLoc));

            xMove += percentDifference * 2.5 * Math.cos(direction);
            yMove += percentDifference * 2.5 * Math.sin(direction);

        }
    }

    public void randomizeMovement() {
        //End game only, no real science to this
        Random rngMovement = new Random();
        //Random.nextInt(max) is inclusive of zero and exclusive of max; currently ranges from -2 to 2
        xMove = -2 + rngMovement.nextInt(5);
        yMove = -2 + rngMovement.nextInt(5);
    }

    public List<GameGUI.pointerFinger> getPointerFingers(List<GameGUI.pointerFinger> lstPF) {
        List<GameGUI.pointerFinger> lstFingers = new ArrayList<>();

        //iterates through each ball in each finger, adding to the new list all that are on this ball
        //this is mostly for combine and split ball, to keep fingers after changes
        for (GameGUI.pointerFinger myPF : lstPF) {
            for (ObjectCircleBall ocb : myPF.ocbPoints) {
                if (this.equals(ocb)) {
                    lstFingers.add(myPF);
                }
            }
        }

        return lstFingers;
    }

    //resets the ball; called after a floor is completed
    public void reset() {
        this.enabled = true;
    }

    //getters
    public double getDensity() {
        return density;
    }
    public Paint getPaint() {
        return paintColor;
    }
    public double getYMove() {
        return yMove;
    }
    public double getXMove() {
        return xMove;
    }
}
