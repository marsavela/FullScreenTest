package la.marsave.fullscreentest;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by sergiu on 08/02/14.
 */
public class GestureListener implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final String GESTURE_TAG = "Gestures";


    public GestureListener() {
        super();
    }

    @Override
    public boolean onDown(MotionEvent event) {
        //Log.d(GESTURE_TAG, "onDown: " + event.toString());
        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        //Log.d(GESTURE_TAG, "onFling: " + event1.toString()+event2.toString());
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        //Log.d(GESTURE_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        //Log.d(GESTURE_TAG, "onScroll: " + e1.toString()+e2.toString());
        return false;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //Log.d(GESTURE_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        //Log.d(GESTURE_TAG, "onSingleTapUp: " + event.toString());
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        Log.d(GESTURE_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d(GESTURE_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        //Log.d(GESTURE_TAG, "onSingleTapConfirmed: " + event.toString());
        return false;
    }
}
