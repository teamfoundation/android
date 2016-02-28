package com.teamfoundationandroid.app;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by Ravi on 2/28/16.
 */
public class SchoolAutoCompleteView extends AutoCompleteTextView {

    public SchoolAutoCompleteView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public SchoolAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public SchoolAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }
}
