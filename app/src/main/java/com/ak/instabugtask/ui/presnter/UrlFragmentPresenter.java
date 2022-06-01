package com.ak.instabugtask.ui.presnter;

import com.ak.instabugtask.repository.WebService;
import com.ak.instabugtask.utils.HttpMethod;
import com.ak.instabugtask.model.Request;

import java.util.Map;


public class UrlFragmentPresenter implements WebService.WebServiceCallback {

    private final UrlFragmentPresenterListener view;
    private final Request req;

    public UrlFragmentPresenter(UrlFragmentPresenterListener view, Request req) {
        this.view = view;
        this.req = req;
    }

    public Request getReq() {
        return req;
    }

    public void execute(String url, String reqBody) {
        req.setUrlStr(url.trim());
        req.setBody(reqBody.trim());
        if (!req.getHeaders().containsKey("Accept"))
            req.getHeaders().put("Accept", "*/*");
        if (!req.getHeaders().containsKey("Content-Type"))
            req.getHeaders().put("Content-Type", "text/plain");
//            req.getHeaders().put("Content-Type", "application/json");
        WebService webService = new WebService(this, req);
        webService.execute();
    }

    private String convertRequestToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Request:\nHeaders:\n");
        for (Map.Entry<String, String> entry : req.getHeaders().entrySet()) {
            sb.append(entry.getKey());
            sb.append(" : ");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        if (req.getHttpMethod() == HttpMethod.POST) {
            sb.append("------------------------------------\nbody: ");
            sb.append(req.getBody());
        } else {
            sb.append(" ------------------------------------\nQueryParams: ");
            sb.append(req.getUrlParams());
        }
        sb.append("\n");

        return sb.toString();
    }

    @Override
    public void onSuccess(String resStr) {
        String reqStr = convertRequestToString();
        String result = reqStr + "\n------------------------------------------------------------------------\n" + resStr;
        view.onResponse(result);
    }

    @Override
    public void onFailure(String result) {
        view.onResponse(result);
    }

    public interface UrlFragmentPresenterListener {
        void onResponse(String result);

//        void onFailure(String result);
    }
}
