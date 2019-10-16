package com.example.mad_assignment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MenuItem;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    //region Screen Navigation and State Management

    // Bottom Navigation for swapping between fragments
    BottomNavigationView navView;

    // Facilitate animations and state between tabs and fragments.
    ViewPager viewPager;

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

    // Shared preference to store global key value pairs for the application.
    SharedPreferences sharedPreferences;

    /*
        The function below sets the adapter with our fragments to a viewPager
     */
    public static void setupFm(FragmentManager fragmentManager, ViewPager viewPager) {
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

    //endregion

    //region Alarms and Notifications Handling

    // To Manage Alarms
    AlarmManager[] alarmManagers;
    // List of Intents which represent alarms
    ArrayList<PendingIntent> intentArray;
    // Array of calendar Objects to represent time.
    Calendar[] calendars;

    //The below is the Alarm data we get from the server.
    ArrayList<DateTime> alarmSchedules = new ArrayList<>();

    // Server's API URL to get all the schedules for all the classes.
    private static final String API = "https://jericho.pnisolutions.com.au/Students/getClasses";

    // JSON object to represent the data obtained from the server.
    private JSONObject jsonBody = new JSONObject();

    // No. of hours prior the lecture that the user want the notifications on.
    int notifyMeBeforeHours = 0;
    // No. of Minutes prior the lecture that the user want the notifications on.
    int notifyMeBeforeMinutes = 0;

    /*
        If the user changes the number of hours when he/she wants to get notified,
        This block of code will be executed
     */
    public void changeNotificatonHours(int notifyMeBeforeHours) {
        this.notifyMeBeforeHours = notifyMeBeforeHours;

        // We store the notifications settings in the shared preferences.
        sharedPreferences.edit().putInt("notifyMeBeforeHours", notifyMeBeforeHours).commit();

        // By doing the following, we refresh the alarm schedule.
        cancelAlarms();
        setAlarms();
    }

    public void changeNotificatonMinutes(int notifyMeBeforeMinutes) {
        this.notifyMeBeforeMinutes = notifyMeBeforeMinutes;

        sharedPreferences.edit().putInt("notifyMeBeforeMinutes", notifyMeBeforeMinutes).commit();

        cancelAlarms();
        setAlarms();
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
            try {
                //TODO: Remove the line below and uncomment the couple of lines underneath it.
                jsonBody.put("CourseCode", "CSE2MAD");
                //jsonBody.put("Identifier", "18916900");
                //jsonBody.put("Password", "1234");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String requestBody = jsonBody.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    API,
                    jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                //JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = response.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    alarmSchedules.add(DateTime.parse(obj.getString("Time"))
                                            .minusHours(Integer.valueOf(strings[0]))
                                            .minusMinutes(Integer.valueOf(strings[1]))
                                            );

                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("HI>>>>>>>>>>", "" + error.getMessage());
                        }
                    });

            // Now we have the request, to execute it we need a requst queue.

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);

            return null;
        }
    }

    /*
        The function below allows us to set times for our alarms.
     */
    public void setAlarms() {

        // Calendar array to store all the alarm times.
        calendars = new Calendar[alarmSchedules.size()];

        // Alarm Managers that handle each alarm.
        alarmManagers = new AlarmManager[alarmSchedules.size()];

        // Intent for each alarm are stored in the array below.
        intentArray = new ArrayList<>();

        // For each of the alarm we want to schedule...
        for (int i = 0; i < alarmSchedules.size(); i++) {

            // We initialize all the calendar instances and set the required time when we want to receive the alarms
            calendars[i] = Calendar.getInstance();

            // We then set the time that the calendar object represents.
            DateTime calendarTime = alarmSchedules.get(i).minusHours(sharedPreferences.getInt("notifyMeBeforeHours", 0)).minusMinutes(sharedPreferences.getInt("notifyMeBeforeMinutes", 0));

            // If the the time set if before or equal to now then the user will get an immediate alarm.
            // To avoid such a circumstance we are having an if condition in here.
            if(calendarTime.isBeforeNow() || calendarTime.isEqualNow())
            {
                calendars[i].setTime(alarmSchedules.get(i).plusWeeks(1).toDate());
            }
            else {
                calendars[i].setTime(calendarTime.toDate());
            }

            // TODO: Remove the log below
            Log.d("This is the actual time", ""+calendars[i]);
            Log.d("THis is the time string", ""+ calendarTime.toDate());

            // Notification Intent is initiated and configured.
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.setAction("MY_NOTIFICATION_MESSAGE");

            // The unique request code for each notification is needed by the receiver receiving the intent so that it can identify what request codes it is open to
            // However, In our case we give away the request code as the receiver needs to listen to multiple requests which are not feasible to hardcode.
            intent.putExtra("requestCode", i);
            intent.putExtra("Lecture Name","MAD");
            intent.putExtra("Notification Text", "HI, Now is your lecture");

            // The pending intent acts as a token that we provide the alarm manager, so that it get the permission to carry out its task.
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // We instantiate the AlarmManager.
            alarmManagers[i] = (AlarmManager) getSystemService(ALARM_SERVICE);

            // We set the alarmManager time, The first parameter facilitates the alarm to appear as a notification even if the application is sleeping.
            alarmManagers[i].setExact(AlarmManager.RTC_WAKEUP, calendars[i].getTimeInMillis(), pendingIntent);

            // We store the intents in the intentArray.
            intentArray.add(pendingIntent);

        }
    }

    /*
        The code below cancels a given alarm.
        This is where our stored intents comes handy.
        Those intents will be used to cancel the alarms scheduled.
     */
    public void cancelAlarms() {
        for (int i = 0; alarmManagers != null && i < alarmManagers.length; i++) {
            alarmManagers[i].cancel(intentArray.get(i));
        }
    }

    //endregion

    //region Authentication

    //TODO: SetAlarmSchedulesAsync can be used instead of calling authenticatUser.
    public class AuthenticateUser extends AsyncTask<String, String, Boolean> {

        boolean resp = false;


        @Override
        protected Boolean doInBackground(String... strings) {

            try {
                jsonBody.put("Identifier", strings[0]);
                jsonBody.put("Password", strings[1]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String requestBody = jsonBody.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    API,
                    jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONObject jsonObject = new JSONObject("response");

                                String respString = jsonObject.getString("response");
                                //JSONArray jsonArray = response.getJSONArray("");

                                resp = respString.equalsIgnoreCase("success");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d(">>>>>>>>>>>>", "" + error.getMessage());

                        }
                    });

            // Now we have the request, to execute it we need a requst queue.

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(jsonObjectRequest);

            return resp;
        }
    }


    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Get the shared preferences to access the key value pairs
        sharedPreferences = this.getSharedPreferences("MyPreferences", 0);

        // Initialized the navigation View.
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Initialize the ViewPager
        viewPager = findViewById(R.id.viewPager);
        setupFm(getSupportFragmentManager(), viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new PageChange());

        // The variables below would be initialized with the values from shared utils.
        notifyMeBeforeHours = sharedPreferences.getInt("notifyMeBeforeHours", 0);
        notifyMeBeforeMinutes = sharedPreferences.getInt("notifyMeBeforeMinutes", 0);

        // We will execute the task to schedule the alarms/Notifications
        new SetAlarmSchedulesAsync().execute("" + notifyMeBeforeHours, "" + notifyMeBeforeMinutes);

        //  alarmSchedules.add(new DateTime().plusHours(notifyMeBeforeHours).plusMinutes(notifyMeBeforeMinutes).getMillis());

        setAlarms();


    }

    /*
        The method below is to facilitate the firebase analytics
        This method will log the search term that the user will enter to look for a lecture.
        The analysis would help us to identify most searched lectures.
     */
    public void logSearchEvent(String query)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, query);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }

    /*
        The method below would help us log a login event to the firebase
        The resultant analytics will help us get the amount of users logging in our app.
     */
    public void logLoginEvent()
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "In App (using NODE.JS)");
        Log.d("Query to be logged","Login activity is being logged");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }


    /*
        If the user did not opt to store his./her password then, during onDestroy,
        The user password would be set to null.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Boolean rememberPassword = sharedPreferences.getBoolean("rememberPassword", false);

        if(!rememberPassword)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("password", null);
        }

    }
}
