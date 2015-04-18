package com.hackforgood.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class Summary extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();

        String time = intent.getStringExtra("TIME");

        TextView timerValue = (TextView) findViewById(R.id.tiempotextdata);
        timerValue.setText(time);

        final Button backbutton = (Button) findViewById(R.id.button2volver);
        backbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent initial_intent = new Intent(Summary.this, InitialActivity.class);
                initial_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(initial_intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_summary, menu);
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
}
