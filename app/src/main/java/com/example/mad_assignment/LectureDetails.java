package com.example.mad_assignment;
import java.sql.Timestamp;

public class LectureDetails {

    String courseName, venue;
    Timestamp scheduledStart;
    Double duration;
    Boolean isActive;

    public LectureDetails(String courseName, String venue) {
        this.courseName = courseName;
        this.venue = venue;
    }

    public LectureDetails(String courseName, String venue, Timestamp scheduledStart, Double duration, Boolean isActive) {
        this.courseName = courseName;
        this.venue = venue;
        this.scheduledStart = scheduledStart;
        this.duration = duration;
        this.isActive = isActive;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Timestamp getScheduledStart() {
        return scheduledStart;
    }

    public void setScheduledStart(Timestamp scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
