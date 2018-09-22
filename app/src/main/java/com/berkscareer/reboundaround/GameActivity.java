package com.berkscareer.reboundaround;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import com.google.android.gms.games.Games;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

public class GameActivity extends Activity {

    //holds the number of the current level
    private int intLevel;

    //various layouts used throughout the game
    private FrameLayout lyotFrame;
    private RelativeLayout lyotWin;
    private RelativeLayout lyotHint;
    private RelativeLayout lyotButtons;

    //layout booleans
    private boolean blnLayoutWinVisible;
    private boolean blnButtonsVisible;

    //pause booleans
    private boolean blnPause = false;
    private Button btnPause;

    //holds the current GameGUI instance
    private GameGUI game;

    //button values
    private int buttonWidth;
    private int buttonHeight;
    private float buttonTextSize;
    private float textViewTextSize;
    private float hintTextSize;

    //options - retrieved from Menu
    private boolean[] options;

    //Used to connect to Facebook
    private ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //attempts to remove the action bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        //sets the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Facebook connection
        shareDialog = new ShareDialog(this);

        //sets the game to fullscreen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //gets values passed from Menu
        intLevel = getIntent().getExtras().getInt("intLevel");
        options = getIntent().getExtras().getBooleanArray("lstOptions");

        //initializes the layouts
        lyotFrame = new FrameLayout(this);
        lyotButtons = new RelativeLayout(this);
        lyotWin = new RelativeLayout(this);

        //creates the instance of GameGUI
        game = new GameGUI(this, this, intLevel);

        //initializes the button values
        Point pntButton = new Point(Menu.scale(.15, .1));
        buttonWidth = pntButton.x;
        buttonHeight = pntButton.y;

        //get the configuration, and set the text sizes (DIP) based on the size
        Configuration config = getResources().getConfiguration();
        if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            textViewTextSize = (float)27.5;
            buttonTextSize = (float)25.0;
            hintTextSize = (float)50.0;
        } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            textViewTextSize = (float)22.5;
            buttonTextSize = (float)20.0;
            hintTextSize = (float)40.0;
        } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            textViewTextSize = (float)17.5;
            buttonTextSize = (float)15.0;
            hintTextSize = (float)30.0;
        } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            textViewTextSize = (float)12.5;
            buttonTextSize = (float)10.0;
            hintTextSize = (float)20.0;
        } else {
            textViewTextSize = (float)22.5;
            buttonTextSize = (float)20.0;
            hintTextSize = (float)40.0;
        }

        //creates the buttons from scratch
        Button btnMenu = this.getButton("Menu", new Point(Menu.size.x - buttonWidth, 0));
        Button btnRestart = this.getButton("Restart", new Point(Menu.size.x - (2 * buttonWidth), 0));
        btnPause = this.getButton("Pause", new Point(Menu.size.x - (3 * buttonWidth), 0));

        //adds listeners to the buttons
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuClick();
            }
        });
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestartClick();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseClick(v);
            }
        });

        //adds the buttons the layout
        lyotButtons.addView(btnMenu);
        lyotButtons.addView(btnRestart);
        lyotButtons.addView(btnPause);

        //adds the GameGUI instance to the main layout
        lyotFrame.addView(game);

        //layout flags
        blnLayoutWinVisible = false;
        blnButtonsVisible = false;

        setContentView(lyotFrame);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //writes the level number to the text file
        writeLevel(intLevel);

    }

    @Override
    public void onStop() {
        super.onStop();

        //writes the level number to the text file
        writeLevel(intLevel);
    }

    public void onMenuClick() {
        //returns from

        //pause the game until the user responds
        game.pause();

        final GameActivity ga = this;

        //ask the user if they are sure if they want to return to the menu
        new AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Returning to Menu")
            .setMessage("Are you sure you would like to return to the menu?")
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //yes - the user wants to return to the menu

                    //if the level end layout is visible, remove it
                    if (blnLayoutWinVisible) {
                        lyotFrame.removeView(lyotWin);
                    }

                    //writes the level number to the text file
                    writeLevel(intLevel);

                    //return to the menu
                    startActivity(new Intent(ga, Menu.class));
                    blnLayoutWinVisible = false;
                }
            })
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //no - the user does not want to return to the menu

                    //return to the game
                    game.unpause();
                }
            })
            .show();

    }

    @Override
    public void onBackPressed() {
        //prompt return to menu
        onMenuClick();
    }

    public void onRestartClick() {
        //restarts the current level

        //if the level end layout is visible, remove it
        if (blnLayoutWinVisible) {
            lyotFrame.removeView(lyotWin);
        }

        //set the text to pause, because it unpauses on restart
        btnPause.setText("Pause");

        //restarts the level
        game.restartLevel(intLevel);
        blnLayoutWinVisible = false;
    }

    public void onPauseClick(View v) {
        Button btnPause = (Button)v;

        //if paused, unpause
        if (blnPause) {
            game.unpause();
            btnPause.setText("Pause");
            blnPause = false;
        //otherwise, pause
        } else {
            game.pause();
            btnPause.setText("Play");
            blnPause = true;
        }
    }

    //allows setting text from GameGUi
    public void setPauseText(String text) {
        btnPause.setText(text);
    }

    public void levelCompleted(boolean blnPlayServices) {
        //when the level is completed, stop all operations until the user continues to another level

        //remove the buttons from the top right corner
        hideButtonOverlay();

        //dim out GameGUI
        lyotWin.setBackgroundColor(Color.rgb(0, 0, 0));
        lyotWin.getBackground().setAlpha(150);

        //initialize the TextView
        TextView txtFinalScore = new TextView(this);

        //build black background boxes - dimensions and locations
        double viewBoxLeft = .3175;
        double viewBoxTop = .2;

        double socialMediaBoxLeft = .1975;
        double socialMediaBoxTop = .6;

        Point pntSocialMediaButtons = Menu.scale(.17, .15);

        //create the end level buttons - placement is very precise

        //returns to menu
        Button btnMenu = this.getButton("Menu", Menu.scale(viewBoxLeft + .0225, viewBoxTop + .03));
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMenuClick();
            }
        });

        //restarts current level
        Button btnRestart = this.getButton("Restart", Menu.scale(viewBoxLeft + .1975, viewBoxTop + .03));
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRestartClick();
            }
        });

        //continues to next level; if last level, return to menu
        Button btnNextLevel = this.getButton("Next Level", Menu.scale(viewBoxLeft + .0225, viewBoxTop + .19), Menu.scale(.32, 0).x, buttonHeight);
        btnNextLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intLevel += 1;
                try {
                    onRestartClick();
                } catch (IndexOutOfBoundsException ex) {
                    onMenuClick();
                    intLevel -= 1;
                }
            }
        });

        //after the user logs in, build a facebook post and post it
        Button btnFacebookShare = this.getButton("Share to Facebook", Menu.scale(socialMediaBoxLeft + .0225, socialMediaBoxTop + .025), pntSocialMediaButtons.x, pntSocialMediaButtons.y);
        btnFacebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //link to Facebook, then post and show
                String shareText;
                if (intLevel <= 0) {
                    shareText = "tutorial " + (intLevel + 4) + " in just " + game.getTime() + "!";
                } else {
                    shareText = "level " + intLevel + " in just " + game.getTime() + "!";
                }
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://goo.gl/VmYuzu"))
                        .setContentTitle("Rebound Around")
                        .setContentDescription("I just beat " + shareText)
                        .build();
                shareDialog.show(content);
            }
        });

        //display the current leaderboard - this is disabled while Google Play is not signed in
        Button btnDisplayLeaderboards = this.getButton("Display Leaderboard", Menu.scale(socialMediaBoxLeft + .2175, socialMediaBoxTop + .025), pntSocialMediaButtons.x, pntSocialMediaButtons.y);
        btnDisplayLeaderboards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open the leaderboard
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(Menu.googleAPI, Levels.getLeaderboardID(intLevel)), 0);
            }
        });

        //after the user logs in, build a Twitter tweet and tweet it
        Button btnTwitterShare = this.getButton("Share to Twitter", Menu.scale(socialMediaBoxLeft + .4125, socialMediaBoxTop + .025), pntSocialMediaButtons.x, pntSocialMediaButtons.y);
        btnTwitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText;
                if (intLevel <= 0) {
                    shareText = "tutorial " + (intLevel + 4) + " in just " + game.getTime() + "!";
                } else {
                    shareText = "level " + intLevel + " in just " + game.getTime() + "!";
                }
                try {
                    TweetComposer.Builder builder = new TweetComposer.Builder(GameActivity.this)
                            .text("I just beat " + shareText + " #ReboundAround")
                            .url(URI.create("https://goo.gl/VmYuzu").toURL());
                    builder.show();
                } catch (MalformedURLException ex) {
                    TweetComposer.Builder builder = new TweetComposer.Builder(GameActivity.this)
                            .text("I just beat " + shareText + " #ReboundAround");
                    builder.show();
                }


            }
        });

        //only prompt for leaderboards if user is signed in
        if (!blnPlayServices) {
            btnDisplayLeaderboards.setEnabled(false);
        }

        //create TextView
        txtFinalScore.setTextColor(Color.WHITE);
        String text;
        if (intLevel <= 0) {
            text = "Tutorial " + (intLevel + 4) + " final time: " + game.getTime();
            txtFinalScore.setText(text);
        } else {
            text = "Level " + intLevel + " final time: " + game.getTime();
            txtFinalScore.setText(text);
        }

        txtFinalScore.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textViewTextSize);
        Point pntScoreLoc = Menu.scale(viewBoxLeft + .0225, viewBoxTop + .135);
        txtFinalScore.setX(pntScoreLoc.x);
        txtFinalScore.setY(pntScoreLoc.y);

        //create two black boxes with rounded corners

        //top box values
        Point viewBoxTopLeft = Menu.scale(viewBoxLeft, viewBoxTop);
        Point viewBoxDims = Menu.scale(.365, .32);

        //create and place top box
        View vBox = new View(this);
        vBox.setX(viewBoxTopLeft.x);
        vBox.setY(viewBoxTopLeft.y);
        vBox.setLayoutParams(new ViewGroup.LayoutParams(viewBoxDims.x, viewBoxDims.y));

        //bottom box values
        Point socialMediaBoxTopLeft = Menu.scale(socialMediaBoxLeft, socialMediaBoxTop);
        Point socialMediaBoxDims = Menu.scale(.605, .2);

        //create and place bottom box
        View vSocialMediaBox = new View(this);
        vSocialMediaBox.setX(socialMediaBoxTopLeft.x);
        vSocialMediaBox.setY(socialMediaBoxTopLeft.y);
        vSocialMediaBox.setLayoutParams(new ViewGroup.LayoutParams(socialMediaBoxDims.x, socialMediaBoxDims.y));

        //round the corners
        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius((float)Menu.scale(.02));
        shape.setColor(Color.BLACK);
        vBox.setBackground(shape);

        shape.setCornerRadius((float)Menu.scale(.015));
        vSocialMediaBox.setBackground(shape);

        //balls move around in the background
        game.backgroundMovement();

        //add the boxes
        lyotWin.addView(vBox);
        lyotWin.addView(vSocialMediaBox);
        //add the TextView and Buttons on top of the boxes
        lyotWin.addView(txtFinalScore);
        lyotWin.addView(btnMenu);
        lyotWin.addView(btnRestart);
        lyotWin.addView(btnNextLevel);
        lyotWin.addView(btnFacebookShare);
        lyotWin.addView(btnDisplayLeaderboards);
        lyotWin.addView(btnTwitterShare);

        //add the layout to the main layout
        lyotFrame.addView(lyotWin);

        blnLayoutWinVisible = true;

    }

    //alternate getButton sub - calls main getButton
    public Button getButton(String strButtonText, Point pntButton) {
        return getButton(strButtonText, pntButton, buttonWidth, buttonHeight);
    }

    //main getButton sub - creates buttons from scratch
    public Button getButton(String strButtonText, Point pntButton, int width, int height) {

        //unlike Menu, which references buttons from XML, creates a new Button
        Button newButton = new AppCompatButton(this);

        //sets text and text size
        newButton.setText(strButtonText);
        newButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, buttonTextSize);

        //sets location
        newButton.setX(pntButton.x);
        newButton.setY(pntButton.y);

        //sets width and height
        newButton.setHeight(height);
        newButton.setWidth(width);

        return newButton;
    }

    public void writeLevel(int level) {
        //writes the level number to the text file
        try {
            FileOutputStream file = openFileOutput("level.txt", Context.MODE_PRIVATE);
            DataOutputStream data = new DataOutputStream(file);
            data.writeInt(level);
            data.close();
            file.close();
        } catch (IOException ex) {
            //writes to logcat if there is a problem
            Log.d(ex.getClass().getName(), "Error writing level number to file");
            Log.d(ex.getClass().getName(), ex.getMessage());
        }
    }

    public void showButtonOverlay() {
        //show the buttons if they are not shown - called from GameGUI
        if (!blnButtonsVisible) {
            lyotFrame.addView(lyotButtons);
            blnButtonsVisible = true;
        }
    }

    public void hideButtonOverlay() {
        //hide the buttons if they are showing - called from GameGUI
        if (blnButtonsVisible) {
            lyotFrame.removeView(lyotButtons);
            blnButtonsVisible = false;
        }
    }

    public void displayHint() {
        //if the user has it enabled, show a hint after several attempts - called from GameGUI
        if (options[0]) {
            game.pause();

            //initialize the layout
            lyotHint = new RelativeLayout(this);

            //create the OK button - closes the hint
            Button btnOK = getButton("OK", Menu.scale(.45, .6), buttonWidth, buttonHeight * 2);
            btnOK.setTextSize(TypedValue.COMPLEX_UNIT_SP, hintTextSize);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lyotFrame.removeView(lyotHint);
                    game.unpause();
                }
            });

            //create the TextView - contains the hint
            TextView txtHint = new TextView(this);
            txtHint.setTextSize(TypedValue.COMPLEX_UNIT_DIP, hintTextSize);
            Point pntOKPos = Menu.scale(.1, .1);
            txtHint.setX(pntOKPos.x);
            txtHint.setY(pntOKPos.y);
            txtHint.setTextColor(Color.BLACK);
            txtHint.setWidth(Menu.scale(.8, 0).x);

            //sets the background color - hides GameGUI until OK is pressed
            View background = new View(this);
            background.setBackgroundColor(Color.WHITE);

            //contains the hint
            String strMessage = "Hint: ";

            //get the remainder of the hint message
            strMessage += Levels.getHint(intLevel);

            //set the TextView text to the hint
            txtHint.setText(strMessage);

            //add the hints to the layout
            lyotHint.addView(background);
            lyotHint.addView(btnOK);
            lyotHint.addView(txtHint);
            lyotFrame.addView(lyotHint);
        }
    }

    //get the options - called in GameGUI
    public boolean[] getOptions() {
        return options;
    }
}
