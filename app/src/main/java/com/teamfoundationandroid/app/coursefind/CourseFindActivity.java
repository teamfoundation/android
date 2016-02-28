package com.teamfoundationandroid.app.coursefind;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.*;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.user.IdentityManager;
import com.facebook.internal.Logger;
import com.google.repacked.apache.commons.lang3.StringEscapeUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.teamfoundationandroid.app.*;
import com.teamfoundationandroid.app.schoolselect.SchoolAdapter;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class CourseFindActivity extends AppCompatActivity implements View.OnClickListener {

    WebView view;
    ProgressDialog dialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /** Class name for log messages. */
    private final static String LOG_TAG = CourseFindActivity.class.getSimpleName();

    /** Bundle key for saving/restoring the toolbar title. */
    private final static String BUNDLE_KEY_TOOLBAR_TITLE = "title";

    /** The identity manager used to keep track of the current user account. */
    private IdentityManager identityManager;

    private Button signOutButton;

    /**
     * Initializes the sign-in and sign-out buttons.
     */
    private void setupSignInButtons() {

        signOutButton = (Button) findViewById(R.id.action_sign_out);
        signOutButton.setOnClickListener(this);

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain a reference to the mobile client. It is created in the Application class,
        // but in case a custom Application class is not used, we initialize it here if necessary.
        AWSMobileClient.initializeMobileClientIfNecessary(this);

        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

        setContentView(R.layout.main_activity);
        TextView school = (TextView) findViewById(R.id.main_activity_fragment_school_label);
        final SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(CourseFindActivity.this);
        school.setText(app_preferences.getString(PrefKeys.SCHOOL_FRIENDLY_NAME,"Unknown School"));


        dialog = ProgressDialog.show(this, "Loading", "Hacking your school's bookstore...", true);

        final Spinner termSpinner = (Spinner) findViewById(R.id.main_activity_fragment_term_spinner);
        final ArrayAdapter<Term> termAdapter = new TermListAdapter(CourseFindActivity.this, android.R.layout.simple_spinner_item, new Term[0]);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termSpinner.setAdapter(termAdapter);
        termAdapter.notifyDataSetChanged();

        final Spinner deptSpinner = (Spinner) findViewById(R.id.main_activity_fragment_department_spinner);
        final Spinner courseSpinner = (Spinner) findViewById(R.id.main_activity_fragment_course_spinner);
        final Spinner sectionSpinner = (Spinner) findViewById(R.id.main_activity_fragment_section_spinner);
        deptSpinner.setEnabled(false);
        courseSpinner.setEnabled(false);
        sectionSpinner.setEnabled(false);

        final String termId;
        String courseId;


        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // your code here
                AsyncHttpClient client = new AsyncHttpClient();
                String url = app_preferences.getString("schoolBaseURL","http://bncollege.com") + "/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd";
                client.addHeader("Content-Length","0");
                client.addHeader("User-Agent","TeamFoundation");
                RequestParams params = new RequestParams();
                params.add("dropdown","term");
                params.add("langId",app_preferences.getString(PrefKeys.SCHOOL_LANG_ID,"-1"));
                params.add("storeId",app_preferences.getString(PrefKeys.SCHOOL_STORE_ID,""));
                params.add("catalogId",app_preferences.getString(PrefKeys.SCHOOL_CAT_ID,""));
                params.add("termId",(((Term) parent.getAdapter().getItem(position)).termId));
                //campusId=63074026&

                //curl -H 'Content-Length:0' -H 'User-Agent:' -X POST 'http://milton.bncollege.com/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd?termId=72257798&courseId=&sectionId=&storeId=82238&catalogId=10001&langId=-1&dropdown=term'

                //curl -H 'Content-Length:0' -H 'User-Agent:' -X POST 'http://milton.bncollege.com/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd?termId=72257798&deptId=72257800&courseId=&sectionId=&storeId=82238&catalogId=10001&langId=-1&dropdown=term'
                client.get(CourseFindActivity.this,url,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        //todo update the department list adapter
                        try {

                            Department[] departments = new Department[response.length()];
                            for (int i = 0; i < response.length(); i++) {
                                departments[i] = new Department(response.getJSONObject(i).getString("categoryId"), response.getJSONObject(i).getString("categoryName"));
                            }

                            deptSpinner.setAdapter(new DepartmentListAdapter(CourseFindActivity.this, android.R.layout.simple_spinner_item, departments));


                            deptSpinner.setEnabled(true);
                            courseSpinner.setEnabled(false);
                            sectionSpinner.setEnabled(false);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                    }


                });


            }
        });

        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // your code here

                AsyncHttpClient client = new AsyncHttpClient();
                String url = app_preferences.getString("schoolBaseURL","http://bncollege.com") + "/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd";
                client.addHeader("Content-Length","0");
                client.addHeader("User-Agent","TeamFoundation");
                RequestParams params = new RequestParams();
                params.add("dropdown","dept");
                params.add("deptId",((Department) parent.getAdapter().getItem(position)).id);
                params.add("langId",app_preferences.getString(PrefKeys.SCHOOL_LANG_ID,"-1"));
                params.add("storeId",app_preferences.getString(PrefKeys.SCHOOL_STORE_ID,""));
                params.add("catalogId",app_preferences.getString(PrefKeys.SCHOOL_CAT_ID,""));
                params.add("termId",(((Term) termSpinner.getSelectedItem()).termId));
                //campusId=63074026&

                //curl -H 'Content-Length:0' -H 'User-Agent:' -X POST 'http://milton.bncollege.com/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd?termId=72257798&courseId=&sectionId=&storeId=82238&catalogId=10001&langId=-1&dropdown=term'

                //curl -H 'Content-Length:0' -H 'User-Agent:' -X POST 'http://milton.bncollege.com/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd?termId=72257798&deptId=72257800&courseId=&sectionId=&storeId=82238&catalogId=10001&langId=-1&dropdown=term'
                client.get(CourseFindActivity.this,url,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        //todo update the department list adapter
                        try {

                            Course[] courses = new Course[response.length()];
                            for (int i = 0; i < response.length(); i++) {
                                courses[i] = new Course(response.getJSONObject(i).getString("categoryId"), response.getJSONObject(i).getString("categoryName"));
                            }

                            courseSpinner.setAdapter(new CourseListAdapter(CourseFindActivity.this, android.R.layout.simple_spinner_item, courses));


                            deptSpinner.setEnabled(true);
                            courseSpinner.setEnabled(true);
                            sectionSpinner.setEnabled(false);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                    }


                });


            }
        });
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // your code here

                AsyncHttpClient client = new AsyncHttpClient();
                String url = app_preferences.getString("schoolBaseURL","http://bncollege.com") + "/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd";
                client.addHeader("Content-Length","0");
                client.addHeader("User-Agent","TeamFoundation");
                RequestParams params = new RequestParams();
                params.add("dropdown","course");
                params.add("deptId",((Department) deptSpinner.getSelectedItem()).id);
                params.add("langId",app_preferences.getString(PrefKeys.SCHOOL_LANG_ID,"-1"));
                params.add("storeId",app_preferences.getString(PrefKeys.SCHOOL_STORE_ID,""));
                params.add("catalogId",app_preferences.getString(PrefKeys.SCHOOL_CAT_ID,""));
                params.add("termId",((Term) termSpinner.getSelectedItem()).termId);
                params.add("courseId",((Course) parent.getAdapter().getItem(position)).id);
                //campusId=63074026&

                //curl -H 'Content-Length:0' -H 'User-Agent:' -X POST 'http://milton.bncollege.com/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd?termId=72257798&courseId=&sectionId=&storeId=82238&catalogId=10001&langId=-1&dropdown=term'

                //curl -H 'Content-Length:0' -H 'User-Agent:' -X POST 'http://milton.bncollege.com/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd?termId=72257798&deptId=72257800&courseId=&sectionId=&storeId=82238&catalogId=10001&langId=-1&dropdown=term'
                client.get(CourseFindActivity.this,url,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        //todo update the department list adapter
                        try {

                            Section[] sections = new Section[response.length()];
                            for (int i = 0; i < response.length(); i++) {
                                sections[i] = new Section(response.getJSONObject(i).getString("categoryId"), response.getJSONObject(i).getString("categoryName"));
                            }

                            sectionSpinner.setAdapter(new SectionListAdapter(CourseFindActivity.this, android.R.layout.simple_spinner_item, sections));


                            deptSpinner.setEnabled(true);
                            courseSpinner.setEnabled(true);
                            sectionSpinner.setEnabled(true);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                    }


                });


            }
        });

        view = new WebView(getApplicationContext());

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36");
        view.getSettings().setLoadsImagesAutomatically(true);
        CookieManager.getInstance().setAcceptCookie(true);

        view.loadUrl("http://milton.bncollege.com/webapp/wcs/stores/servlet/TBWizardView?catalogId=10001&langId=-1&storeId=82238");
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(view,true);
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.evaluateJavascript("(function() { " +
                                "var elements = document.getElementsByClassName(\"bncbOptionItem\");" +
                                " if (elements.length > 0) { " +
                                  "var answer = {};" +
                                  "for (var i = 0; i < elements.length; i++) {" +
                                     "answer[elements[i].getAttribute(\"data-optionvalue\")] = elements[i].innerHTML;" +
                                  "}" +
                                  "return JSON.stringify(answer);" +
                                "} else { return \"nothing yet\";} })()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {

                                value = value.substring(1,value.length()-1);
                                value = value.replace("\\","");
                                Log.w("coursefind","oldvaue: " + value);
                                /**/
                                if (!value.equals("nothing yet")) {
                                    Log.w("coursefind","have data");
                                    dialog.dismiss();
                                    Log.w("coursefind","CAnceling timer");
                                    timer.cancel();
                                    try {
                                        JSONObject termsJson = new JSONObject(value);
                                        Term[] terms = new Term[termsJson.length()];
                                        for (int i = 0; i < termsJson.names().length(); i++) {
                                            String id = termsJson.names().getString(i);
                                            terms[i] = new Term(id, termsJson.getString(id));
                                        }

                                        termAdapter.notifyDataSetChanged();
                                        termSpinner.setAdapter(new TermListAdapter(CourseFindActivity.this, android.R.layout.simple_spinner_item, terms));

                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        });
                    }
                });

            }}, 0, 500);

        //curl -H 'Content-Length:0' -H 'User-Agent:TeamFoundation' -X POST 'http://milton.bncollege.com/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd?campusId=63074026&termId=72257798&deptId=72257800&courseId=&sectionId=&storeId=82238&catalogId=10001&langId=-1&dropdown=dept'`


        Button submitButton = (Button) findViewById(R.id.main_activity_fragment_find_books_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go pull the stuff from bncollege
                final WebView view2 = new WebView(getApplicationContext());

                view2.getSettings().setJavaScriptEnabled(true);
                view2.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36");
                view2.getSettings().setLoadsImagesAutomatically(true);
                CookieManager.getInstance().setAcceptCookie(true);

                view2.loadUrl(app_preferences.getString(PrefKeys.SCHOOL_BASE_URL,"") + "/webapp/wcs/stores/servlet/BNCBTBListView?" +
                        "storeId=" + app_preferences.getString(PrefKeys.SCHOOL_STORE_ID,"") +
                        "&catalogId=10001" + app_preferences.getString(PrefKeys.SCHOOL_CAT_ID,"") +
                        "&langId=-1" + app_preferences.getString(PrefKeys.SCHOOL_LANG_ID,"") +
                        "&section_2=" + ((Section) sectionSpinner.getSelectedItem()).id);
                CookieManager.getInstance().setAcceptCookie(true);
                CookieManager.getInstance().setAcceptThirdPartyCookies(view,true);
                final Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view2.evaluateJavascript("(function() { " +
                                        "var titles = document.getElementsByClassName(\"noImageDisReq\");\n" +
                                        "var isbns = document.getElementsByClassName(\"book_desc1\")\n" +
                                        "if(titles.length > 0) {\n" +
                                        "    var answer = {};\n" +
                                        "    for (var i=0; i < titles.length; i++) {\n" +
                                        "        answer[titles[i].getAttribute(\"title\")] = isbns[i].getElementsByTagName(\"ul\")[0].getElementsByTagName(\"li\")[2].innerHTML\n" +
                                        "    }\n" +
                                        "return JSON.stringify(answer);" +
                                        "}" +
                                        "else { return \"nothing yet\";} })()", new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {

                                        value = value.substring(1,value.length()-1);
                                        value = value.replace("\\","");
                                        Log.w("findbooks","oldvaue: " + value);
                                /**/
                                        if (!value.equals("nothing yet")) {
                                            Log.w("findbooks","have data");
                                            dialog.dismiss();
                                            Log.w("findbooks","CAnceling timer");
                                            timer.cancel();
                                            try {
                                                Intent tent = new Intent(CourseFindActivity.this,BookViewActivity.class);
                                                tent.putExtra("bookJson",value);
                                                startActivity(tent);
                                                finish();

                                            }
                                            catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }
                                });
                            }
                        });

                    }}, 0, 500);

            }
        });

        //http://milton.bncollege.com/webapp/wcs/stores/servlet/BNCBTBListView?storeId=82238&catalogId=10001&langId=-1&section_2=70205613
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!AWSMobileClient.defaultMobileClient().getIdentityManager().isUserSignedIn()) {
            // In the case that the activity is restarted by the OS after the application
            // is killed we must redirect to the splash activity to handle the sign-in flow.
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }

        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // register notification receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver,
                new IntentFilter(com.teamfoundationandroid.app.PushListenerService.ACTION_SNS_NOTIFICATION));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here excluding the home button.

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
        // Save the title so it will be restored properly to match the view loaded when rotation
        // was changed or in case the activity was destroyed.
    }

    @Override
    public void onClick(final View view) {
        if (view == signOutButton) {
            // The user is currently signed in with a provider. Sign out of that provider.
            identityManager.signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }

        // ... add any other button handling code here ...

    }

    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "Received notification from local broadcast. Display it in a dialog.");

            Bundle data = intent.getBundleExtra(PushListenerService.INTENT_SNS_NOTIFICATION_DATA);
            String message = PushListenerService.getMessage(data);

            new AlertDialog.Builder(CourseFindActivity.this)
                    .setTitle(R.string.push_demo_title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();

        // unregister notification receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver);
    }


}
