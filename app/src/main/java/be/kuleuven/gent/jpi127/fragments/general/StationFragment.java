package be.kuleuven.gent.jpi127.fragments.general;

//import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.model.Station;
import be.kuleuven.gent.jpi127.model.StationData;
import be.kuleuven.gent.jpi127.model.Train;
import be.kuleuven.gent.jpi127.model.User;
import be.kuleuven.gent.jpi127.support.TrainAdapter;
import be.kuleuven.gent.jpi127.support.VolleyResponseListener;


public class StationFragment extends Fragment implements VolleyResponseListener {

    private static final String TAG = "StationFragment";

    private SharedPreferences sharedPref;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Context context;
    private Station station;
    private ArrayList<Train>trains;
    private String baseUrl;
    private String url;
    private boolean tracked;
    private boolean isFavorit;


    private TextView stationTitleTextView;
    private TextView currentDelayTextView;
    private TextView commonDelayTextView;
    private TextView piekTextView;
    private TextView dalTextView;
    private Button favoritButton;
    private Switch trackSwitch;

    private int requestNumber;

    private User user;



    public void setCredentials(Station station){
        this.station=station;
    }

    /**
     * Method called on start up, the link with the user interface happens here.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_station,null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        sharedPref = getActivity().getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        StringBuilder urlBuilder = new StringBuilder();

        baseUrl=sharedPref.getString("url","http://192.168.0.178:8080/rail4you/");
        urlBuilder.append(baseUrl);
        url=urlBuilder.toString();

        recyclerView = (RecyclerView) view.findViewById(R.id.trains_recyclerview);
        recyclerView.setHasFixedSize(true);
        context = this.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        stationTitleTextView= (TextView) view.findViewById(R.id.textViewStationName);
        currentDelayTextView= (TextView) view.findViewById(R.id.textViewCurrentDelay);
        commonDelayTextView = (TextView) view.findViewById(R.id.textViewComDelay);
        piekTextView= (TextView) view.findViewById(R.id.textViewPiek);
        dalTextView = (TextView) view.findViewById(R.id.textViewDal);
        favoritButton =(Button) view.findViewById(R.id.button_favorit);
        trackSwitch = (Switch) view.findViewById(R.id.switch_track);

        Gson gson = new Gson();
        String json = sharedPref.getString("user", "");
        user = gson.fromJson(json, User.class);

        trains= new ArrayList<>();

        stationTitleTextView.setText(station.getName());

        requestNumber=0;
        performThirdPartySearh();

        trackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    addToTracked();
                }
            }
        });
        favoritButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFavorit && checkForUser()){
                    addToFavorit();
                    favoritButton.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void addToFavorit() {
        requestNumber=3;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("/addToFavorites");
        urlBuilder.append("?stationCode=");
        urlBuilder.append(station.getUri());
        urlBuilder.append("&userId=");
        urlBuilder.append(user.getId());
        Log.d(TAG, "addToFavorit: url" +urlBuilder.toString());
        requestStarted();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlBuilder.toString(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestCompleted(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private boolean checkForUser() {
        boolean out=sharedPref.contains("user");
        if(out){
            Gson gson= new Gson();
            String userString=sharedPref.getString("user","nope");
            user=gson.fromJson(userString,User.class);

        }
        return out;
    }

    private void addToTracked() {
        requestNumber=4;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("/addToTracked");
        urlBuilder.append("?stationID=");
        urlBuilder.append(station.getUri());
        requestStarted();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlBuilder.toString(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestCompleted(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void performThirdPartySearh() {



        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("/getApiData");
        urlBuilder.append("?stationCode=");
        urlBuilder.append(station.getUri());
        requestStarted();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlBuilder.toString(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestCompleted(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);


    }

    private void getTrackedData() {
        requestNumber=1;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("/getData");
        urlBuilder.append("?stationCode=");
        urlBuilder.append(station.getUri());
        requestStarted();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlBuilder.toString(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestCompleted(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void loadFavorite() {
        if(!checkForUser()){
            favoritButton.setVisibility(View.INVISIBLE);
        }else{
            requestNumber=2;
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(baseUrl);
            urlBuilder.append("/isFavorit");
            urlBuilder.append("?stationCode=");
            urlBuilder.append(station.getUri());
            urlBuilder.append("&userId=");
            urlBuilder.append(user.getId());
            Log.d(TAG, "loadFavorite: url" + urlBuilder.toString());
            requestStarted();
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    urlBuilder.toString(),
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            requestCompleted(response);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }

    }



    @Override
    public void requestStarted() {
        Log.d(TAG, "requestStarted: url: " + url);

    }

    @Override
    public void requestCompleted(String response) {

        if(requestNumber==2){
            if(response.equals("TRUE"))isFavorit=true;
            else isFavorit=false;
            if(isFavorit){
                favoritButton.setVisibility(View.INVISIBLE);
            }

        } else if(requestNumber==1){
            try {
                Log.d(TAG, "requestCompleted: response: " + response);
                JSONArray jsonArray=new JSONArray(response);
                ArrayList<StationData> datalist = new ArrayList<>();
                if(jsonArray.length()>0){
                    tracked=true;
                    for(int i =0;i<jsonArray.length();i++){
                        JSONObject obj=jsonArray.getJSONObject(i);
                        StationData data=new StationData(obj);
                        datalist.add(data);
                    }
                    commonDelayTextView.setText("Average delay: " + datalist.get(0).getCommDelay() + "[min/day]");
                    piekTextView.setText("Max: " + datalist.get(0).getPiek() + "[min/day]");
                    dalTextView.setText("Min:" + datalist.get(0).getDal() + "[min/day]");
                }else{
                    tracked=false;
                    commonDelayTextView.setText("Average delay: not tracked");
                    piekTextView.setText("Max: not tracked");
                    dalTextView.setText("Min: not tracked");
                }
                trackSwitch.setChecked(tracked);


                loadFavorite();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if(requestNumber==0){
            try {
                Log.d(TAG, "requestCompleted: response: " + response);
                JSONObject inputResponse= new JSONObject(response);
                JSONArray jsonArray=inputResponse.getJSONArray("@graph");
                for(int i =0;i<jsonArray.length();i++){
                    JSONObject obj=jsonArray.getJSONObject(i);
                    Train train=new Train(obj);
                    trains.add(train);
                    int newDelay=station.getTotalDelay();
                    newDelay+=Integer.parseInt(train.getDelay());
                    station.setTotalDelay(newDelay);
                }
                currentDelayTextView.setText("Current delay: " + station.getTotalDelay()/60 + "minutes");
                adapter = new TrainAdapter(trains,context,getActivity().getSupportFragmentManager());
                recyclerView.setAdapter(adapter);

                getTrackedData();

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
