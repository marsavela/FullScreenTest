package la.marsave.fullscreentest;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sergiu on 14/02/14.
 */
public class HelpFragment extends Fragment {

    View.OnClickListener clickListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.help_layout, container, false);
        view.setOnClickListener(clickListener);
        return view;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}