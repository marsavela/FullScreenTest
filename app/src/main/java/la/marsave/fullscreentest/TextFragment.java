/*
 * Copyright (C) 2013 The Android Open Source Project
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

/**
 * Created by sergiu on 10/02/14.
 */

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextFragment extends Fragment {

    OnTextFragmentAnimationEndListener mListener;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.text_fragment, container, false);
        TextView webLink = (TextView) view.findViewById(R.id.credits);
        webLink.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        int id = enter ? R.animator.slide_fragment_in : R.animator.slide_fragment_out;
        final Animator anim = AnimatorInflater.loadAnimator(getActivity(), id);
        if (enter) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mListener.onAnimationEnd();
                }
            });
        }
        return anim;
    }

    public void setOnTextFragmentAnimationEnd(OnTextFragmentAnimationEndListener listener) {
        mListener = listener;
    }

}