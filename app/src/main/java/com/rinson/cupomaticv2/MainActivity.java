package com.rinson.cupomaticv2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

//Keep this media player
public class MainActivity extends AppCompatActivity {

    int bowlSetting;
    int intervalTotalTimeInSeconds;
    int breaktime;
    int sampleTimeSeconds;
    int roundoneTimeSeconds;
    int roundTwoTimeSeconds;
    int roundThreeTimeSeconds;
    static boolean advancedMode;
    boolean vibrate;
    boolean voicePrompts;
    ActionBar actionBar;
    MediaPlayer mediaPlayer;
    Vibrator vibrator;


    SharedPreferences sharedPreferences;


    public void setUpMemory() {
        sharedPreferences = this.getSharedPreferences("com.rinson.cupomaticv2", Context.MODE_PRIVATE);

        if (sharedPreferences.contains("bowlSetting")) {
            bowlSetting = sharedPreferences.getInt("bowlSetting", 0);
        } else {
            sharedPreferences.edit().putInt("bowlSetting", 20).apply();
        }

        if (sharedPreferences.contains("advancedMode")) {
            advancedMode = sharedPreferences.getBoolean("advancedMode", false);
        } else {
            sharedPreferences.edit().putBoolean("advancedMode", false).apply();
        }

        if (sharedPreferences.contains("intervalTimeInSeconds")) {
            intervalTotalTimeInSeconds = sharedPreferences.getInt("intervalTimeInSeconds", 20);
        } else {
            sharedPreferences.edit().putInt("intervalTimeInSeconds", 20).apply();
        }

        if (sharedPreferences.contains("breakTime")) {
            breaktime = sharedPreferences.getInt("breakTime", 20);
        } else {
            sharedPreferences.edit().putInt("breakTime", 240).apply();
        }
        if (sharedPreferences.contains("sampleTime")) {
            sampleTimeSeconds = sharedPreferences.getInt("sampleTime", 20);
        } else {
            sharedPreferences.edit().putInt("sampleTime", 600).apply();
        }
        if (sharedPreferences.contains("roundOneTime")) {
            roundoneTimeSeconds = sharedPreferences.getInt("roundOneTime", 20);
        } else {
            sharedPreferences.edit().putInt("roundOneTime", 840).apply();
        }
        if (sharedPreferences.contains("roundTwoTime")) {
            roundTwoTimeSeconds = sharedPreferences.getInt("roundTwoTime", 20);
        } else {
            sharedPreferences.edit().putInt("roundTwoTime", 1080).apply();
        }
        if (sharedPreferences.contains("roundThreeTime")) {
            roundThreeTimeSeconds = sharedPreferences.getInt("roundThreeTime", 20);
        } else {
            sharedPreferences.edit().putInt("roundThreeTime", 1320).apply();
        }
        if (sharedPreferences.contains("voicePrompts")) {
            voicePrompts = sharedPreferences.getBoolean("voicePrompts", true);
        } else {
            sharedPreferences.edit().putBoolean("voicePrompts", true).apply();
        }
    }

    static ArcProgress intervalProgress;
    static ArcProgress pourProgress;
    static ArcProgress breakProgress;
    static ArcProgress sampleProgress;

    static ParentTimer parentTimer;

    public static TextView mainTimerDisplayText;
    public static Button getReadyButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {
            openSettingsActivity();
            Log.i("Menu items selected", "Settings");
        } else {
            openHelpActivity();
            Log.i("Menu items selected", "Help");
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMemory();
        parentTimer = new ParentTimer(advancedMode, bowlSetting, intervalTotalTimeInSeconds, breaktime, sampleTimeSeconds, roundoneTimeSeconds, roundTwoTimeSeconds, roundThreeTimeSeconds, vibrate, this);
        mainTimerDisplayText = findViewById(R.id.mainTimerDisplay);
        getReadyButton = findViewById(R.id.getReadyButton);
        setupProgressViews();
        updateGetReadyStopButton();
        actionBar = getSupportActionBar();
        hideActionBar();
        setUpVibrate();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void setupProgressViews() {


        intervalProgress = findViewById(R.id.intervalProgress);
        intervalProgress.setMax(intervalTotalTimeInSeconds - 1);
        intervalProgress.setProgress(0);

        pourProgress = findViewById(R.id.pourProgress);
        pourProgress.setMax(bowlSetting);
        pourProgress.setProgress(0);


        if (advancedMode == true) {
            breakProgress = findViewById(R.id.breakProgress);
            breakProgress.setMax(bowlSetting);
            breakProgress.setBottomText("Br");
            breakProgress.setProgress(0);
            sampleProgress = findViewById(R.id.sampleProgress);
            sampleProgress.setMax(bowlSetting);
            sampleProgress.setBottomText("Sa");
            sampleProgress.setProgress(0);

        } else {
            breakProgress = findViewById(R.id.breakProgress);
            breakProgress.setVisibility(View.INVISIBLE);
            breakProgress.setProgress(0);
            sampleProgress = findViewById(R.id.sampleProgress);
            sampleProgress.setMax(bowlSetting);
            sampleProgress.setBottomText("Br");
            sampleProgress.setProgress(0);
        }
    }


    public void clickButton(View view) {

        if (parentTimer.getRunningstaus() == true) {

            resetMainTimerDisplay();
            invalidateAllTimers();
            MainActivity.invalidateAllTimers();
            updateGetReadyStopButton();
            setupProgressViews();
            revealActionBar();

        } else {
            openBowlsActivity();
            MainActivity.invalidateAllTimers();
            updateGetReadyStopButton();
        }
        Log.i("Info", "Button was pressed");
    }


    // Open activity section
    public void openBowlsActivity() {
        Intent intent = new Intent(MainActivity.this, Bowls.class);
        startActivity(intent);
    }

    public void openSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, Settings.class);
        startActivity(settingsIntent);
    }

    public void openHelpActivity() {
        Intent intent = new Intent(MainActivity.this, Help.class);
        startActivity(intent);
    }


    public static void updatdateMainTimerDisplay(String text) {
        mainTimerDisplayText.setText(text);
    }

    public static void showStopButton() {
        getReadyButton.setBackgroundResource(R.drawable.stop_button);
        getReadyButton.setText("Stop");
        getReadyButton.setTextColor(Color.WHITE);
    }

    private void showGetReadyButton() {
        getReadyButton.setBackgroundResource(R.drawable.get_ready_button);
        getReadyButton.setText("Get Ready");
        getReadyButton.setTextColor(Color.WHITE);
    }

    public void updateGetReadyStopButton() {
        if (parentTimer.getRunningstaus() == true) {
            showStopButton();
        } else {
            showGetReadyButton();
        }
    }

    public static void updateProgressViews() {

        pourProgress.setProgress(parentTimer.timers[0].bowlsPassed);


        if (advancedMode == true) {

            breakProgress.setProgress(parentTimer.timers[1].bowlsPassed);
            sampleProgress.setProgress(parentTimer.timers[2].bowlsPassed);

        } else {

            sampleProgress.setBottomText("Br");
            sampleProgress.setProgress(parentTimer.timers[1].bowlsPassed);

        }
    }

    public static void invalidateAllTimers() {
        parentTimer.stopParentTimer();
        parentTimer.parentTimerCancelsIntervalTimer();
        resetMainTime();
        parentTimer.cancelTimerCells();
    }

    public void resetMainTimerDisplay() {

        updatdateMainTimerDisplay("00:00");
    }

    public static void resetMainTime() {
        parentTimer.resetMainTimeToZero();

    }

    public void hideActionBar() {
        if (parentTimer.getRunningstaus() == true) {
            actionBar.hide();
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    public void revealActionBar() {
        actionBar.show();
    }

    @Override
    public void onBackPressed() {
        if (parentTimer.getRunningstaus() == true) {
            Toast.makeText(getApplicationContext(), "Back button is disabled, while the timer is running.",
                    Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
        }
    }

    public void setupintervalsMediaPLayers(String mediaPlayerId) {

        if(voicePrompts) {

            switch (mediaPlayerId) {
                case "pour":
                    break;

                case "brake":
                    mediaPlayer = MediaPlayer.create(this, R.raw.brake);
                    mediaPlayer.start();
                    this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                        }
                    });
                    break;

                case "sample":
                    mediaPlayer = MediaPlayer.create(this, R.raw.sample);
                    mediaPlayer.start();
                    this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                        }
                    });
                    break;

                case "round_one":
                    mediaPlayer = MediaPlayer.create(this, R.raw.round_one);
                    mediaPlayer.start();
                    this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                        }
                    });
                    break;

                case "round_two":
                    mediaPlayer = MediaPlayer.create(this, R.raw.round_two);
                    mediaPlayer.start();
                    this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                        }
                    });
                    break;

                case "round_three":
                    mediaPlayer = MediaPlayer.create(this, R.raw.round_three);
                    mediaPlayer.start();
                    this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                        }
                    });
                    break;
            }
        }
    }


    public void playBeep() {

        if(vibrate){

            vibrator.vibrate(1000);
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.beep);
        mediaPlayer.start();
        this.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });

    }

    public void playPour(){

        mediaPlayer = MediaPlayer.create(this, R.raw.pour);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
    }

    public void playGetReady(){
        if(voicePrompts) {
            mediaPlayer = MediaPlayer.create(this, R.raw.get_ready);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();

                }
            });
        }
    }

    public void setUpVibrate(){
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }
}
