package com.momagic;


import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class RestClient {

    public static final String BASE_URL = "https://aevents.izooto.com/app.php";
    private static final int TIMEOUT = 120000;
    public static final int GET_TIMEOUT = 60000;
    public static final String EVENT_URL="https://et.izooto.com/evt";
    public static final String PROPERTIES_URL="https://prp.izooto.com/prp";
    public static final String IMPRESSION_URL="https://impr.izooto.com/imp";
    public static  final String NOTIFICATIONCLICK="https://clk.izooto.com/clk";
    public static final String SUBSCRIPTION_API="https://usub.izooto.com/sunsub";
    public static final String LASTNOTIFICATIONCLICKURL="https://lci.izooto.com/lci";
    public static final String LASTNOTIFICATIONVIEWURL="https://lim.izooto.com/lim";
    public static final String LASTVISITURL="https://lvi.izooto.com/lvi";
    public static final String MEDIATION_IMPRESSION="https://med.dtblt.com/medi";
    public static final String MEDIATION_CLICKS="https://med.dtblt.com/medc";
    public static final String SUBSCRIBER_URL="https://pp.izooto.com/idsyn";

    // add new url from momagic side

    public static final String MOMAGIC_SUBSCRIPTION_URL="https://irctc.truenotify.in/momagicflow/appenp";
    public  static final String MOMAGIC_USER_PROPERTY="https://irctc.truenotify.in/momagicflow/appup";
    public static final String MOMAGIC_CLICK="https://irctc.truenotify.in/momagicflow/appclk";
    private static int getThreadTimeout(int timeout) {
        return timeout + 5000;
    }


    static void get(final String url, final ResponseHandler responseHandler) {
        new Thread(new Runnable() {
            public void run() {
                makeApiCall(url, null, null, responseHandler, GET_TIMEOUT);
            }
        }).start();
    }
    static void getRequest(final String url, final int timeOut,final ResponseHandler responseHandler) {
        new Thread(new Runnable() {
            public void run() {
                if(timeOut==0)
                makeApiCall(url, null, null, responseHandler, GET_TIMEOUT);
                else
                    makeApiCall(url, null, null, responseHandler,timeOut);
            }
        }).start();
    }

    static void postRequest(final String url,final ResponseHandler responseHandler)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                makeApiCall(url,AppConstant.POST,null,responseHandler,GET_TIMEOUT);
            }
        }).start();
    }
    static void postRequest1(final String url, final JSONObject jsonObject, final ResponseHandler responseHandler)

    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                makeApiCall(url,AppConstant.POST,null,responseHandler,GET_TIMEOUT);
            }
        }).start();
    }
    static void newPostRequest(final String url, final Map<String,String> data, final ResponseHandler responseHandler) {
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
//                httpPostRequest(url, data, responseHandler);
                makeApiCall(url, AppConstant.POST, data, responseHandler, GET_TIMEOUT);
            }
        }).start();
    }
    public static void makeApiCall(final String url, final String method, final Map<String,String> data, final ResponseHandler responseHandler, final int timeout) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                startHTTPConnection(url, method, data, responseHandler, timeout);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void  httpPostRequest(String _url, Map<String,String> data,ResponseHandler responseHandler)
    {
        URLConnection con=null;
        try {
            URL url = new URL( _url );
             con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            StringJoiner sj = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                sj = new StringJoiner("&");
                for(Map.Entry<String,String> entry : data.entrySet())
                    sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + entry.getValue());
            }


            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type",AppConstant.FORM_URL_ENCODED);
            http.setRequestProperty( "charset", "utf-8");
            http.setRequestProperty( "Content-Length", Integer.toString( length ));
            http.setInstanceFollowRedirects( false );
            http.setUseCaches( false );
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(out);
            }
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
//
                if (responseHandler != null) {
                    callResponseHandlerOnSuccess(responseHandler, "Success"+http.getResponseCode());
                }
                else
                    Lg.w(AppConstant.APP_NAME_TAG, AppConstant.ATTACHREQUEST);
               // }
            } else {
                if (responseHandler != null) {
                    callResponseHandlerOnFailure(responseHandler, http.getResponseCode(),null,null);
                }

            }
        }catch (IOException e) {
            if (responseHandler != null) {
                callResponseHandlerOnFailure(responseHandler, 400,null,null);

            }
            e.printStackTrace();
        }
        finally {
            if (con != null)
                ((HttpURLConnection) con).disconnect();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void startHTTPConnection(String url, String method, Map<String,String> data, ResponseHandler responseHandler, int timeout) {
        HttpURLConnection con = null;
        int httpResponse = -1;
        String json = null;
        StringJoiner sj = null;
        try {
            if (data != null) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    sj = new StringJoiner("&");
                    for (Map.Entry<String, String> entry : data.entrySet())
                        sj.add(URLEncoder.encode(entry.getKey(), AppConstant.UTF) + "=" + entry.getValue());
                }
            }
            if (url.contains(AppConstant.HTTPS) || url.contains(AppConstant.HTTP) || url.contains(AppConstant.IMPR)) {
                con = (HttpURLConnection) new URL(url).openConnection();
            } else {
                con = (HttpURLConnection) new URL(BASE_URL + url).openConnection();
            }
            con.setUseCaches(false);
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            if (method != null) {
                con.setRequestMethod(AppConstant.POST);
                con.setDoOutput(true);
                byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
                int length = out.length;
                con.setFixedLengthStreamingMode(length);
                con.setRequestProperty(AppConstant.CONTENT_TYPE, AppConstant.FORM_URL_ENCODED);
                con.setRequestProperty(AppConstant.CHARSET_, AppConstant.UTF_);
                con.setRequestProperty(AppConstant.CONTENT_L, Integer.toString( length ));
                con.setInstanceFollowRedirects( false );
                con.setUseCaches( false );
                con.connect();
                try(OutputStream os = con.getOutputStream()) {
                    os.write(out);
                }
            }
            httpResponse = con.getResponseCode();
            InputStream inputStream;
            Scanner scanner;
            if (httpResponse == HttpURLConnection.HTTP_OK) {
                if (url.equals(AppConstant.CDN + DATB.mAppId + AppConstant.DAT))
                    Lg.d(AppConstant.APP_NAME_TAG, AppConstant.SUCCESS);
                else
                    Lg.d(AppConstant.APP_NAME_TAG, AppConstant.SUCCESS);
                inputStream = con.getInputStream();
                scanner = new Scanner(inputStream, AppConstant.UTF);
                json = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                scanner.close();
                if (responseHandler != null) {
                    callResponseHandlerOnSuccess(responseHandler, json);
                }
                else
                    Lg.w(AppConstant.APP_NAME_TAG, AppConstant.ATTACHREQUEST);
            } else {
                if (url.equals(AppConstant.CDN + DATB.mAppId + AppConstant.DAT))
                    Lg.d(AppConstant.APP_NAME_TAG, AppConstant.SUCCESS);
                else
                    Lg.d(AppConstant.APP_NAME_TAG, AppConstant.FAILURE);
                inputStream = con.getErrorStream();
                if (inputStream == null)
                    inputStream = con.getInputStream();
                if (inputStream != null) {
                    scanner = new Scanner(inputStream, AppConstant.UTF);
                    json = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                    scanner.close();
                }
                if (responseHandler != null) {
                    callResponseHandlerOnFailure(responseHandler, httpResponse, json, null);
                }
                else
                    Lg.w(AppConstant.APP_NAME_TAG, AppConstant.ATTACHREQUEST);
            }
        } catch (Throwable t) {
            if (t instanceof java.net.ConnectException || t instanceof java.net.UnknownHostException)
                Lg.i(AppConstant.APP_NAME_TAG,  AppConstant.EXCEPTIONERROR+ t.getClass().getName());
            else
                Lg.i(AppConstant.APP_NAME_TAG,  AppConstant.EXCEPTIONERROR+ t);
            if (responseHandler != null)
                callResponseHandlerOnFailure(responseHandler, httpResponse, null, t);
            else
                Lg.w(AppConstant.APP_NAME_TAG, AppConstant.ATTACHREQUEST);
        } finally {
            if (con != null)
                con.disconnect();
        }
    }

    private static void callResponseHandlerOnSuccess(final ResponseHandler handler, final String response) {
        new AppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                handler.onSuccess(response);
            }
        });

    }

    private static void callResponseHandlerOnFailure(final ResponseHandler handler, final int statusCode, final String response, final Throwable throwable) {
        new AppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                handler.onFailure(statusCode, response, throwable);
            }
        });
    }

    static class ResponseHandler {
        void onSuccess(String response) {
            Lg.d(AppConstant.APP_NAME_TAG,  AppConstant.APISUCESS);
        }

        void onFailure(int statusCode, String response, Throwable throwable) {
            Lg.v(AppConstant.APP_NAME_TAG, AppConstant.APIFAILURE  + response);
        }
    }
}