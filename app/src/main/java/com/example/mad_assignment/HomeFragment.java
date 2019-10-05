package com.example.mad_assignment;

import android.content.Intent;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.android.volley.toolbox.JsonObjectRequest;


public class HomeFragment extends Fragment {

	Button loginButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private FloatingActionButton floatingActionButton;

    /*
       We need to perform network based request. FOr doing that we are using Volley.
       We also need internet permission for that in our manifest file.
     */
    private static final String API = "https://jericho.pnisolutions.com.au/Students/getClasses";
    private JSONObject jsonBody = new JSONObject();
    private List<LectureDetails> lectureDetailsList;

    // We are using joda time for easier time formatting.
    private DateTimeFormatter dateFormat = ISODateTimeFormat.dateTime();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, null);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

       // The code below is flexible, but is it efficient?
       // this.API_URL = getString(R.string.API_URL);

        // We use recycler view to display data as a vertical scrollable screen.
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // Every component will have a same size
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // A floating action button to facilitate manual refresh.
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Load the recycler view data whenever the users presses the floating action button.
                new LoadRecyclerViewDataAsync().execute();
            }
        });

        // The actual list that will store the LectureDetail components.
        lectureDetailsList = new ArrayList<>();

        // We have to set the adapter to empty values to prevent warnings and exceptions.
        adapter = new LectureDetailsAdapter(lectureDetailsList, getContext());


        recyclerView.setAdapter(adapter);


        // Fetch the data Asynchronously, have not implemented the loading circle yet!
        new LoadRecyclerViewDataAsync().execute();


    }

    /*
        Method to Asynchronously load the data for the Recycler View.
     */
    public class LoadRecyclerViewDataAsync extends AsyncTask<String, String, Void>{

        @Override
        protected Void doInBackground(String... strings) {

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

                            // We will get the whole JSON in here.
                            lectureDetailsList = new ArrayList<>();

                            try {

                                //JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = response.getJSONArray("data");

                                for(int i=0; i<jsonArray.length(); i++) {
                                   JSONObject obj = jsonArray.getJSONObject(i);

                                    LectureDetails lectureDetail = new LectureDetails(
                                            obj.getString("CourseCode"),
                                            obj.getString("Room"),
                                            new Timestamp( dateFormat.parseDateTime(obj.getString("Time")).getMillis()), //Timestamp
                                            obj.getDouble("Duration"),
                                            obj.getInt("isRunning")==1

                                    );

                                    lectureDetailsList.add(lectureDetail);
                                }

                                adapter = new LectureDetailsAdapter(lectureDetailsList, getContext());

                                recyclerView.setAdapter(adapter);

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

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);

            return null;
        }
    }

    //region Helper Method to convert a string into Timestamp
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
    //endregion


}
