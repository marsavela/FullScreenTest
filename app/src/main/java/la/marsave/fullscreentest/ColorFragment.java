package la.marsave.fullscreentest;

/**
 * Created by SergiuDaniel on 11/02/14.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class ColorFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_COLOR_NUMBER = "color_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ColorFragment newInstance(int colorNumber) {
        ColorFragment fragment = new ColorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLOR_NUMBER, colorNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ColorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        rootView.setBackgroundColor(getArguments().getInt(ARG_COLOR_NUMBER));
        return rootView;
    }
}