package be.kuleuven.gent.jpi127.general.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import java.util.ArrayList;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.model.Station;
import be.kuleuven.gent.jpi127.support.StationAdapter;
import be.kuleuven.gent.jpi127.support.TrainAdapter;
import be.kuleuven.gent.jpi127.support.VolleyResponseListener;

public class FavoritesFragment  extends Fragment implements VolleyResponseListener {

    private static final String TAG = "FavoritesFragment";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Context context;
    private ArrayList<Station>stations;
    private SharedPreferences sharedPref;
    private String baseUrl;
    private String url;
    private ArrayList<String>stationCodes;
    private int volleyNumber;
    final ProgressDialog progressDialog= new ProgressDialog(getContext());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        sharedPref = getActivity().getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("lastUsedFragment",R.id.FavoritesMI);
        editor.commit();
        StringBuilder urlBuilder = new StringBuilder();

        baseUrl=sharedPref.getString("url","http://192.168.0.178:8080/rail4you/");
        urlBuilder.append(baseUrl);
        urlBuilder.append("/getFavorites");
        url=urlBuilder.toString();

        recyclerView = (RecyclerView) view.findViewById(R.id.favorites_recyclerview);
        recyclerView.setHasFixedSize(true);
        context = this.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        stations= new ArrayList<>();
        stationCodes=new ArrayList<>();
        loadRecyclerView();


    }

    private void loadRecyclerView() {
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        requestStarted();
        volleyNumber=1;

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        requestCompleted(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),volleyError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void loadStations() {
        volleyNumber=2;
        url = baseUrl.concat("/getStations");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        requestCompleted(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),volleyError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }

    @Override
    public void requestStarted() {
        Log.d(TAG, "requestStarted: url: " + url);

    }

    @Override
    public void requestCompleted(String response) {
        Log.d(TAG, "requestCompleted: url: " + url);
        //todo: afhandelen van de JSON logica en aanspreken van de adapter;

        if(volleyNumber==1){
            try {
                JSONArray jsonArray=new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    stationCodes.add(jsonObject.getString("station_uri"));
                }
                loadStations();
                volleyNumber=0;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (volleyNumber==2){
            try {
                JSONArray jsonArray=new JSONArray(response);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject=jsonArray.getJSONObject(i);
                    stations.add(new Station(jsonObject));
                }
                adapter = new StationAdapter(stations,context,getActivity().getSupportFragmentManager());
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



    }


    @Override
    public void requestEndedWithError(VolleyError error) {
        Log.d(TAG, "requestEndedWithError: " + error);
    }
}
