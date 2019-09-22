package com.example.mad_assignment;

public class LectureDetails {

    String lectureName, lecturePlace;

    public LectureDetails(String lectureName, String lecturePlace) {
        this.lectureName = lectureName;
        this.lecturePlace = lecturePlace;
    }

    public String getLectureName() {
        return lectureName;
    }

    public void setLectureName(String lectureName) {
        this.lectureName = lectureName;
    }

    public String getLecturePlace() {
        return lecturePlace;
    }

    public void setLecturePlace(String lecturePlace) {
        this.lecturePlace = lecturePlace;
    }
}
