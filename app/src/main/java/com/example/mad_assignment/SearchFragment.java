package com.example.mad_assignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

public class SearchFragment extends Fragment {

    private SearchView searchBar;
    private RecyclerView searchRecyclerView;
    private RecyclerView.Adapter adapter;
    private TextView courseCode;
    private TextView outcomeLabel;

    private static final String SearchAPI = "https://jericho.pnisolutions.com.au/Public/getSubject";//http://my-json-server.typicode.com/RahulRathodGitHub/demoJSON/lectures/";
    private JSONObject jsonBody = new JSONObject();
    private List<LectureDetails> lectureDetailsList;
    private TextView NoInetSearch;

    // We are using joda time for easier time formatting.
    private DateTimeFormatter dateFormat = ISODateTimeFormat.dateTime();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search, null);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        searchBar = (SearchView) view.findViewById(R.id.searchBar);
        courseCode = (TextView) view.findViewById(R.id.CourseCode);
        outcomeLabel = (TextView) view.findViewById(R.id.outcomeLabel);
        NoInetSearch = (TextView) view.findViewById(R.id.NoInetSearch);

        searchRecyclerView = (RecyclerView) view.findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        lectureDetailsList = new ArrayList<>();

        adapter = new LectureDetailsAdapter(lectureDetailsList, getContext());

        searchBar.setQueryHint("Search Course Code");

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                courseCode.setText("Course Code: "+s.toUpperCase());

                if(s!="")
                {
                    courseCode.setVisibility(View.VISIBLE);
                    outcomeLabel.setVisibility(View.VISIBLE);
                }
                else
                {
                    courseCode.setVisibility(View.INVISIBLE);
                    outcomeLabel.setVisibility(View.INVISIBLE);
                }

                new LoadRecyclerViewDataAsync().execute(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.isEmpty())
                {
                    courseCode.setVisibility(View.INVISIBLE);
                    outcomeLabel.setVisibility(View.INVISIBLE);

                    new LoadRecyclerViewDataAsync().execute("");
                }
                return false;
            }
        });

        searchRecyclerView.setAdapter(adapter);

        new LoadRecyclerViewDataAsync().execute();


    }

    public class LoadRecyclerViewDataAsync extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... strings) {

            String query = "";
            if(strings.length > 0)
            {
                query = strings[0];
            }
            final String finalQuery = query;

            try{
                jsonBody.put("CourseCode", finalQuery.toUpperCase());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            final String requestBody = jsonBody.toString();

            JsonObjectRequest jsonObjectRequest =  new JsonObjectRequest(Request.Method.POST,
                    SearchAPI,
                    jsonBody,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            // We will get the whole JSON in here.
                            lectureDetailsList = new ArrayList<>();

                            try {
                                NoInetSearch.setVisibility(View.INVISIBLE);

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

                                searchRecyclerView.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                            if (volleyError instanceof NetworkError || volleyError instanceof AuthFailureError || volleyError instanceof TimeoutError) {

                                NoInetSearch.setVisibility(View.VISIBLE);
                                Log.d(">>>>>>>>>>>>","Internet Error: "+volleyError.getMessage());

                            }

                            Log.d(">>>>>>>>>>>>",""+volleyError.getMessage());
                            // Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG);
                        }
                    });


            // Now we have the request, to execute it we need a requst queue.

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(jsonObjectRequest);

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
}
