package com.example.mad_assignment;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class testDetailsAdapter extends RecyclerView.Adapter<testDetailsAdapter.ViewHolder> {

    private List<testDetails> testDetailsList;
    private Context context;

    public testDetailsAdapter(List<testDetails> testDetailsList, Context context) {
        this.testDetailsList = testDetailsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lecture_list, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        testDetails testDetail = testDetailsList.get(position);

        holder.lecturename.setText(testDetail.getResponse());
        holder.lecturePlace.setText("VENUE: "+testDetail.getData());
        holder.lectureDuration.setText("DURATION: ");
        holder.lectureTime.setText("START: ");
        holder.lectureStatus.setText("RUNNING");
    }

    // This method decide the lecture status
    private String getLectureStatus(Timestamp lectureStartTime, Double lectureDurationdouble, Boolean lectureIsActive)
    {
        if(!lectureIsActive) return "CANCELLED";
        //LOGIC TO BE DONE IN HERE.
        return "RUNNING";
    }


    @Override
    public int getItemCount() {
        return testDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView lecturename, lecturePlace, lectureTime, lectureDuration, lectureStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lecturename = (TextView) itemView.findViewById(R.id.lectureName);
            lecturePlace = (TextView) itemView.findViewById(R.id.lecturePlace);
            lectureTime = (TextView) itemView.findViewById(R.id.lectureTime);
            lectureDuration = (TextView) itemView.findViewById(R.id.lectureDuration);
            lectureStatus = (TextView) itemView.findViewById(R.id.lectureStatus);

        }
    }

}
