package com.example.mad_assignment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private static final String dummy_url = "https://simplifiedcoding.net/demos/marvel/";
    // We need to perform network based request. FOr doing that we are using Volley.
    // We also need internet permission for that in our manifest file.

    private List<LectureDetails> lectureDetailsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, null);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        lectureDetailsList = new ArrayList<>();

        adapter = new LectureDetailsAdapter(lectureDetailsList, getContext());

        recyclerView.setAdapter(adapter);

        // TO fetch the data
        loadRecyclerViewData();

        /* ===============DUMMY HARDCODED DATA===============
        for(int i=0; i<=10; i++)
        {
            LectureDetails lectureDetail = new LectureDetails(
                    "CSE"+(i+1)+"MAD",
                    "GS"
            );

            lectureDetailsList.add(lectureDetail);
        }
        */

    }

    private void loadRecyclerViewData()
    {
        // In this method, we will fetch data from internet.
        // As the data is coming from internet, it might take some time, so we will show a progress dialog.

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.show();

        // Now sing volley we wil make a string request

        StringRequest stringRequest =  new StringRequest(Request.Method.GET,
                dummy_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        // We will get the whole JSON in here.

                        try {

                            //JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = new JSONArray(response);

                            for(int i=0; i<jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                LectureDetails lectureDetail = new LectureDetails(
                                        obj.getString("name"),
                                        obj.getString("team")
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
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG);
                    }
                });

        // Now we have the request, to execute it we need a requst queue.

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

}
