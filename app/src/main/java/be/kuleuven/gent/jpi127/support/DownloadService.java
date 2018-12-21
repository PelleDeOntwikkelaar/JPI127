package be.kuleuven.gent.jpi127.support;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.kuleuven.gent.jpi127.model.Station;

public class DownloadService extends IntentService implements VolleyResponseListener{

    private static final String TAG = "DownloadService";

    private int result = Activity.RESULT_CANCELED;

    public static final String NOTIFICATION = "be.kuleuven.gent.jpi127.support";

    public DownloadService() {
        super("DownloadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        String urlPath = intent.getStringExtra("urlpath");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlPath,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        requestCompleted(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }

    private void publishResults(ArrayList<String> names ) {

        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra("result", names);
        sendBroadcast(intent);
    }

    @Override
    public void requestStarted() {
        Log.d(TAG, "requestStarted: ");

    }

    @Override
    public void requestCompleted(String response) {
        JSONArray jsonArray = null;
        ArrayList<String> names=new ArrayList<>();
        try {
            jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i);
                Station station = new Station(o);
                names.add(station.getName());
            }
        }catch (JSONException e) {
                e.printStackTrace();
        }


        publishResults(names);

    }

        @Override
    public void requestEndedWithError(VolleyError error) {

    }
}