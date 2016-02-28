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
    private final String MY_DEBUG_TAG = "CustomerAdapter";
    private ArrayList<School> items;
    private ArrayList<School> suggestions;
    private int viewResourceId;

    public SchoolAdapter(Context context, int viewResourceId, ArrayList<School> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<School>) items.clone();
        this.suggestions = new ArrayList<School>();
        this.viewResourceId = viewResourceId;
    }

    private LayoutInflater layoutInflater;
    List<School> schools;

    private LayoutInflater.Filter mFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((School)resultValue).getName();
        }

        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<School> suggustedSchools = new ArrayList<School>();
                for (School school : schools) {
                    // Note: change the "contains" to "startsWith" if you only want starting matches
                    if (school.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(school);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<School>) results.values);
            } else {
                // no filter, add entire original list back in
                addAll(schools);
            }
            notifyDataSetChanged();
        }
    };

    public SchoolAdapter(Context context, int textViewResourceId, List<School> schools) {
        super(context, textViewResourceId, schools);
        // copy all the customers into a master list
        mSchool = new ArrayList<School>(schools.size());
        mSchools.addAll(schools);
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.customerNameLabel, null);
        }

        School school = getItem(position);

        TextView name = (TextView) view.findViewById(R.id.customerNameLabel);
        name.setText(customer.getName());

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
