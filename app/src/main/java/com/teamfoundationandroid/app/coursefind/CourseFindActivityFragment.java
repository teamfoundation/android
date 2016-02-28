package com.teamfoundationandroid.app.coursefind;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.teamfoundationandroid.app.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class CourseFindActivityFragment extends Fragment {

    public CourseFindActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }
}
