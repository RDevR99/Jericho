package com.example.mad_assignment;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LectureDetailsAdapter extends RecyclerView.Adapter<LectureDetailsAdapter.ViewHolder> {

    private List<LectureDetails> lectureDetailsList;
    private Context context;

    public LectureDetailsAdapter(List<LectureDetails> lectureDetailsList, Context context) {
        this.lectureDetailsList = lectureDetailsList;
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

        LectureDetails lectureDetail = lectureDetailsList.get(position);

        holder.lecturename.setText(lectureDetail.getLectureName());
        holder.lecturePlace.setText(lectureDetail.getLecturePlace());
    }

    @Override
    public int getItemCount() {
        return lectureDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView lecturename, lecturePlace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lecturename = (TextView) itemView.findViewById(R.id.lectureName);
            lecturePlace = (TextView) itemView.findViewById(R.id.lecturePlace);
        }
    }

}
