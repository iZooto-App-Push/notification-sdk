package com.momagic;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestClient {
    static final String BASE_URL = "https://aevents.izooto.com/app";
    static final int GET_TIMEOUT = 60000;
    static String GOOGLE_JSON_URL = "https://cdn.izooto.com/app/app_";  //old
    static final String EVENT_URL = "https://et.izooto.com/evt";
    static final String PROPERTIES_URL = "https://prp.izooto.com/prp";
    static final String IMPRESSION_URL = "https://impr.izooto.com/imp";
    static final String NOTIFICATIONCLICK = "https://clk.izooto.com/clk";
    static final String SUBSCRIPTION_API = "https://usub.izooto.com/sunsub";
    static final String LASTNOTIFICATIONCLICKURL = "https://lci.izooto.com/lci";
    static final String LASTNOTIFICATIONVIEWURL = "https://lim.izooto.com/lim";
    static final String LASTVISITURL = "https://lvi.izooto.com/lvi";
    static final String MEDIATION_IMPRESSION = "https://med.dtblt.com/medi";
    static final String MEDIATION_CLICKS = "https://med.dtblt.com/medc";
    static final String SUBSCRIBER_URL = "https://pp.izooto.com/idsyn";
    static final String APP_EXCEPTION_URL = "https://aerr.izooto.com/aerr";
    static final String PERSISTENT_NOTIFICATION_DISMISS_URL = "https://dsp.izooto.com/dsp";
    // add new url from momagic side
    static final String MOMAGIC_SUBSCRIPTION_URL = "https://irctc.truenotify.in/momagicflow/appenp";
    static final String MOMAGIC_USER_PROPERTY = "https://irctc.truenotify.in/momagicflow/appup";
    static final String MOMAGIC_CLICK = "https://irctc.truenotify.in/momagicflow/appclk";
    static final String MOMAGIC_IMPRESSION = "https://irctc.truenotify.in/momagicflow/appimpr";

    private static int getThreadTimeout(int timeout) {
        return timeout + 5000;
    }

    static void get(final String url, final ResponseHandler responseHandler) {
        new Thread(new Runnable() {
            public void run() {
                makeApiCall(url, null, null, null, responseHandler, GET_TIMEOUT);
            }
        }).start();
    }

    static void getRequest(final String url, final int timeOut, final ResponseHandler responseHandler) {
        new Thread(new Runnable() {
            public void run() {
                if (timeOut == 0)
                    makeApiCall(url, null, null, null, responseHandler, GET_TIMEOUT);
                else
                    makeApiCall(url, null, null, null, responseHandler, timeOut);
            }
        }).start();
    }

    static void postRequest(final String url, final Map<String, String> data, JSONObject jsonObject, final ResponseHandler responseHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                makeApiCall(url, AppConstant.POST, data, jsonObject, responseHandler, GET_TIMEOUT);
            }
        }).start();
    }

    public static void makeApiCall(final String url, final String method, final Map<String, String> data, JSONObject jsonObject, final ResponseHandler responseHandler, final int timeout) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(new Runnable() {
            @Override
            public void run() {
                startHTTPConnection(url, method, data, jsonObject, responseHandler, timeout);
            }
        });

    }

    private static void startHTTPConnection(String url, String method, final Map<String, String> data, JSONObject jsonBody, ResponseHandler responseHandler, int timeout) {
        HttpURLConnection con = null;
        int httpResponse = -1;
        String json = null;
        StringBuilder postData = null;
        int retry = 0;
        boolean delay = false;
        do {
            if (delay) {
                Util.sleepTime(2000);
            }
            try {
                if (url.contains(AppConstant.HTTPS) || url.contains(AppConstant.HTTP) || url.contains(AppConstant.IMPR)) {
                    con = (HttpURLConnection) new URL(url).openConnection();
                } else {
                    con = (HttpURLConnection) new URL(BASE_URL + url).openConnection();
                }
                if (data != null) {
                    postData = new StringBuilder();
                    for (Map.Entry<String, String> param : data.entrySet()) {
                        if (postData.length() != 0) {
                            postData.append('&');
                        }
                        postData.append(URLEncoder.encode(param.getKey(), AppConstant.UTF));
                        postData.append('=');
                        postData.append(URLEncoder.encode(param.getValue(), AppConstant.UTF));
                    }
                }
                con.setUseCaches(false);
                con.setConnectTimeout(timeout);
                con.setReadTimeout(timeout);
                if (jsonBody != null) {
                    con.setDoInput(true);
                }
                if (method != null) {
                    if (method.equalsIgnoreCase(AppConstant.POST) && jsonBody == null) {
                        con.setRequestProperty(AppConstant.CONTENT_TYPE, AppConstant.FORM_URL_ENCODED);
                    } else {
                        con.setRequestProperty(AppConstant.CONTENT_TYPE, AppConstant.FORM_URL_JSON);
                    }
                    con.setRequestMethod(method);
                    con.setDoOutput(true);

                }
                if (method != null) {
                    if (postData != null) {
                        byte[] out = postData.toString().getBytes(StandardCharsets.UTF_8);
                        int length = out.length;
                        con.setFixedLengthStreamingMode(length);
                        con.setRequestProperty(AppConstant.CONTENT_TYPE, AppConstant.FORM_URL_ENCODED);
                        con.setRequestProperty(AppConstant.CHARSET_, AppConstant.UTF_);
                        con.setRequestProperty(AppConstant.CONTENT_L, Integer.toString(length));
                        con.setInstanceFollowRedirects(false);
                        con.setUseCaches(false);
                        con.connect();

                        try (OutputStream os = con.getOutputStream()) {
                            os.write(out);
                        }
                    }
                }
                if (jsonBody != null) {
                    String strJsonBody = jsonBody.toString();
                    byte[] sendBytes = strJsonBody.getBytes(AppConstant.UTF);
                    con.setFixedLengthStreamingMode(sendBytes.length);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(sendBytes);
                }

                httpResponse = con.getResponseCode();
                InputStream inputStream;
                Scanner scanner;
                if (httpResponse == HttpURLConnection.HTTP_OK) {
                    DebugFileManager.createExternalStoragePublic(DATB.appContext, "->" + url, "[Log.V]->URL");
                    if (data != null) {
                        DebugFileManager.createExternalStoragePublic(DATB.appContext, "->" + data, "[Log.V]->URL");
                    }
                    if (jsonBody != null) {
                        DebugFileManager.createExternalStoragePublic(DATB.appContext, "->" + jsonBody.toString(), "[Log.V]->URL");
                    }
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
                    } else
                        Lg.w(AppConstant.APP_NAME_TAG, AppConstant.ATTACH_REQUEST);
                } else {
                    retry++;
                    delay = true;
                    if (retry >= 4) {
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
                        } else
                            Lg.w(AppConstant.APP_NAME_TAG, AppConstant.ATTACH_REQUEST);
                    }
                }
            } catch (Throwable t) {
                retry++;
                delay = true;
                if (retry >= 4) {
                    if (t instanceof java.net.ConnectException || t instanceof java.net.UnknownHostException)
                        Lg.i(AppConstant.APP_NAME_TAG, AppConstant.EXCEPTIONERROR + t.getClass().getName());
                    else
                        Lg.i(AppConstant.APP_NAME_TAG, AppConstant.EXCEPTIONERROR + t);
                    if (responseHandler != null)
                        callResponseHandlerOnFailure(responseHandler, httpResponse, null, t);
                    else
                        Lg.w(AppConstant.APP_NAME_TAG, AppConstant.ATTACH_REQUEST);
                }
                Util.handleExceptionOnce(DATB.appContext, t.toString(), "RestClient", "startHTTPConnection");
            } finally {
                if (con != null)
                    con.disconnect();
            }
        } while (retry < 4 && httpResponse != 200);
    }

    private static void callResponseHandlerOnSuccess(final ResponseHandler handler, final String response) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            handler.onSuccess(response);
        });
    }

    private static void callResponseHandlerOnFailure(final ResponseHandler handler, final int statusCode, final String response, final Throwable throwable) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            handler.onFailure(statusCode, response, throwable);
        });
    }

    static class ResponseHandler {
        void onSuccess(String response) {
            Lg.d(AppConstant.APP_NAME_TAG, AppConstant.APISUCESS);
        }

        void onFailure(int statusCode, String response, Throwable throwable) {
            Lg.v(AppConstant.APP_NAME_TAG, AppConstant.APIFAILURE + response);
        }
    }

}