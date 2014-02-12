package la.marsave.fullscreentest;

/**
 * Created by SergiuDaniel on 11/02/14.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    /**
     * The {@link int} that will host displayed colors.
     */
    private static final int[] colours = new int[] {
            Color.WHITE,    Color.GRAY,     Color.BLACK,
            Color.YELLOW,   Color.MAGENTA,  Color.CYAN,
            Color.BLUE,     Color.GREEN,    Color.RED
    };

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a ColorFragment (defined as a static inner class below).
        return ColorFragment.newInstance(colours[position]);
    }

    @Override
    public int getCount() {
        // Show 9 total pages.
        return colours.length;
    }
}