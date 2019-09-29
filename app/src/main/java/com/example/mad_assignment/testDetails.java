package com.example.mad_assignment;
import java.sql.Timestamp;

public class testDetails {

    String response, data;

    public testDetails(String response, String data) {
        this.response = response;
        this.data = data;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
