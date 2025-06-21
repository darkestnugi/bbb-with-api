package com.example.bbbwithapi.helper;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;

public class DatePickerHelper extends DatePicker {
    public DatePickerHelper(Context context) {
        super(context);
    }

    //This is the important constructor
    public DatePickerHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DatePickerHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public DatePickerHelper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            //Excluding the top of the view
            if(ev.getY() < getHeight()/3.3F)
                return false;

            ViewParent p = getParent();
            if (p != null)
                p.requestDisallowInterceptTouchEvent(true);
        }

        return false;
    }
}
