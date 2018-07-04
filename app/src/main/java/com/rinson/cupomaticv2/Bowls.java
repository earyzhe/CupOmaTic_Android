package com.rinson.cupomaticv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Bowls extends AppCompatActivity {

//    ParentTimer parentTimer = new ParentTimer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bowls);
    }

    public void startButton(View view) {
        openMainActivity();
        MainActivity.updatdateMainTimerDisplay("Ready");
        ParentTimer.startStartTimer();
        ParentTimer.switchParentTimerActiviationState();
        MainActivity.showStopButton();
        Log.i("Info", "Start Button pressed");
    }

    public void openMainActivity() {

        Intent intent = new Intent(Bowls.this, MainActivity.class);

        startActivity(intent);
    }
}

