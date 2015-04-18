package com.hackforgood.myapplication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;



public class InitialActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

    private TextView texto;
    private SeekBar seekBar;
    private TextView texto2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        texto = (TextView) findViewById(R.id.textView);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(300);

        texto2 = (TextView)findViewById(R.id.textView3);

        final ImageButton button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                texto2.setText("Corriendo");
                System.out.println("Corriendo");
            }
        });

        final ImageButton button2 = (ImageButton) findViewById(R.id.imageButton2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                texto2.setText("Bici");
                System.out.println("Bici");
            }
        });

        final ImageButton button3 = (ImageButton) findViewById(R.id.imageButton3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                texto2.setText("Andando");
                System.out.println("Andando");
            }
        });

        final Button startbutton = (Button) findViewById(R.id.button);
        startbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent route_intent = new Intent("com.hackforgood.myapplication.RouteConfirmation");
                startActivity(route_intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

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


    public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser )
    {
       texto.setText("Duración de la ruta: " + progress + " minutos." );
    }


    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }


}
