package com.teamfoundationandroid.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.util.Timer;
import java.util.TimerTask;

public class CourseFindActivity extends AppCompatActivity {
    WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_find_activity);
        view = new WebView(getApplicationContext());

        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36");
        view.getSettings().setLoadsImagesAutomatically(true);
        CookieManager.getInstance().setAcceptCookie(true);

        view.loadUrl("http://milton.bncollege.com/webapp/wcs/stores/servlet/TBWizardView?catalogId=10001&langId=-1&storeId=82238");
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(view,true);
        start();
        //curl -H 'Content-Length:0' -H 'User-Agent:TeamFoundation' -X POST 'http://milton.bncollege.com/webapp/wcs/stores/servlet/TextBookProcessDropdownsCmd?campusId=63074026&termId=72257798&deptId=72257800&courseId=&sectionId=&storeId=82238&catalogId=10001&langId=-1&dropdown=dept'`

        //curl 'http://bncollege.com/partners-search/' --data 'school_name=milton&school_type_id=&state_id='


    }

    private Timer timer;
    private TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    view.evaluateJavascript("(function() { return document.getElementsByClassName(\"bncbOptionItem\")[0].outerHTML; })()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            System.out.println("hi there");
                            System.out.println(value);
                        }
                    });
                }
            });

        }
    };

    public void start() {
        if(timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 2000);
    }

    public void stop() {
        timer.cancel();
        timer = null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.course_find_menu, menu);
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
