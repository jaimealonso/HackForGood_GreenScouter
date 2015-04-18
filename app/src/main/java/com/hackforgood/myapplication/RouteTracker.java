package com.hackforgood.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.widget.TextView;
import android.os.SystemClock;
import android.widget.Button;
import android.view.View;




public class RouteTracker extends ActionBarActivity {

    private TextView timerValue;

    private Button pauseButton;


    private long startTime = 0L;



    private Handler customHandler = new Handler();



    long timeInMilliseconds = 0L;

    long timeSwapBuff = 0L;

    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_tracker);

        timerValue = (TextView) findViewById(R.id.timertextView);

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        pauseButton = (Button) findViewById(R.id.endbutton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);

                Intent summary_intent = new Intent("com.hackforgood.myapplication.Summary");
                summary_intent.putExtra("TIME", timerValue.getText());
                startActivity(summary_intent);

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_route_tracker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Runnable updateTimerThread = new Runnable() {


        public void run() {



            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;



            updatedTime = timeSwapBuff + timeInMilliseconds;



            int secs = (int) (updatedTime / 1000);

            int mins = secs / 60;

            secs = secs % 60;

            int milliseconds = (int) (updatedTime % 1000);

            timerValue.setText("" + mins + ":"

                    + String.format("%02d", secs) + ":"

                    + String.format("%03d", milliseconds));

            customHandler.postDelayed(this, 0);

        }



    };
}
