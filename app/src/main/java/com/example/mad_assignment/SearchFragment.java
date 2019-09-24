package com.example.mad_assignment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.AttrRes;
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
    private static final String dummy_url = "http://my-json-server.typicode.com/RahulRathodGitHub/demoJSON/lectures/";

    private List<LectureDetails> lectureDetailsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search, null);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        searchBar = (SearchView) view.findViewById(R.id.searchBar);

        searchRecyclerView = (RecyclerView) view.findViewById(R.id.searchRecyclerView);
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        lectureDetailsList = new ArrayList<>();

        adapter = new LectureDetailsAdapter(lectureDetailsList, getContext());

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                List<LectureDetails> lectureDetails = new ArrayList<>();

                for(LectureDetails lectureDetail: lectureDetailsList){

                    if(lectureDetail.courseName.contains(s)){
                        lectureDetails.add(lectureDetail);
                    }
                }

                new LoadRecyclerViewDataAsync().execute(s);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
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

            final StringRequest stringRequest =  new StringRequest(Request.Method.GET,
                    dummy_url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            // We will get the whole JSON in here.
                            lectureDetailsList = new ArrayList<>();

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

                                adapter = new LectureDetailsAdapter(lectureDetailsList, getContext());

                                searchRecyclerView.setAdapter(adapter);

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
}
