package com.app.andrew.moviesviewer;

import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;

public class MainActivity extends AppCompatActivity {
    public static int IMAGE_WIDTH;
    public static int IMAGE_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        //final ActionBar bar = getSupportActionBar();
       // bar.setDisplayHomeAsUpEnabled(true);
        //bar.setDisplayUseLogoEnabled(true);

        setImageDimentions();
        if(savedInstanceState == null)
            getFragmentManager().beginTransaction().replace(R.id.activity_main, new MainFragment()).commit();
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
