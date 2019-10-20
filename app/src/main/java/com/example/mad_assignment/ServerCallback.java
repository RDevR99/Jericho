package com.example.mad_assignment;

/*
    The interface is used to facilitate waiting on a response object from an HTTP request.
 */
public interface ServerCallback {
    void onSuccess(Boolean result);
}
