package com.ak.instabugtask.repository;

import android.os.AsyncTask;
import android.util.Log;

import com.ak.instabugtask.utils.HttpMethod;
import com.ak.instabugtask.model.Request;
import com.ak.instabugtask.utils.WebServiceStatus;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class WebService extends AsyncTask<String, String, String> {

    private static final String TAG = "WebService";
    private final WebServiceCallback callback;
    private final Request req;

    public WebService(WebServiceCallback callback, Request req) {
        this.callback = callback;
        this.req = req;
    }

    @Override
    protected String doInBackground(String... strings) {
        URL url;
        HttpURLConnection urlConnection;
        try {
            url = new URL(req.getUrlStr());
            urlConnection = (HttpURLConnection) url.openConnection();
            req.setUrlParams(url.getQuery());
        } catch (MalformedURLException e) { //URL throws
            Log.e(TAG, "doInBackground: URL error", e);
            publishProgress(WebServiceStatus.FAILED.name(), e.getMessage());
            return null;
        } catch (IOException e) { //openConnection
            Log.e(TAG, "doInBackground: URL connection error", e);
            publishProgress(WebServiceStatus.FAILED.name(), e.getMessage());
            return null;
        } catch (Exception ignore) {
            Log.e(TAG, "doInBackground: ", ignore);
            return null;
        }

        try {
            //SetHeaders
            setGivenHeaders(urlConnection);
            //POST REQUEST
            if (req.getHttpMethod() == HttpMethod.POST) {
                urlConnection.setRequestMethod("POST"); //throws protocol exception
                prepareRequestForPOST(urlConnection); //throws IOException
            }
            //GET REQUEST or get res from post request "execute"
            //InputStream inputStream = urlConnection.getInputStream();
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            publishProgress(WebServiceStatus.SUCCESS.name(), convertResponseToString(inputStream, urlConnection));

        } catch (ProtocolException e) {
            Log.e(TAG, "doInBackground: setting method error", e);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: request error", e);
            String msg = fetchErrorMessage(urlConnection);
            publishProgress(WebServiceStatus.FAILED.name(), msg);
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
        }finally {
            urlConnection.disconnect();
        }
        return null;
    }

    private String fetchErrorMessage(HttpURLConnection urlConnection) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
        StringBuilder sb = new StringBuilder("Error:\n");
        String resBody;
        try {
            while ((resBody = bufferedReader.readLine()) != null) { //readLine throws
                sb.append(resBody);
            }
        } catch (Exception e) {
            Log.e(TAG, "doInBackgrounds error in extracting err msg: ", e);
            return "something went wrong!";
        }
        return sb.toString();
    }

    private void setGivenHeaders(HttpURLConnection urlConnection) {
        for (Map.Entry<String, String> entry : req.getHeaders().entrySet()) {
            urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private void prepareRequestForPOST(HttpURLConnection urlConnection) throws IOException {
        urlConnection.setDoOutput(true);
        //for best performance is to set body size
//        urlConnection.setFixedLengthStreamingMode(length);
        //if body size unknown then set this otherwise the whole body will be buffered into memory
        urlConnection.setChunkedStreamingMode(0);

        //Convert params to byte and make request
        byte[] byteData = req.getBody().getBytes();
        OutputStream os = urlConnection.getOutputStream();
        os.write(byteData, 0, byteData.length);
    }

    private String convertResponseToString(InputStream inputStream, HttpURLConnection urlConnection) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("Response:\nStatusCode: ");
        sb.append(urlConnection.getResponseCode());

        sb.append("\n------------------------------------\nHeaders: \n");
        for (Map.Entry<String, List<String>> header : urlConnection.getHeaderFields().entrySet()) {
            sb.append(header.getKey());
            sb.append(" : ");
            sb.append(header.getValue().get(0));
            sb.append("\n");
        }

        //convert inputStream data to string and fetch the data "body"
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        sb.append("------------------------------------\nbody: ");
        String resBody;
        while ((resBody = bufferedReader.readLine()) != null) { //readLine throws
            sb.append(resBody);
        }
        return sb.toString();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if (WebServiceStatus.SUCCESS.name().equals(values[0]))
            callback.onSuccess(values[1]);
        else
            callback.onFailure(values[1]);
    }

    public interface WebServiceCallback {
        void onSuccess(String resStr);

        void onFailure(String result);
    }
}