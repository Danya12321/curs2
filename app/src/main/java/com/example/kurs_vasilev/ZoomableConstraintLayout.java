package com.example.kurs_vasilev;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;


public class ZoomableConstraintLayout extends ConstraintLayout {
    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    public static float scaleFactors = 1.0f;
    public static float lastTouchX;
    public static float lastTouchY;
    public static final int INVALID_POINTER_ID = -1;
    public static int activePointerId = INVALID_POINTER_ID;


    public ZoomableConstraintLayout(Context context) {
        super(context);
        init(context);
    }
    public ZoomableConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public ZoomableConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);

        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = event.getActionIndex();
                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);

                lastTouchX = x;
                lastTouchY = y;
                activePointerId = event.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = event.findPointerIndex(activePointerId);
                final float x = event.getX(pointerIndex);
                final float y = event.getY(pointerIndex);

                if (!scaleDetector.isInProgress()) {
                    final float dx = x - lastTouchX;
                    final float dy = y - lastTouchY;

                    setTranslationX(getTranslationX() + dx);
                    setTranslationY(getTranslationY() + dy);

                    invalidate();
                }

                lastTouchX = x;
                lastTouchY = y;
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                activePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = event.getActionIndex();
                final int pointerId = event.getPointerId(pointerIndex);

                if (pointerId == activePointerId) {
                    final int newPointerIndex = (pointerIndex == 0) ? 1 : 0;
                    lastTouchX = event.getX(newPointerIndex);
                    lastTouchY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactors *= detector.getScaleFactor();
            scaleFactors = Math.max(0.5f, Math.min(scaleFactors, 1.5f));

            setScaleX(scaleFactors);
            setScaleY(scaleFactors);

            return true;
        }
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            scaleFactors *= 1.5;
            scaleFactors = Math.min(scaleFactors, 1.5f);

            setScaleX(scaleFactors);
            setScaleY(scaleFactors);

            return true;
        }
    }

}
