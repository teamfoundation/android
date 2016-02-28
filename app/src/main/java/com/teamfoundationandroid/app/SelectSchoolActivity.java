package com.teamfoundationandroid.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import java.util.ArrayList;

public class SelectSchoolActivity extends AppCompatActivity {
    /*
      * Change to type CustomAutoCompleteView instead of AutoCompleteTextView
      * since we are extending to customize the view and disable filter
      * The same with the XML view, type will be CustomAutoCompleteView
      */
    SchoolAutoCompleteView myAutoComplete;

    // adapter for auto-complete
    ArrayAdapter<SchoolAdapter> myAdapter;

    public interface SchoolCallback {
        void processSchools(School[] schools);
    }

    public void pullSchools(String hint, final SchoolCallback schoolCallback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //todo parse the responseString
                School[] schools = new School[length];
                //add the schools
                schoolCallback.processSchools(schools);

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_school_activity);

        AutoCompleteTextView inputSchool = (SchoolAutoCompleteView) findViewById(R.id.select_school_fragment_input_school);
        inputSchool.setFocusableInTouchMode(true);
        inputSchool.requestFocus();
        inputSchool.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                // if you want to see in the logcat what the user types
                SelectSchoolActivity mainActivity = ((MainActivity) context);

                // update the adapater
                myAdapter.notifyDataSetChanged();

                // get suggestions from the database
                pullSchools(userInput.toString(), new SchoolCallback() {
                    @Override
                    public void processSchools(School[] schools) {

                        // update the adapter
                        myAdapter = new SchoolAdapter(SelectSchoolActivity.this, R.layout.list_view_row_item, schools);

                        myAutoComplete.setAdapter(myAdapter);
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ArrayList<School> schools = new ArrayList<>();
        String[] items = new String[] {"Please search..."};



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        inputSchool.setAdapter(adapter);
        inputSchool.on
    }

    // this function is used in CustomAutoCompleteTextChangedListener.java
    public String[] getItemsFromDb(String searchTerm){

        // add items on the array dynamically
        List<MyObject> products = databaseH.read(searchTerm);
        int rowCount = products.size();

        String[] item = new String[rowCount];
        int x = 0;

        for (MyObject record : products) {

            item[x] = record.objectName;
            x++;
        }

        return item;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_school_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
