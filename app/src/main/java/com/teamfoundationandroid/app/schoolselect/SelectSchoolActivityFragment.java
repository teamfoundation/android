package com.teamfoundationandroid.app.schoolselect;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.teamfoundationandroid.app.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectSchoolActivityFragment extends Fragment {

    public SelectSchoolActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.select_school_fragment, container, false);
    }
}
