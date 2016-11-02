package com.app.andrew.moviesviewer;

import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {
    public static int IMAGE_WIDTH;
    public static int IMAGE_HEIGHT;
    private boolean twoPanes;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("width", IMAGE_WIDTH);
        outState.putInt("height", IMAGE_HEIGHT);
        outState.putBoolean("twopanes", twoPanes);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        IMAGE_HEIGHT = savedInstanceState.getInt("height");
        IMAGE_WIDTH = savedInstanceState.getInt("width");
        twoPanes = savedInstanceState.getBoolean("twopanes");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        //final ActionBar bar = getSupportActionBar();
       // bar.setDisplayHomeAsUpEnabled(true);
        //bar.setDisplayUseLogoEnabled(true);
        if(savedInstanceState != null)
            return;
        setImageDimentions();
        getFragmentManager().beginTransaction().replace(R.id.main_view, new MainFragment()).commit();
        if(findViewById(R.id.secondary_view) == null){
            twoPanes = false;

        }else{
            twoPanes = true;
            //todo check this

        }
/*
        if(savedInstanceState == null){
            getFragmentManager().beginTransaction().replace(R.id.activity_main, new MainFragment()).commit();
            setImageDimentions();
        }
*/
    }

    public void setImageDimentions(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.x;
        int lengh = size.y;

        IMAGE_WIDTH = width / 2;
        IMAGE_HEIGHT = lengh / 2;
    }
}
