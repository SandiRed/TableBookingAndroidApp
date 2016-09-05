package com.quad.booking.tablebooking.network;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.quad.booking.tablebooking.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class NetworkHandler {
    private final String TAG = "NetworkHandler";
    private RequestQueue mRequestQueue;

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
                /*HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify(ApiConstants.AUTHORITY, session);*/
            }
        };
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkClientTrusted(certs, authType);
                        } catch (CertificateException ignored) {
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkServerTrusted(certs, authType);
                        } catch (CertificateException ignored) {
                        }
                    }
                }
        };
    }

    private SSLSocketFactory getSSLSocketFactory(Context context)
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(R.raw.androiddebug);
        //KeyStore keyStore = KeyStore.getInstance("BKS");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        try {
            Certificate ca = cf.generateCertificate(caInput);
            keyStore.setCertificateEntry("ca", ca);
        } finally {
            caInput.close();
        }

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);

        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);
        return sslContext.getSocketFactory();
    }

    private RequestQueue getRequestQueue(final Context applicationContext) {
        if (mRequestQueue == null) {
            HurlStack hurlStack = new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    Log.d(TAG, "createConnection for " + url);
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection)
                            super.createConnection(url);
                    return httpsURLConnection;
                }
            };
            mRequestQueue = Volley.newRequestQueue(applicationContext, hurlStack);
        }
        return mRequestQueue;
    }

    private <T> void addToRequestQueue(Request<T> req, String tag, Context applicationContext) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue(applicationContext).add(req);
    }

    private <T> void addToRequestQueue(Request<T> req, Context applicationContext) {
        req.setTag(TAG);
        getRequestQueue(applicationContext).add(req);
    }

    private void handleErrorResponse(String jsonString, NetworkResult networkResult, Context context) {
        //Note: Can have custom responses based on error code; for eg 401 can have a different error response string compared to status code 400.
        Log.d("trace", " handleErrorResponse: " + jsonString);
        String message = jsonString;
        JSONObject response = null;
               // message = (jsonString);
        networkResult.setFailureResultData(message);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    @NonNull
    private HashMap<String, String> setHeaders(HashMap<String, String> headers, Context applicationContext) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (headers != null)
            map = new HashMap<String, String>(headers);
        map.put("Content-Type", "application/json");
        return map;
    }


    public void makeGetRequestForJsonArray(final String url, final String cancelTag, final Context context,
                                           final int repeatCount, final NetworkResult networkResult) {
        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, url + "******************Success Response for GET*********************" + response.toString());
                        networkResult.setSuccessResultData(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (checkAuthError(error, url, networkResult, context)) {
                            Log.d(TAG, "-----Authorization Error---------");
                            handleErrorResponse("401",networkResult,context );
                        } else {
                            if (error.getMessage() != null) {
                                if (error.networkResponse != null)
                                    Log.d(TAG, "Failure Response for GET: " + error.networkResponse.statusCode + ", Error Response--->" + error.getMessage() + ", " + new String(error.networkResponse.data));
                                else
                                    Log.d(TAG, "Failure Response for GET: " + error.getMessage() + ", ");
                            }
                            if (repeatCount > 0) {
                                int repeat = repeatCount - 1;
                                makeGetRequestForJsonArray(url, cancelTag, context, repeat, networkResult);
                            } else {
                                String message;
                                if (error.networkResponse == null) {
                                    message = context.getResources().getString(R.string.connect_device_to_network);
                                } else {
                                    String errorMsg;
                                    if (error.getMessage() != null)
                                        errorMsg = error.getMessage();
                                    message = new String(error.networkResponse.data);
                                }
                                handleErrorResponse(message, networkResult, context);
                                Log.d(TAG, url + "<--url,Error Response of the GET Request--->" + error.getMessage() + ", " + message);
                            }
                        }
                    }
                }
        );
        addToRequestQueue(getRequest, cancelTag, context);
    }

    private boolean checkAuthError(VolleyError error, String url, NetworkResult networkResult, Context context) {
        if (error instanceof com.android.volley.AuthFailureError) {
            handleErrorResponse("{\"StatusCode:\":401}", networkResult, context);
        }
        return true;
    }


    public interface NetworkResult {
        void setSuccessResultData(String message);

        void setFailureResultData(String message);
    }

}