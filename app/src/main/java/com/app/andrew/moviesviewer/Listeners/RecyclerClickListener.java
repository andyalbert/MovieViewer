package com.app.andrew.moviesviewer.Listeners;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.app.andrew.moviesviewer.R;

/**
 * Created by andrew on 10/24/16.
 */

public class RecyclerClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener listener;
    GestureDetector detector;

    public interface OnItemClickListener{
        public void onClick(View view, int position);
    }

    public RecyclerClickListener(Context context, OnItemClickListener listener){
        this.listener = listener;
        detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && listener != null && detector.onTouchEvent(e)) {
            listener.onClick(childView, rv.getChildAdapterPosition(childView));
            return true;
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
