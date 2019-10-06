package com.example.mad_assignment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MenuItem;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;
    ViewPager viewPager;

    AlarmManager[] alarmManagers;
    ArrayList<PendingIntent> intentArray;
    Calendar[] calendars;
    //Lets assume that the below is the data we get from the server.
    ArrayList<Long> alarmSchedules = new ArrayList<>();

    private static final String API = "https://jericho.pnisolutions.com.au/Students/getClasses";
    private JSONObject jsonBody = new JSONObject();

    SharedPreferences sharedPreferences;


    int notifyMeBeforeHours = 0;
    int notifyMeBeforeMinutes = 0;

    public void changeNotificatonHours(int notifyMeBeforeHours)
    {
        this.notifyMeBeforeHours = notifyMeBeforeHours;

        sharedPreferences.edit().putInt("notifyMeBeforeHours", notifyMeBeforeHours).commit();

        cancelAlarms();
        setAlarms();
    }

    public void changeNotificatonMinutes(int notifyMeBeforeMinutes)
    {
        this.notifyMeBeforeMinutes = notifyMeBeforeMinutes;

        sharedPreferences.edit().putInt("notifyMeBeforeMinutes", notifyMeBeforeMinutes).commit();

        cancelAlarms();
        setAlarms();
    }


    public static void setupFm(FragmentManager fragmentManager, ViewPager viewPager){
        FragmentAdapter Adapter = new FragmentAdapter(fragmentManager);
        //Add All Fragment To List
        Adapter.add(new HomeFragment(), "Home Page");
        Adapter.add(new SearchFragment(), "Search Page");
        Adapter.add(new SettingsFragment(), "Settings Page");
        viewPager.setAdapter(Adapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_search:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_settings:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("MyPreferences", 0);

        // Initialized the navigation View.
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager = findViewById(R.id.viewPager);
        setupFm(getSupportFragmentManager(), viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new PageChange());

        // The variables below would be initialized with the values from shared utils.
         notifyMeBeforeHours = sharedPreferences.getInt("notifyMeBeforeHours", 0);
         notifyMeBeforeMinutes = sharedPreferences.getInt("notifyMeBeforeMinutes", 0);

       // if(isInternetAvailable()) {
            new SetAlarmSchedulesAsync().execute("" + notifyMeBeforeHours, "" + notifyMeBeforeMinutes);
       // }
       // else
       // {
            //TODO Handle no internet connection
       // }

        setAlarms();


    }

    private boolean isInternetAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo internetInfo = connectivityManager.getActiveNetworkInfo();
        return internetInfo != null && internetInfo.isConnected();

    }

    /*
        Method to Asynchronously load the data for the Recycler View.
     */
    public class SetAlarmSchedulesAsync extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(final String... strings) {

            // In this method, we will fetch data from internet.
            // As the data is coming from internet, it might take some time, so we will show a progress dialog.

            // Now through stringRequest of volley we wil make a string request
            try{
                jsonBody.put("Identifier", "18916900");
                jsonBody.put("Password", "1234");
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            final String requestBody = jsonBody.toString();

            JsonObjectRequest jsonObjectRequest =  new JsonObjectRequest(Request.Method.POST,
                    API,
                    jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                //JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = response.getJSONArray("data");

                                for(int i=0; i<jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    alarmSchedules.add(DateTime.parse(obj.getString("Time"))
                                            .plusHours(Integer.valueOf(strings[0]))
                                            .plusMinutes(Integer.valueOf(strings[1]))
                                            .getMillis());

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d(">>>>>>>>>>>>",""+error.getMessage());

                            if(error instanceof TimeoutError || error instanceof NoConnectionError){

                            }
                            else if(error instanceof AuthFailureError){

                            }
                            else if(error instanceof ServerError){

                            }
                            else if(error instanceof ParseError){

                            }
                        }
                    });

            // Now we have the request, to execute it we need a requst queue.

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);

            return null;
        }
    }

    public void setAlarms()
    {
        calendars = new Calendar[alarmSchedules.size()];

        alarmManagers = new AlarmManager[alarmSchedules.size()];

        intentArray = new ArrayList<>();

        for(int i=0; i<alarmSchedules.size(); i++)
        {
            calendars[i] = Calendar.getInstance();
            calendars[i].setTimeInMillis(alarmSchedules.get(i));
            //calendars[i].set(Calendar.HOUR_OF_DAY, 23);
            //calendars[i].set(Calendar.MINUTE, alarmSchedules.get(i));

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.setAction("MY_NOTIFICATION_MESSAGE");

            intent.putExtra("requestCode", i);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManagers[i] = (AlarmManager) getSystemService(ALARM_SERVICE);

            alarmManagers[i].setExact(AlarmManager.RTC_WAKEUP, calendars[i].getTimeInMillis(), pendingIntent);

            intentArray.add(pendingIntent);

        }
    }


    public void cancelAlarms()
    {
        for(int i=0; alarmManagers!=null && i<alarmManagers.length; i++)
        {
            alarmManagers[i].cancel(intentArray.get(i));
        }
    }





    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    navView.setSelectedItemId(R.id.navigation_home);
                    break;
                case 1:
                    navView.setSelectedItemId(R.id.navigation_search);
                    break;
                case 2:
                    navView.setSelectedItemId(R.id.navigation_settings);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
