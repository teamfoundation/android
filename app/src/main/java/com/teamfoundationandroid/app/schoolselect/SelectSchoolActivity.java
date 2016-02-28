package com.teamfoundationandroid.app.schoolselect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.teamfoundationandroid.app.PrefKeys;
import com.teamfoundationandroid.app.coursefind.CourseFindActivity;
import com.teamfoundationandroid.app.R;
import cz.msebera.android.httpclient.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SelectSchoolActivity extends AppCompatActivity {
    SchoolAutoCompleteView inputSchool;

    School[] schools;

    // adapter for auto-complete
    ArrayAdapter<School> myAdapter;

    public interface SchoolCallback {
        void processSchools();
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
                    schools = new School[schoolLinks.size()];
                    for (int i = 0; i < schoolLinks.size(); i++) {
                        Element link = schoolLinks.get(i);
                        String baseURl = link.attr("href");
                        String name = link.ownText();
                        schools[i] = new School(name,baseURl);

                    }
                    //add the schools
                    schoolCallback.processSchools();
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
                final School school = schools[position];
                final ProgressDialog dialog = ProgressDialog.show(SelectSchoolActivity.this, "Loading", "Hacking your school's bookstore...", true);
                AsyncHttpClient client = new AsyncHttpClient();
                client.addHeader("User-Agent","TeamFoundation");
                client.addHeader("Content-Length","0");
                client.get(school.baseURL, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Document doc = Jsoup.parse(responseString);
                        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(SelectSchoolActivity.this);
                        String storeId = doc.select("input#storeId").get(0).attr("value");
                        String langID = doc.select("input#langId").get(0).attr("value");
                        String catId = doc.select("input#catalogId").get(0).attr("value");

                        app_preferences.edit()
                                .putString(PrefKeys.SCHOOL_BASE_URL,school.baseURL)
                                .putString(PrefKeys.SCHOOL_FRIENDLY_NAME,school.friendlyName)
                                .putString(PrefKeys.SCHOOL_STORE_ID,doc.select("input#storeId").get(0).attr("value"))
                                .putString(PrefKeys.SCHOOL_LANG_ID,doc.select("input#langId").get(0).attr("value"))
                                .putString(PrefKeys.SCHOOL_CAT_ID,doc.select("input#catalogId").get(0).attr("value")).apply();
                        Intent tent = new Intent(SelectSchoolActivity.this, CourseFindActivity.class);
                        dialog.dismiss();
                        startActivity(tent);
                        finish();
                    }
                });


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
                    public void processSchools() {
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
