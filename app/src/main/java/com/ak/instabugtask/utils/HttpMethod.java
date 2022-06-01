package com.ak.instabugtask.utils;

public enum HttpMethod {
    GET(0), POST(1);
    private final int methodCode;

    HttpMethod(int methodCode) {
        this.methodCode = methodCode;
    }

    public int getMethodCode() {
        return methodCode;
    }
}
