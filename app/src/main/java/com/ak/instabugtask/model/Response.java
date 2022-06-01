package com.ak.instabugtask.model;

import com.ak.instabugtask.utils.HttpMethod;

import java.util.Map;

public class Response {
    private int statusCode;
    private String body;
    private String headers;

    public Response(int statusCode, String body, String headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }
}
