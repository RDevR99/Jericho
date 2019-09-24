package com.example.mad_assignment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String API_URL = "http://my-json-server.typicode.com/RahulRathodGitHub/demoJSON/lectures/";
    private List<LectureDetails> lectureDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lectureDetailsList =  new ArrayList<>();

        // Initialized the navigation View.
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);

        loadFragment(new HomeFragment());

    }

    public List<LectureDetails> getLectureList()
    {
        return loadRecyclerViewData();
        //new LoadRecyclerViewDataAsync().execute();
        //return lectureDetailsList;
    }

    private List<LectureDetails> loadRecyclerViewData()
    {
        // In this method, we will fetch data from internet.
        // As the data is coming from internet, it might take some time, so we will show a progress dialog.

        // Now trought stringRequest of volley we wil make a string request

        StringRequest stringRequest =  new StringRequest(Request.Method.GET,
                API_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {


                        // We will get the whole JSON in here.

                        try {

                            //JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(response);

                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                LectureDetails lectureDetail = new LectureDetails(
                                        obj.getString("courseName"),
                                        obj.getString("venue"),
                                        toTimestamp(obj.getString("scheduledStart")),
                                        obj.getDouble("duration"),
                                        obj.getBoolean("isActive")
                                );

                                lectureDetailsList.add(lectureDetail);
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
                        // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG);
                    }
                });

        // Now we have the request, to execute it we need a requst queue.

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

        return lectureDetailsList;

    }


    public class LoadRecyclerViewDataAsync extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            // In this method, we will fetch data from internet.
            // As the data is coming from internet, it might take some time, so we will show a progress dialog.

            // Now through stringRequest of volley we wil make a string request

            StringRequest stringRequest =  new StringRequest(Request.Method.GET,
                    API_URL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            // We will get the whole JSON in here.

                            try {

                                //JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = new JSONArray(response);

                                for(int i=0; i<jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    LectureDetails lectureDetail = new LectureDetails(
                                            obj.getString("courseName"),
                                            obj.getString("venue"),
                                            toTimestamp(obj.getString("scheduledStart")),
                                            obj.getDouble("duration"),
                                            obj.getBoolean("isActive")
                                    );

                                    lectureDetailsList.add(lectureDetail);
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
                            // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG);
                        }
                    });

            // Now we have the request, to execute it we need a requst queue.

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);

            return null;
        }
    }

    private Timestamp toTimestamp(String timeStampString)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(timeStampString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp timestamp = new Timestamp(((Date) parsedDate).getTime());

        return timestamp;
    }

    private boolean loadFragment(Fragment fragment){

        if(fragment != null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }

        return false;

    }

   /* public void refreshFragmentUI(Fragment fragment) {

        if(fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .detach(fragment)
                    .attach(fragment)
                    .commit();
        }
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment fragment = null;

        switch(menuItem.getItemId()){

            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;
            case R.id.navigation_search:
                fragment = new SearchFragment();
                break;
            case R.id.navigation_settings:
                fragment = new SettingsFragment();
                break;
        }

        return loadFragment(fragment);
    }
}
