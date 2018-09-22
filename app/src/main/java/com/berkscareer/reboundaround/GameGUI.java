package com.berkscareer.reboundaround;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.games.Games;

import java.util.ArrayList;
import java.util.List;

public class GameGUI extends SurfaceView implements SurfaceHolder.Callback {

    //thread and thread handler - run the constant loop
    private GameThread threadGame;
    private Handler handlerGame;

    //lists
    private List<List<Object>> lstFloors;                   //holds all floors on the current level
    private List<Object> lstObjects;                        //holds all objects on the current floor
    private List<Object> lstRemoval;                        //holds all objects to be removed once the loop is completed
    public static List<ObjectCircleBall> lstNextFloorAdd;   //holds all balls that fell through a pocket
    public static List<Object> lstAdd;                      //holds all objects to be added once the loop is completed
    private static List<pointerFinger> lstPointerFingers;   //holds all fingers currently down
    private List<pointerFinger> lstPFRemove;                //holds all fingers to be removed once the loop is completed
    public static List<Collision> lstCollisions;            //holds all collisions to be drawn

    //holds the current floor
    private int floorCounter = 0;

    //movement timer
    private String strTime;
    private long lngTimer;
    private long lngStartTime;
    private boolean blnTimerStarted;
    private boolean blnIsMoving;
    private int activePocketCounter;

    //current level
    private int intLevel;

    //display paints
    private Paint scorePaint;
    private Paint backgroundPaint;

    //button overlay
    private final int OVERLAY_MAX = 75;
    private int intOverlayTimer = 0;

    //global instance of GameActivity for access to Resources
    public static GameActivity ga;

    //level completed status
    private boolean blnLevelCompleted = false;

    //movement predictions and collisions
    private Path ocbMovement;
    private Paint collisionPaint;

    //pausing
    private boolean blnPause = false;
    private boolean blnPauseTime = false;
    private boolean blnDrawPause = false;
    private int pauseLength;

    //hints
    private boolean blnFirstHintShown = false;
    private boolean blnSecondHintShown = false;

    //used to prevent bugs while text barrier is fullscreen
    private boolean blnOnTextBarrier = false;

    public GameGUI(Context context) {
        super(context);
    }

    public GameGUI(Context context, GameActivity game, int levelNumber) {
        super(context);

        ga = game;

        //Dealing with balls between floors
        lstRemoval = new ArrayList<>();
        lstAdd = new ArrayList<>();
        lstPointerFingers = new ArrayList<>();
        lstPFRemove = new ArrayList<>();
        lstNextFloorAdd = new ArrayList<>();
        lstCollisions = new ArrayList<>();

        //Starts the loop handling all game events
        getHolder().addCallback(this);
        threadGame = new GameThread(getHolder(), this);
        handlerGame = new Handler();

        //Score paint
        scorePaint = new Paint();
        scorePaint.setColor(Color.parseColor("#000000"));
        scorePaint.setTextSize((float) ((Menu.size.x + Menu.size.y) / 50.0));

        //Background color
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);

        //collisions pausing and movement predictions
        collisionPaint = new Paint();
        collisionPaint.setStrokeWidth((float) Menu.scale(.003));
        collisionPaint.setColor(Color.rgb(35, 243, 142));
        collisionPaint.setStyle(Paint.Style.STROKE);
        ocbMovement = new Path();

        //Starts the level
        restartLevel(levelNumber);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Connect if we've lost connection
        if (ga.getOptions()[ga.getOptions().length - 1] && !Menu.googleAPI.isConnected()) {
            Menu.googleAPI.connect();
        }

        //Paint background first
        canvas.drawColor(backgroundPaint.getColor());

        blnIsMoving = false;

        //collide and move before draw
        for (Object obj : lstObjects) {
            if (obj instanceof ObjectCircleBall) {
                ObjectCircleBall ocb = (ObjectCircleBall) obj;

                //check collisions and collide
                ocb.isColliding(lstObjects, true);

                //move the balls
                if (ocb.move()) {
                    blnIsMoving = true;
                }
            }
        }

        //New objects added (cannot add/remove during for each loops)
        lstObjects.addAll(lstAdd);
        lstAdd.clear();

        //counts the number of pockets - used to end the level - waits until all animations are completed
        int pocketCounter = 0;
        //counts the number of non-shrinking pockets - ensures hints do not trigger while last pocket animation is going
        activePocketCounter = 0;

        //Main sequence, moves, slows down, everything to do with these balls
        for (Object obj : lstObjects) {

            if (obj.getEnabled()) {

                //while not during a fullscreen text barrier, draw each object
                if (!blnOnTextBarrier || obj instanceof ObjectPolygonBarrierText) {
                    obj.draw(canvas);
                }

                //for each ball, iterate through each gravity barrier and apply gravity
                if (obj instanceof ObjectCircleBall) {

                    ObjectCircleBall ocb = (ObjectCircleBall) obj;

                    for (Object _obj : lstObjects) {
                        if (_obj instanceof ObjectCircleBarrierGravity) {
                            ((ObjectCircleBarrierGravity) _obj).event(ocb);
                        }
                    }

                    //apply friction
                    if (!blnLevelCompleted) {
                        ocb.applyFriction();
                    }

                }

                //for each pocket, increment level complete counters
                if (obj instanceof ObjectCirclePocket) {
                    pocketCounter += 1;
                    if (!((ObjectCirclePocket)obj).getShrink()) {
                        activePocketCounter += 1;
                    }
                }

            } else {
                //if disabled, add to remove list
                lstRemoval.add(obj);
            }
        }

        //remove disabled objects (cannot add/remove during for each loops)
        lstObjects.removeAll(lstRemoval);
        lstRemoval.clear();

        if (!blnLevelCompleted) {

            //Timer & Score keeping
            int milliseconds = (int) lngTimer;
            int seconds = (milliseconds / 1000);
            milliseconds -= seconds * 1000;
            int minutes = (seconds / 60);
            seconds -= minutes * 60;

            //format time to MM:SS:MM
            strTime = "";
            if (minutes < 10) {
                strTime += "0" + minutes;
            } else {
                strTime += minutes;
            }
            strTime += ":";
            if (seconds < 10) {
                strTime += "0" + seconds;
            } else {
                strTime += seconds;
            }
            strTime += ":";
            if (milliseconds < 100) {
                if (milliseconds < 10) {
                    strTime += "00" + milliseconds;
                } else {
                    strTime += "0" + milliseconds;
                }
            } else {
                strTime += milliseconds;
            }

            //draw time
            canvas.drawText("Time: " + strTime, 5, 5 + scorePaint.getTextSize(), scorePaint);
            if (intLevel <= 0) {
                canvas.drawText("Tutorial " + (intLevel + 4) + ", Floor " + (floorCounter + 1) + "/" + lstFloors.size(), 5, 10 + 2 * scorePaint.getTextSize(), scorePaint);
            } else {
                canvas.drawText("Level " + intLevel + ", Floor " + (floorCounter + 1) + "/" + lstFloors.size(), 5, 10 + 2 * scorePaint.getTextSize(), scorePaint);
            }

            //display hints if when not moving, there are valid pockets, the time is greater than the cutoff, and the hints have not been shown yet
            if (!blnIsMoving && activePocketCounter > 0) {
                if (!blnFirstHintShown && lngTimer > 15000 /*15 seconds*/) {
                    blnFirstHintShown = true;
                    ga.displayHint();
                } else if (!blnSecondHintShown && lngTimer > 30000 /*30 seconds*/) {
                    blnSecondHintShown = true;
                    ga.displayHint();
                }
            }

            if (pocketCounter == 0 && lstFloors.size() - 1 > floorCounter) {
                //A floor has been complete, and there are more floors
                floorCounter += 1;

                //of the balls added earlier to the next floor, resets the ones that fell into pockets
                for (ObjectCircleBall ocb : lstNextFloorAdd) {
                    ocb.reset();
                }

                //clear lstObjects and repopulate it
                lstObjects.clear();
                lstObjects = lstFloors.get(floorCounter);
                lstObjects.addAll(lstNextFloorAdd);

                lstNextFloorAdd.clear();

            } else if (pocketCounter == 0 && lstFloors.size() - 1 == floorCounter) {

                //All floors on the level are completed, and all pockets on the floor are gone
                //Level is complete

                String leaderboardID = Levels.getLeaderboardID(intLevel);

                boolean blnPlay = false;

                if (ga.getOptions()[ga.getOptions().length - 1] && Menu.googleAPI != null && Menu.googleAPI.isConnected()) {
                    //submit the score to the leaderboard, if applicable
                    if (leaderboardID != null) {
                        Games.Leaderboards.submitScore(Menu.googleAPI, leaderboardID, lngTimer);
                        blnPlay = true;
                    }

                    //see if there are achievements to unlock
                    if (intLevel > 0) {
                        if (lngTimer < 2000) {
                            Games.Achievements.unlock(Menu.googleAPI, getResources().getString(R.string.ach_quick_time));
                        } else if (lngTimer > 120000) {
                            Games.Achievements.unlock(Menu.googleAPI, getResources().getString(R.string.ach_youre_a_slow_poke));
                        }
                    }
                }

                //calls GameActivity's level completed
                ga.levelCompleted(blnPlay);
                blnLevelCompleted = true;

            }
        }

        //for each finger on each ball, display the projection, if enabled
        if (!blnLevelCompleted) {
            for (pointerFinger myPF : lstPointerFingers) {
                for (ObjectCircleBall ocbPoint : myPF.ocbPoints) {
                    //regardless of projection enabled status, set the finger distance (used in launch)
                    myPF.projDist = Math.sqrt((Math.pow((myPF.pntFinger.x - ocbPoint.getXLoc()), 2) + Math.pow((myPF.pntFinger.y - ocbPoint.getYLoc()), 2)));

                    if (ga.getOptions()[1]) {
                        //if the finger is outside of an enabled ball
                        if (myPF.projDist > ocbPoint.getRadius() && ocbPoint.getEnabled()) {

                            //predict launch movement - friction is currently not subtracted

                            //movement direction
                            double direction = Math.atan2((ocbPoint.getYLoc() - myPF.pntFinger.y), (ocbPoint.getXLoc() - myPF.pntFinger.x));

                            //distance from ball to finger
                            double distance = Menu.getPercent(Math.sqrt(Math.pow(ocbPoint.getXLoc() - myPF.pntFinger.x, 2.0) + Math.pow(ocbPoint.getYLoc() - myPF.pntFinger.y, 2.0)) - ocbPoint.getRadius());

                            //change in velocity if finger is released
                            double changeInVelocity = (distance) / ocbPoint.getMass();

                            //new x and y moves if finger is released
                            double newXMove = Menu.scale(changeInVelocity) * Math.cos(direction);
                            double newYMove = Menu.scale(changeInVelocity) * Math.sin(direction);

                            //angle of movement, to get radius length in both dimensions at that angle
                            double newMoveAngle = Math.atan2(newYMove, newXMove);
                            float xRad = (float)(Math.cos(newMoveAngle) * ocbPoint.getRadius());
                            float yRad = (float)(Math.sin(newMoveAngle) * ocbPoint.getRadius());

                            //the tip of the line
                            float xTip = (float)ocbPoint.getXLoc() + xRad + (float)(10.0 * newXMove);
                            float yTip = (float)ocbPoint.getYLoc() + yRad + (float)(10.0 * newYMove);

                            //the angle of the line
                            double angle = Math.atan2(yTip - ocbPoint.getYLoc(), xTip - ocbPoint.getXLoc());

                            //clear the path, the move from the ball to the tip
                            myPF.ocbPath.rewind();
                            myPF.ocbPath.moveTo((float) ocbPoint.getXLoc(), (float) ocbPoint.getYLoc());
                            myPF.ocbPath.lineTo(xTip, yTip);
                            myPF.ocbPath.close();

                            float arrowHeadLength = (float)Menu.scale(.01);

                            //path out the two heads of the arrow
                            myPF.ocbPath.moveTo(xTip, yTip);
                            myPF.ocbPath.lineTo(xTip - (arrowHeadLength * (float) Math.cos(angle - (Math.PI / 4.0))),
                                    yTip - (arrowHeadLength * (float) Math.sin(angle - (Math.PI / 4.0))));
                            myPF.ocbPath.close();

                            myPF.ocbPath.moveTo(xTip, yTip);
                            myPF.ocbPath.lineTo(xTip - (arrowHeadLength * (float) Math.cos(angle + (Math.PI / 4.0))),
                                    yTip - (arrowHeadLength * (float) Math.sin(angle + (Math.PI / 4.0))));
                            myPF.ocbPath.close();

                            //draw the path
                            canvas.drawPath(myPF.ocbPath, myPF.projPaint);
                        }
                    }
                }
            }

            //******************
            //COLLISIONS PAUSING
            //******************

            //pause if the option is on or if we are in the last floor of the first tutorial
            if (GameGUI.ga.getOptions()[3] || (intLevel == -3 && floorCounter + 1 == lstFloors.size())) {
                //draw the collisions to the screen, then pause the thread
                int length = lstCollisions.size();

                //if there are collisions, draw them and pause
                if (length != 0) {

                    //draw each collision
                    for (Collision collision : lstCollisions) {
                        collision.draw(canvas);
                    }

                    //pause for half a second for each collision
                    pauseLength = length * 500;
                    blnPauseTime = true;
                }

            }
            //only draw collisions once
            lstCollisions.clear();

            //***********
            //PROJECTIONS
            //***********

            //show the movement of each ball if it is turned on, the game is paused, or we are in the tutorials
            if ((ga.getOptions()[4] || (blnDrawPause || blnPauseTime)) || intLevel < 1) {
                //if paused, set booleans and draw projection regardless of option state
                if (blnDrawPause) {
                    blnDrawPause = false;
                    blnPause = true;
                }

                //as long as we are not in a fullscreen text barrier, draw the projections
                if (!blnOnTextBarrier) {
                    for (Object obj : lstObjects) {
                        if (obj instanceof ObjectCircleBall) {

                            ObjectCircleBall ocb = (ObjectCircleBall) obj;

                            //for each moving ball, draw collision

                            if (ocb.getMoving()) {
                                //angle of movement
                                double angle = Math.atan2(ocb.getYMove(), ocb.getXMove());

                                //radius length in each dimension at that angle
                                float xRad = (float) (Math.cos(angle) * ocb.getRadius());
                                float yRad = (float) (Math.sin(angle) * ocb.getRadius());

                                //the tip of the arrow
                                float xTip = (float) ocb.getXLoc() + xRad + (float) (ocb.getXMove() * 10.0);
                                float yTip = (float) ocb.getYLoc() + yRad + (float) (ocb.getYMove() * 10.0);

                                //line from ball to tip
                                ocbMovement.moveTo((float) ocb.getXLoc(), (float) ocb.getYLoc());
                                ocbMovement.lineTo(xTip, yTip);
                                ocbMovement.close();

                                float arrowHeadLength = (float) Menu.scale(.015);

                                //build the head of the arrow
                                ocbMovement.moveTo(xTip, yTip);
                                ocbMovement.lineTo(xTip - (arrowHeadLength * (float) Math.cos(angle - (Math.PI / 4.0))),
                                        yTip - (arrowHeadLength * (float) Math.sin(angle - (Math.PI / 4.0))));
                                ocbMovement.close();

                                ocbMovement.moveTo(xTip, yTip);
                                ocbMovement.lineTo(xTip - (arrowHeadLength * (float) Math.cos(angle + (Math.PI / 4.0))),
                                        yTip - (arrowHeadLength * (float) Math.sin(angle + (Math.PI / 4.0))));
                                ocbMovement.close();

                                //draw the path
                                canvas.drawPath(ocbMovement, collisionPaint);
                                ocbMovement.reset();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent e) {

        if (!blnLevelCompleted) {
            //On finger down
            if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN || (e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {

                boolean onBall = false;

                for (Object obj: lstObjects) {
                    if (obj instanceof ObjectCircleBall && !(obj instanceof ObjectCircleBallDud)) {
                        ObjectCircleBall ocb = (ObjectCircleBall)obj;

                        //for each ball, get the distance of the current finger from the ball
                        double dblCurrentDist = Math.sqrt(Math.pow((e.getX(e.findPointerIndex(e.getPointerId(e.getActionIndex()))) - ocb.getXLoc()), 2) + Math.pow((e.getY(e.findPointerIndex(e.getPointerId(e.getActionIndex()))) - ocb.getYLoc()), 2));

                        //if finger is inside a ball, create a new pointerFinger
                        if (dblCurrentDist <= ocb.getRadius()) {

                            pointerFinger myPF = new pointerFinger();

                            myPF.pointID = e.getPointerId((e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
                            myPF.ocbPoints.add(ocb);
                            myPF.pntFinger = new Point((int) e.getX(e.findPointerIndex(myPF.pointID)), (int) e.getY(e.findPointerIndex(myPF.pointID)));
                            myPF.projDist = dblCurrentDist;
                            myPF.projPaint = new Paint(ocb.getPaint());
                            myPF.projPaint.setStrokeWidth((float) Menu.scale(.0035));
                            myPF.projPaint.setStyle(Paint.Style.STROKE);
                            myPF.ocbPath = new Path();

                            lstPointerFingers.add(myPF);
                            onBall = true;
                        }
                    } else if (!blnOnTextBarrier && !blnPause) {
                        //set the text barrier to fullscreen if it is clicked on
                        if (obj instanceof ObjectPolygonBarrierText) {
                            ObjectPolygonBarrierText opbt = (ObjectPolygonBarrierText)obj;
                            if (e.getY() >= opbt.getTop()) {
                                opbt.setFullscreen();
                                pause();
                                blnOnTextBarrier = true;
                            }
                        }
                    }
                }

                //if the touch was not on a ball, show the overlay
                if (!onBall) {
                    ga.showButtonOverlay();
                    intOverlayTimer = OVERLAY_MAX;
                }

                //if we have a fullscreen text barrier, hide the overlay
                if (blnOnTextBarrier) {
                    ga.hideButtonOverlay();
                    intOverlayTimer = 0;
                }
            }

            //On finger move
            if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {

                //at least one pointerFinger moved, so refresh them
                for (pointerFinger myPF: lstPointerFingers) {
                    myPF.pntFinger.x = (int) e.getX(e.findPointerIndex(myPF.pointID));
                    myPF.pntFinger.y = (int) e.getY(e.findPointerIndex(myPF.pointID));
                }

            }

            //On finger up
            if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP || (e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {

                for (pointerFinger myPF: lstPointerFingers) {
                    if (e.getPointerId((e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT) == myPF.pointID) {
                        for (ObjectCircleBall ocbPoint : myPF.ocbPoints) {
                            if (myPF.projDist > ocbPoint.getRadius()) {

                                //for each action up outside a ball, launch said ball
                                ocbPoint.launch(new Point(myPF.pntFinger.x, myPF.pntFinger.y));
                            }
                            if (ocbPoint instanceof ObjectCircleBallSplit && ocbPoint.getRadius() > myPF.projDist) {

                                //split if action up is inside a split ball

                                //remove this if to allow the ball the split as many times as there are fingers in one tick
                                if (ocbPoint.getEnabled()) {

                                    //split the balls
                                    if (((ObjectCircleBallSplit)ocbPoint).event(lstAdd)) {


                                        //if split, remove the balls from the finger and add the three new ones
                                        pointerFinger newPF = lstPointerFingers.get(lstPointerFingers.size() - 1);
                                        if (!newPF.equals(myPF)) {
                                            newPF.ocbPoints.remove(ocbPoint);
                                        }

                                        for (pointerFinger otherPF : ocbPoint.getPointerFingers(lstPointerFingers)) {
                                            if (!otherPF.equals(myPF)) {
                                                otherPF.ocbPoints.add((ObjectCircleBallSplit)lstAdd.get(lstAdd.size() - 1));
                                                otherPF.ocbPoints.add((ObjectCircleBallSplit)lstAdd.get(lstAdd.size() - 2));
                                                otherPF.ocbPoints.add((ObjectCircleBallSplit)lstAdd.get(lstAdd.size() - 3));
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //pointerFinger went up, it no longer exists
                        lstPFRemove.add(myPF);
                    }
                }

                //remove pointerFingers, then clear the list
                for (pointerFinger myPF : lstPFRemove) {
                    lstPointerFingers.remove(lstPointerFingers.indexOf(myPF));
                }
                lstPFRemove.clear();

                //when the finger zooming into the text barrier goes up, reset
                if (blnOnTextBarrier) {
                    for (Object obj: lstObjects) {
                        if (obj instanceof ObjectPolygonBarrierText) {
                            ObjectPolygonBarrierText opbt = (ObjectPolygonBarrierText)obj;
                            opbt.setNormal();
                            unpause();
                            blnOnTextBarrier = false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public void restartLevel(int levelNumber) {
        //clear the current level and create a new level

        //clear the remove and add lists
        lstRemoval.clear();
        lstAdd.clear();
        lstPointerFingers.clear();
        lstNextFloorAdd.clear();

        //populate the level lists
        lstFloors = Levels.getLevel(levelNumber);
        lstObjects = lstFloors.get(0);

        //reset the counter and flags
        blnLevelCompleted = false;
        blnTimerStarted = false;
        blnIsMoving = false;
        lngTimer = 0;
        lngStartTime = 0;
        floorCounter = 0;

        //set intLevel
        intLevel = levelNumber;

        //unpause
        if (blnPause) {
            unpause();
        }
    }

    public void backgroundMovement() {

        //get the objects from the last floor
        lstObjects = lstFloors.get(lstFloors.size() - 1);

        for (Object obj: lstObjects) {
            //remove the pockets
            if (obj instanceof ObjectCirclePocket) {
                lstObjects.remove(obj);
            }
            //randomize the balls
            else if (obj instanceof ObjectCircleBall) {
                ((ObjectCircleBall)obj).randomizeMovement();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //start the loop
        try {
            threadGame.run();


        } catch (NullPointerException ex) {
            //when opened while running, attempts to run null; return to Menu
            ga.onMenuClick();
        }
    }

    //required by interface
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            handlerGame.removeCallbacks(threadGame);
            clearView();
        } catch (NullPointerException ex) {
            //when opened while running, also destroys surface after leaving Menu; do not remove callbacks
            Log.d("GameGUI", "Reopened while open; returning to Menu");
        }

    }

    public void clearView() {
        //sets all class objects to null so GC can collect them
        //otherwise, OOM error after too many GameGUI launches
        threadGame = null;
        handlerGame = null;
        lstFloors = null;
        lstObjects = null;
        lstRemoval = null;
        lstNextFloorAdd = null;
        lstAdd = null;
        lstPointerFingers = null;
        lstPFRemove = null;
        lstCollisions = null;
        strTime = null;
        scorePaint = null;
        backgroundPaint = null;
        collisionPaint = null;
    }

    //return the formatted time
    public String getTime() {
        return strTime;
    }

    //return the pause flag
    public boolean getPause() {
        return blnPause;
    }

    //set the pause booleans, show the button display
    public void pause() {
        blnDrawPause = true;
        ga.showButtonOverlay();
        intOverlayTimer = OVERLAY_MAX;
        blnTimerStarted = false;
    }

    //pause the game for a set amount of time; show the button display
    public void pause(long millis) {
        ga.showButtonOverlay();
        intOverlayTimer = OVERLAY_MAX;
        blnTimerStarted = false;
        synchronized (threadGame) {
            try {
                threadGame.wait(millis);
            } catch (InterruptedException ex) {
                Log.d("Interrupted Exception", ex.getMessage());
                //reset the interrupted status
                Thread.currentThread().interrupt();
            }
        }
    }

    //unpause the game
    public void unpause() {
        blnPause = false;
        blnDrawPause = false;
        blnPauseTime = false;
        ga.setPauseText("Pause");
        synchronized (threadGame) {
            threadGame.notify();
        }
    }

    //main game thread; draws to the canvas as fast as it can
    private class GameThread implements Runnable {

        Canvas canvas;
        final SurfaceHolder surface;
        GameGUI gameView;

        public GameThread(SurfaceHolder sh, GameGUI gg) {
            surface = sh;
            gameView = gg;
        }

        //looped method
        @SuppressLint("WrongCall")
        public void run() {

            if (!gameView.getPause()) {

                //tick the overlay
                if (intOverlayTimer >= 0) {
                    if (intOverlayTimer == 0) {
                        ga.hideButtonOverlay();
                    } else {
                        intOverlayTimer -= 1;
                    }
                }

                //timer for scoring
                if (blnIsMoving && blnTimerStarted && activePocketCounter > 0) {
                    //if moving and timer started, increment
                    lngTimer += System.currentTimeMillis() - lngStartTime;
                    lngStartTime = System.currentTimeMillis();
                } else if (blnIsMoving && activePocketCounter > 0) {
                    //if moving and not started, start
                    blnTimerStarted = true;
                    lngStartTime = System.currentTimeMillis();
                } else if (blnTimerStarted) {
                    //if not moving and started, start next time it is moving
                    blnTimerStarted = false;
                }

                //set the canvas to null to ensure we get a fresh canvas
                canvas = null;

                try {
                    //get the canvas from the SurfaceHolder
                    canvas = surface.lockCanvas(new Rect(0, 0, gameView.getWidth(), gameView.getHeight()));

                    synchronized (surface) {

                        //Computes and draws the game
                        gameView.onDraw(canvas);

                    }
                } finally {
                    if (canvas != null) {
                        //push the canvas to the surface
                        surface.unlockCanvasAndPost(canvas);
                    }
                }
            }

            //Loop
            handlerGame.post(threadGame);

            //if we are pausing for an amount of time, pause after a draw
            if (blnPauseTime) {
                blnPauseTime = false;
                pause(pauseLength);
                pauseLength = 0;
            }
        }
    }

    //holds each finger that went down on a ball
    public class pointerFinger {
        //Properties for fingers on the screen

        //finger ID - associated with ActionEvent ID
        protected int pointID;
        //all balls that this finger controls
        protected List<ObjectCircleBall> ocbPoints;
        //the draw path for this finger
        protected Path ocbPath;
        //the location of the finger
        protected Point pntFinger;
        //the distance from the finger to a ball
        protected double projDist;
        //the color of the projection
        protected Paint projPaint;

        public pointerFinger() {
            ocbPoints = new ArrayList<>();
            projPaint = new Paint();
            ocbPath = new Path();
        }
    }

    public static List<pointerFinger> getPF() {
        return lstPointerFingers;
    }
}

