/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package la.marsave.fullscreentest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends Activity implements
        OnTextFragmentAnimationEndListener, FragmentManager.OnBackStackChangedListener {

    boolean mDidSlideOut = false;
    boolean mIsAnimating = false;

    /**
     * The different fragments that will host the section contents.
     */
    private InfiniteViewPager mInfiniteViewPager;
    private TextFragment mTextFragment;
    private HelpFragment mHelpFragment;
    private View mDarkHoverView;

    private GestureDetector mScrollDetector;

    //TAGs
    private static final String PREFERENCES = "secret";
    private static final String HELPED = "helped";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);
        setContentView(R.layout.activity_main);

        getFragmentManager().addOnBackStackChangedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
          The {@link android.support.v4.view.PagerAdapter} that will provide
          fragments for each of the sections. We use a
          {@link SectionsPagerAdapter} derivative, which will keep every
          loaded fragment in memory. If this becomes too memory intensive, it
          may be best to switch to a
          {@link android.support.v13.app.FragmentStatePagerAdapter}.
         */
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mScrollDetector = new GestureDetector(this, new ViewPagerGestureDetector());

        // Set up the ViewPager with the sections adapter.
        mInfiniteViewPager = (InfiniteViewPager) findViewById(R.id.pager);
        mInfiniteViewPager.setAdapter(new InfinitePagerAdapter(mSectionsPagerAdapter));
        mInfiniteViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mInfiniteViewPager.setOnTouchListener(mTouchViewPagerListener);

        // Set up the DarkHover that will be seen to cover the adapter once slided in.
        mDarkHoverView = findViewById(R.id.dark_hover_view);
        mDarkHoverView.setAlpha(0);
        mDarkHoverView.setOnTouchListener(mTouchDarkHoverListener);

        mTextFragment = new TextFragment();
        mTextFragment.setOnTextFragmentAnimationEnd(this);

        //we look for the help fragment if already exists.
        mHelpFragment = (HelpFragment) getFragmentManager().findFragmentByTag(HELPED);

        //If it doesn't, we create it, if needed.
        if (mHelpFragment == null && !getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getBoolean(HELPED, false)) {
            mHelpFragment = new HelpFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.move_to_back_container, mHelpFragment, HELPED);
            transaction.commit();
        }
        if (mHelpFragment != null) {
            mDarkHoverView.setAlpha(0.7F);
            mHelpFragment.setClickListener(mClickHelper);
        }
    }

    @Override
    public void onBackPressed() {
        mDidSlideOut = false;
        super.onBackPressed();
    }

    @Override
    public void onBackStackChanged() {
        if (!mDidSlideOut) {
            slideForward();
        }
    }

    @Override
    protected void onPause() {
        //TODO Proper screen rotation handle.
        if (mDidSlideOut) {
            mDidSlideOut = false;
            getFragmentManager().popBackStack();
        }
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    View.OnTouchListener mTouchDarkHoverListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v.getAlpha() > 0) {
                switchFragments();
                return true;
            }
            return false;
        }
    };

    View.OnTouchListener mTouchViewPagerListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mScrollDetector.onTouchEvent(event);
        }
    };

    View.OnClickListener mClickHelper = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
            editor.putBoolean(HELPED, true);
            editor.commit();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.remove(mHelpFragment);
            transaction.commit();

            mDarkHoverView.setAlpha(0);
        }
    };

    private class ViewPagerGestureDetector extends SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            switchFragments();
            return super.onSingleTapConfirmed(e);
        }
    }

    /**
     * This method is used to toggle between the two fragment states by
     * calling the appropriate animations between them. The entry and exit
     * animations of the text fragment are specified in R.animator resource
     * files. The entry and exit animations of the image fragment are
     * specified in the slideBack and slideForward methods below. The reason
     * for separating the animation logic in this way is because the translucent
     * dark hover view must fade in at the same time as the image fragment
     * animates into the background, which would be difficult to time
     * properly given that the setCustomAnimations method can only modify the
     * two fragments in the transaction.
     */
    private void switchFragments() {
        if (mIsAnimating) {
            return;
        }
        mIsAnimating = true;
        if (mDidSlideOut) {
            mDidSlideOut = false;
            getFragmentManager().popBackStack();
        } else {
            mDidSlideOut = true;

            Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator arg0) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.animator.slide_fragment_in, 0, 0,
                            R.animator.slide_fragment_out);
                    transaction.add(R.id.move_to_back_container, mTextFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            };
            slideBack(listener);
        }
    }

    /**
     * This method animates the image fragment into the background by both
     * scaling and rotating the fragment's view, as well as adding a
     * translucent dark hover view to inform the user that it is inactive.
     */
    public void slideBack(Animator.AnimatorListener listener) {
        View movingFragmentView = mInfiniteViewPager;

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofFloat("rotationY", 40f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.8f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.8f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.0f, 0.5f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationY", 0);
        movingFragmentRotator.setStartDelay(getResources().
                getInteger(R.integer.half_slide_up_down_duration));

        AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, darkHoverViewAnimator, movingFragmentRotator);
        s.addListener(listener);
        s.start();
    }

    /**
     * This method animates the image fragment into the foreground by both
     * scaling and rotating the fragment's view, while also removing the
     * previously added translucent dark hover view. Upon the completion of
     * this animation, the image fragment regains focus since this method is
     * called from the onBackStackChanged method.
     */
    public void slideForward() {
        View movingFragmentView = mInfiniteViewPager;

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.5f, 0.0f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationX", 0);
        movingFragmentRotator.setStartDelay(
                getResources().getInteger(R.integer.half_slide_up_down_duration));

        AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, movingFragmentRotator, darkHoverViewAnimator);
        s.setStartDelay(getResources().getInteger(R.integer.slide_up_down_duration));
        s.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
            }
        });
        s.start();
    }

    public void onAnimationEnd() {
        mIsAnimating = false;
    }
}
