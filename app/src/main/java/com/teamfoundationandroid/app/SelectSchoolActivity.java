package com.teamfoundationandroid.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SelectSchoolActivity extends AppCompatActivity {
    SchoolAutoCompleteView inputSchool;

    // adapter for auto-complete
    ArrayAdapter<School> myAdapter;

    public interface SchoolCallback {
        void processSchools(School[] schools);
    }

    public void pullSchools(String hint, final SchoolCallback schoolCallback) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("school_name",hint);
        client.post("http://bncollege.com/partners-search/", params,new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if (responseString != null) {

                    Document doc = Jsoup.parse(responseString);
                    Elements schoolLinks = doc.select("a");
                    School[] schools = new School[schoolLinks.size()];
                    for (int i = 0; i < schoolLinks.size(); i++) {
                        Element link = schoolLinks.get(i);
                        String baseURl = link.attr("href");
                        String name = link.ownText();
                        schools[i] = new School(name,baseURl);

                    }
                    //add the schools
                    schoolCallback.processSchools(schools);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_school_activity);

        inputSchool = (SchoolAutoCompleteView) findViewById(R.id.select_school_fragment_input_school);
        inputSchool.setFocusableInTouchMode(true);
        inputSchool.requestFocus();
        inputSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("item clicked");
            }
        });
        inputSchool.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                // update the adapater
                if (myAdapter != null) {
                    myAdapter.notifyDataSetChanged();
                }

                // get suggestions from the database
                pullSchools(userInput.toString(), new SchoolCallback() {
                    @Override
                    public void processSchools(School[] schools) {
                        if (myAdapter != null) {
                            myAdapter.notifyDataSetChanged();
                        }
                        // update the adapter
                        myAdapter = new SchoolAdapter(SelectSchoolActivity.this, R.layout.select_school_fragment_list_view_row, schools);
                        inputSchool.setAdapter(myAdapter);
                    }
                });

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
