package com.teamfoundationandroid.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ravi on 2/28/16.
 */
public class SchoolAdapter extends ArrayAdapter<School> {
    final String TAG = "AutocompleteCustomArrayAdapter.java";

    Context mContext;
    int layoutResourceId;
    School data[] = null;

    public SchoolAdapter(Context mContext, int layoutResourceId, School[] data) {

        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

        @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            /*
             * The convertView argument is essentially a "ScrapView" as described is Lucas post
             * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
             * It will have a non-null value when ListView is asking you recycle the row layout.
             * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.*/

    if(convertView==null){
        // inflate the layout
        LayoutInflater inflater = ((SelectSchoolActivity) mContext).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
    }

    // object item based on the position
    School objectItem = data[position];

    // get the TextView and then set the text (item name) and tag (item ID) values
    TextView textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
    textViewItem.setText(objectItem.friendlyName);


        return convertView;

        }

}
