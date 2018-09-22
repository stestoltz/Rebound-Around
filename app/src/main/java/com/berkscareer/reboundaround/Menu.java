//Program:  Rebound Around
//Authors:  Weston L and Stephen S
//Date:     September 2015 - May 2016
//Purpose:  To increase an interest in STEM knowledge through real physics gameplay
//          as well as challenging a user's problem solving abilities.

package com.berkscareer.reboundaround;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import com.facebook.FacebookSdk;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    //used in scaling
    public static double scaleAvg;
    public static Point size;

    //button sizes and text sizes
    private float headingTextSize;
    private float buttonTextSize;
    private Point pntPadding;
    private Point pntButtonSize;

    //used for Google Play
    public static GoogleApiClient googleAPI;
    private SignInButton btnSign;
    private Button btnAchievements;

    private final int SIGN_IN_CODE = 327;
    private final int ACHIEVEMENTS_CODE = 517;

    private boolean blnConnecting = false;
    private boolean blnSignInClicked = false;
    private boolean blnAutoSignIn = true;
    private boolean blnExplicitSignOut = false;

    private boolean blnSignTextChange = false;

    //Change with number of booleans
    //GOOGLE PLAY BOOLEAN IS ALWAYS LAST
    private final int NUMBER_OPTIONS = 6;

    //array of options to pass to GameActivity
    private boolean[] options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Sets default to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Initializes Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        //Sets view to fullscreen
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //Gets the size of the screen, and sets it to the size variable
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);

        //set notifications bar to translucent - only works on KitKat or higher
        if (android.os.Build.VERSION.SDK_INT >= 19){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //List of options to pass to GameActivity
        options = new boolean[NUMBER_OPTIONS];
        //preinitialize the array in case the file is empty or does not contain all of the booleans
        options[0] = true; //Hints
        options[1] = true; //Projections
        options[2] = true; //Animations
        options[3] = false; //Collisions pauses
        options[4] = true; //Movement direction
        options[5] = false; //Google Play

        //Make sure this stays calibrated with the number of booleans
        try {

            //Read boolean values from the text file
            //If there is no file, it *should* create one
            //If there are less than 6 values in the text file, it will catch it and preload the list
            FileInputStream file;
            file = openFileInput("options.txt");
            DataInputStream data = new DataInputStream(file);
            options[0] = getBoolean(data, true); //Hints
            options[1] = getBoolean(data, true); //Projections
            options[2] = getBoolean(data, true); //Animations
            options[3] = getBoolean(data, false); //Collisions pauses
            options[4] = getBoolean(data, true); //Movement direction
            options[5] = getBoolean(data, false); //Google Play
            data.close();
            file.close();
        } catch (IOException IOEx) {
            Log.d("Files", "File reading failed");
        }

        //get the general screen size, then set the text sizes (DIP)
        Configuration config = getResources().getConfiguration();
        if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            headingTextSize = (float)90.0;
            buttonTextSize = (float)50.0;

            blnSignTextChange = true;
        } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            headingTextSize = (float)70.0;
            buttonTextSize = (float)40.0;

            blnSignTextChange = true;
        } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            headingTextSize = (float)50.0;
            buttonTextSize = (float)30.0;
        } else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            headingTextSize = (float)30.0;
            buttonTextSize = (float)20.0;
        } else {
            headingTextSize = (float)70.0;
            buttonTextSize = (float)40.0;
        }

        //Initialize button sizes and padding - scaled
        pntPadding = scale(.03, .02);
        pntButtonSize = scale(.25, .1375);

        //Initialize scale average - diagonal distance of the screen, used for circle scaling
        //noinspection SuspiciousNameCombination
        scaleAvg = Math.sqrt((Math.pow(size.x, 2) + Math.pow(size.y, 2)));

        setContentView(R.layout.activity_title_menu);

        //Initialize the GoogleApiClient, which handles all connections to Google Play
        googleAPI = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        //Create the menu
        initializeMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if user has already signed in, automatically sign them in on start
        if (!blnExplicitSignOut && options[options.length - 1] && googleAPI != null) {
            googleAPI.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //disconnect the Google API on close, if it is connected
        if (googleAPI != null && googleAPI.isConnected()) {
            googleAPI.disconnect();
        }
    }


    public void initializeMenu() {
        //get the objects from XML
        Button btnPlay = (Button) findViewById(R.id.btnPlay);
        Button btnLevels = (Button) findViewById(R.id.btnLevel);
        Button btnOptions = (Button) findViewById(R.id.btnOptions);
        btnSign = (SignInButton) findViewById(R.id.btnSignIn);
        btnAchievements = (Button) findViewById(R.id.btnAchievements);

        TextView txtHeading = (TextView) findViewById(R.id.txtTitle);

        //build the heading
        txtHeading.setX(pntPadding.x);
        txtHeading.setY(pntPadding.y);
        String text = "Rebound Around";
        txtHeading.setText(text);
        txtHeading.setTextSize(TypedValue.COMPLEX_UNIT_DIP, headingTextSize);

        //build the buttons
        Point pntPlay = scale(0, .4);
        getButton("Play", pntPlay, btnPlay);
        Point pntLevels = scale(0, .575);
        getButton("Levels", pntLevels, btnLevels);
        Point pntOptions = scale(0, .75);
        getButton("Options", pntOptions, btnOptions);

        //prepare the Google Play buttons
        Point pntSign = scale(.6, .4);
        getButton(pntSign, btnSign);
        btnSign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSignInClick(v);
            }
        });
        try {
            setButtons(googleAPI.isConnected());
        } catch (NullPointerException ex) {
            setButtons(options[options.length - 1]);
        }

        if (blnSignTextChange) {
            setGoogleButtonSize(buttonTextSize);
        }

        //decrease the button size for the Achievements button
        buttonTextSize *= (2.0/3.0);

        Point pntAchievements = scale(.6, .575);
        getButton("Achievements", pntAchievements.x, pntAchievements.y, btnAchievements);

        ViewGroup.LayoutParams params = btnAchievements.getLayoutParams();
        params.width = (int)((double)pntButtonSize.x * (4.0/3.0));
        btnAchievements.setLayoutParams(params);

        if (!options[options.length - 1]) {
            btnAchievements.setEnabled(false);
        }

        //fix the button size
        buttonTextSize *= (3.0/2.0);

    }

    public void onPlayClick(View v) {

        int intLevel;

        //if the user stopped at another level, get it from the text file
        try {
            FileInputStream file = openFileInput("level.txt");
            DataInputStream data = new DataInputStream(file);
            intLevel = data.readInt();
            data.close();
            file.close();
        } catch  (IOException ex) {
            //Sets it to -3 if the end of the file is hit before it reads (EOFException) to default to tutorial
            intLevel = -3;
        }

        //select the level, whether read from the file or defaulted to tutorial
        onLevelSelect(intLevel);

    }

    public void onLevelClick(View v) {

        //show the level menu
        setContentView(R.layout.activity_levels_menu);

        //create the heading
        TextView txtHeading = (TextView) findViewById(R.id.txtTitleLevel);
        txtHeading.setY(pntPadding.y);
        txtHeading.setText("Levels");
        txtHeading.setTextSize(TypedValue.COMPLEX_UNIT_DIP, headingTextSize);

        //create the back button
        Button btnBack = (Button) findViewById(R.id.btnBackLevel);
        Point pntBackLoc = scale(0, .75);
        getButton("Back", pntBackLoc, btnBack);

        //holds the levels - used to create the ListView
        List<String> lstLevels = new ArrayList<>();

        //add the levels
        lstLevels.add("Tutorial 1");
        lstLevels.add("Tutorial 2");
        lstLevels.add("Tutorial 3");
        lstLevels.add("Tutorial 4");
        for (int counter = 1; counter <= Levels.getNumLevels(); counter += 1) {
            lstLevels.add("Level " + counter);
        }

        //build the ListView; associate it with lstLevels
        ListView lstLevelsView = (ListView) findViewById(R.id.lstLevels);
        lstLevelsView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lstLevels));
        lstLevelsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //when a level is selected, call onLevelSelect to initialize it

                //in Levels, first tutorial is -3 and first level is 1; in the list, first tutorial is 0 and first level is 3; decrement by 3 to match
                id -= 3;

                onLevelSelect((int) id);

            }
        });

        lstLevelsView.setX(pntPadding.x);
        lstLevelsView.setY(scale(0, .225).y);
        ViewGroup.LayoutParams params = lstLevelsView.getLayoutParams();
        params.height = scale(0, .5).y;
        lstLevelsView.setLayoutParams(params);

    }

    public void onLevelSelect(int position) {
        //create a new Intent for GameActivity, passing it the selected level and the options list
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("intLevel", position);
        intent.putExtra("lstOptions", options);

        //start GameActivity
        startActivity(intent);
    }

    public void onOptionClick(View v) {

        setContentView(R.layout.activity_options_menu);

        //create the heading
        TextView txtHeading = (TextView) findViewById(R.id.txtTitleOptions);
        txtHeading.setY(pntPadding.y);
        txtHeading.setText("Options");
        txtHeading.setTextSize(TypedValue.COMPLEX_UNIT_DIP, headingTextSize);

        //create the checkboxes
        CheckBox chkHints = (CheckBox) findViewById(R.id.chkHints);
        chkHints = getCheckBox(chkHints, options[0], pntPadding.x, Menu.scale(0, .25).y, "Hints");
        chkHints.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onHintsClick(v);
            }
        });

        CheckBox chkProjections = (CheckBox) findViewById(R.id.chkProjections);
        chkProjections = getCheckBox(chkProjections, options[1], pntPadding.x, Menu.scale(0, .35).y, "Projections");
        chkProjections.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onProjectionsClick(v);
            }
        });

        CheckBox chkAnimations = (CheckBox) findViewById(R.id.chkAnimations);
        chkHints = getCheckBox(chkAnimations, options[2], pntPadding.x, Menu.scale(0, .45).y, "Animations");
        chkHints.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onAnimationsClick(v);
            }
        });

        CheckBox chkCollisionsPause = (CheckBox) findViewById(R.id.chkCollisionPause);
        chkCollisionsPause = getCheckBox(chkCollisionsPause, options[3], pntPadding.x, Menu.scale(0, .55).y, "Collisions pause");
        chkCollisionsPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {onCollisionsPauseClick(v);
            }
        });

        CheckBox chkMovementDirection = (CheckBox) findViewById(R.id.chkMovementDirection);
        chkMovementDirection = getCheckBox(chkMovementDirection, options[4], pntPadding.x, Menu.scale(0, .65).y, "Movement direction");
        chkMovementDirection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {onMovementDirectionClick(v);
            }
        });

        //create the back button
        Button btnBack = (Button) findViewById(R.id.btnBackOptions);
        Point pntBackLoc = scale(0, .75);
        getButton("Back", pntBackLoc, btnBack);

    }

    public void onSignInClick(View v) {

        //attempt to connect to Google Play

        if (blnSignInClicked) {
            //already connected, trying to sign out
            onSignOutClick(v);
        } else {
            //not connected, trying to sign in
            blnSignInClicked = true;
            if (googleAPI != null) {
                googleAPI.connect();
            }
        }
    }

    public void onSignOutClick(View v) {

        blnSignInClicked = false;
        blnExplicitSignOut = true;
        if (googleAPI != null && googleAPI.isConnected()) {
            //disconnect from Google Play
            Games.signOut(googleAPI);
            googleAPI.disconnect();
            setButtons(false);
        }

        //set the Google Play boolean to false
        options[options.length - 1] = false;

        btnAchievements.setEnabled(false);

        //write the options to the file
        writeOptions();
    }

    public void onAchievementClick(View v) {
        if (googleAPI != null && options[options.length - 1]) {
            if (!googleAPI.isConnected()) {
                googleAPI.connect();
            }

            if (googleAPI.isConnected()) {
                startActivityForResult(Games.Achievements.getAchievementsIntent(googleAPI), ACHIEVEMENTS_CODE);
            }
        }
    }

    public void setButtons(boolean signedIn) {
        if (signedIn) {
            setGoogleButtonText("Sign Out");
        } else {
            setGoogleButtonText("Sign In");
        }
    }

    //Loops through the SignInButton (which is a layout) and finds the TextView child
    public void setGoogleButtonText(String text) {
        for (int counter = 0; counter <= btnSign.getChildCount() - 1; counter += 1) {
            if (btnSign.getChildAt(counter) instanceof TextView) {
                ((TextView)btnSign.getChildAt(counter)).setText(text);
            }
        }
    }

    //Loops through the SignInButton (which is a layout) and finds the TextView child
    public void setGoogleButtonSize(float textSize) {
        for (int counter = 0; counter <= btnSign.getChildCount() - 1; counter += 1) {
            if (btnSign.getChildAt(counter) instanceof TextView) {
                ((TextView)btnSign.getChildAt(counter)).setTextSize(textSize);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        //if not already attempting to connect and if sign in was clicked or auto sign in is enabled, attempt to sign in again
        if (!blnConnecting && (blnSignInClicked || blnAutoSignIn)) {
            blnConnecting = true;
            blnSignInClicked = false;
            blnAutoSignIn = false;

            //if connection fails again, allow this sub to try again
            if (!BaseGameUtils.resolveConnectionFailure(this, googleAPI, connectionResult, SIGN_IN_CODE, getResources().getString(R.string.signin_error))) {
                blnConnecting = false;
            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {

        //connection to Google Play has succeeded
        setButtons(true);
        blnSignInClicked = true;

        //set the Google Play boolean to true
        options[options.length - 1] = true;

        btnAchievements.setEnabled(true);

        //write the options to the file
        writeOptions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleAPI.connect();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ensures the activity result is from signing in
        if (requestCode == SIGN_IN_CODE) {
            blnSignInClicked = false;
            blnConnecting = false;
            if (resultCode == RESULT_OK) {
                //if the activity results in success, sign in
                googleAPI.connect();
            } else {
                //display an error message if there is an error
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_failure);
            }

        }
    }

    public CheckBox getCheckBox(CheckBox chk, boolean val, float x, float y, String text) {
        //used to create our all of our Checkboxes

        //set location
        chk.setX(x);
        chk.setY(y);

        //set text and state
        chk.setChecked(val);
        if (val) {
            chk.setText(text + " on");
        } else {
            chk.setText(text + " off");
        }

        //set text size
        chk.setTextSize(TypedValue.COMPLEX_UNIT_DIP, buttonTextSize);

        return chk;
    }

    public void onCheckBoxClick(View v, String baseText, int optionsNum) {
        CheckBox chk = (CheckBox) v;
        if (chk.isChecked()) {
            options[optionsNum] = true;
            chk.setText(baseText + " on");
        } else {
            options[optionsNum] = false;
            chk.setText(baseText + " off");
        }
    }

    //when chkHints is clicked, switch value
    public void onHintsClick(View v) {
        onCheckBoxClick(v, "Hints", 0);
    }

    //when chkProjections is clicked, switch value
    public void onProjectionsClick(View v) {
        onCheckBoxClick(v, "Projections", 1);
    }

    //when chkAnimations is clicked, switch value
    public void onAnimationsClick(View v) {
        onCheckBoxClick(v, "Animations", 2);
    }

    public void onCollisionsPauseClick(View v) {
        onCheckBoxClick(v, "Collisions pause", 3);
    }

    public void onMovementDirectionClick(View v) {
        onCheckBoxClick(v, "Movement direction", 4);
    }

    //when back is clicked, return to menu and recreate it
    public void onBackClick(View v) {
        setContentView(R.layout.activity_title_menu);
        initializeMenu();
    }

    //when back is clicked from options, also write any changed to the file
    public void onOptionBackClick(View v) throws IOException {
        onBackClick(v);

        writeOptions();
    }

    public void writeOptions() {
        //attempts to write the current state of the options to the file
        try {
            //creates a connection
            FileOutputStream file = openFileOutput("options.txt", Context.MODE_PRIVATE);
            DataOutputStream data = new DataOutputStream(file);

            //writes the values
            for (boolean bool : options) {
                data.writeBoolean(bool);
            }

            //closes the connection
            data.close();
            file.close();
        } catch (IOException ex) {
            Log.d(ex.getClass().getName(), "Error writing options to the text file");
            Log.d(ex.getClass().getName(), ex.getMessage());
        }
    }

    public boolean getBoolean(DataInputStream data, boolean defaultValue) {
        try {
            return data.readBoolean();
        } catch (IOException ex) {
            return defaultValue;
        }
    }

    public void getButton(Point pntButton, SignInButton newButton) {
        //used to create the Google Play SignInButton

        //set location
        newButton.setX(pntButton.x);
        newButton.setY(pntButton.y);

        //set width and height
        ViewGroup.LayoutParams params = newButton.getLayoutParams();
        params.width = pntButtonSize.x;
        params.height = pntButtonSize.y;
        newButton.setLayoutParams(params);

    }

    //alternate, overloaded getButton sub - calls the main getButton
    public void getButton(String strButtonText, Point pntButton, Button newButton) {
        getButton(strButtonText, pntPadding.x, pntButton.y, newButton);
    }

    //main getButton sub
    public void getButton(String strButtonText, int x, int y, Button newButton) {
        //used to create all of our XML based buttons in this class

        //set text and text sizes
        newButton.setText(strButtonText);
        newButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, buttonTextSize);

        //set location
        newButton.setX(x);
        newButton.setY(y);

        //set width and height
        ViewGroup.LayoutParams params = newButton.getLayoutParams();
        params.width = pntButtonSize.x;
        params.height = pntButtonSize.y;
        newButton.setLayoutParams(params);
    }

    //global sub - used for physics scaling
    //scales circle radii
    public static double scale(double percentSize) {
        return percentSize * scaleAvg;
    }

    //global sub - used for physics scaling
    //either scales a point, or scales an individual values to X or Y - just pass 0 for the other value
    public static Point scale(double percentSizeX, double percentSizeY) {
        return new Point((int)(percentSizeX * size.x), (int)(percentSizeY * size.y));
    }

    //global sub - used for physics scaling
    //used to undo circle radii scaling - used for scaling circular equations
    public static double getPercent(double pixelDist) {
        return pixelDist / scaleAvg;
    }
}
