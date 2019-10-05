package com.example.mad_assignment;

import android.app.LauncherActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/*
    This Adapter is utilized by the recycler view to populate lecture details in a vertical scrollable list of cards.
 */
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

        double lectureDurationdouble = lectureDetail.getDuration();
        Timestamp lectureStartTime = lectureDetail.getScheduledStart();
        boolean lectureIsActive = lectureDetail.isActive;


        holder.lecturename.setText(lectureDetail.getCourseName());
        holder.lecturePlace.setText("VENUE: "+lectureDetail.getVenue());
        holder.lectureDuration.setText("DURATION: "+ lectureDurationdouble);
        holder.lectureTime.setText("START: " + lectureStartTime.toLocaleString());
        holder.lectureStatus.setText(getLectureStatus(lectureStartTime, lectureDurationdouble, lectureIsActive));

    }

    // This method decide the lecture status
    private String getLectureStatus(Timestamp lectureStartTime, Double lectureDurationdouble, Boolean lectureIsActive)
    {
        DateTime lectureStartDateTime = new DateTime(lectureStartTime);

        if(!lectureIsActive)
            return "CANCELLED";

        else if(
                (lectureStartDateTime.isEqualNow())
                 ||
                (new DateTime().isAfter(lectureStartDateTime) && new DateTime().isBefore(lectureStartDateTime.plusMinutes((int)(lectureDurationdouble*60))))
               )
            return "RUNNING";

        else if(new DateTime().isAfter(lectureStartDateTime.plusMinutes((int)(lectureDurationdouble*60))))
            return "FINISHED";

        else
            return "SCHEDULED TO RUN";
    }


    @Override
    public int getItemCount() {
        return lectureDetailsList.size();
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
