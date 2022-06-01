package com.ak.instabugtask.model;

import com.ak.instabugtask.utils.HttpMethod;

import java.util.Map;

public class Request {
    private HttpMethod httpMethod;
    private String urlStr;
    private String urlParams;
    private String body;
    private Map<String, String> headers;

    public Request(HttpMethod httpMethod, String urlStr, String body, Map<String, String> headers) {
        this.httpMethod = httpMethod;
        this.urlStr = urlStr;
        this.body = body;
        this.headers = headers;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(String urlParams) {
        this.urlParams = urlParams;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}