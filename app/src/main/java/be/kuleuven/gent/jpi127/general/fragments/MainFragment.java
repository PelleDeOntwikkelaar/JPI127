package be.kuleuven.gent.jpi127.general.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.support.Station;
import be.kuleuven.gent.jpi127.support.VolleyResponseListener;

public class MainFragment extends Fragment implements VolleyResponseListener {
    private static final String TAG = "MainFragment";

    SharedPreferences sharedPref;
    String baseUrl;
    String url;

    private AutoCompleteTextView autoCompleteTextView;
    private Button searchButton;
    private ArrayList<Station> stations;

    String[] nameList;

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
        return inflater.inflate(R.layout.fragment_main,null);
    }



    /**
     * Second method called on start up.
     * Initiates the variables.
     * Links the UI-elements.
     * Retrieves the needed elements from the shared preferences.
     * Calls other methods needed to perform all fragment actions.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        sharedPref = getActivity().getSharedPreferences("myPrefs",Context.MODE_PRIVATE);

        baseUrl=sharedPref.getString("url","http://192.168.0.178:8080/rail4you");
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("/stations");
        url=urlBuilder.toString();

        //UI components
        autoCompleteTextView= (AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView_input_station_name);
        searchButton=(Button) view.findViewById(R.id.button_search);

        //variable declaration
        stations=new ArrayList<>();

        //load additional info
        loadStations();

        //Link Button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch(findSelectedStation());
            }
        });

    }


    private void loadStations() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        requestStarted();

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

    private Station findSelectedStation(){
        String name = autoCompleteTextView.getText().toString();
        if(!name.equals("Stations")){
            for(Station station:stations){
                if(station.getName().equals(name)) return station;
            }
        }
        return null;

    }

    private void performSearch(Station selectedStation) {
        if(selectedStation!=null){
            FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
            Fragment fragment=new MainFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_area,fragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void requestStarted() {
        Log.d(TAG, "requestStarted: load stations started at url: " + url);
    }

    @Override
    public void requestCompleted(String response) {
        JSONArray jsonArray = null;
        ArrayList<String> names=new ArrayList<>();
        try {
            jsonArray = new JSONArray(response);
            for (int i =0;i<jsonArray.length();i++){
                JSONObject o = jsonArray.getJSONObject(i);
                Station station= new Station(o);
                names.add(station.getName());
                stations.add(station);
            }

            nameList= names.toArray(new String[0]);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, nameList);
            autoCompleteTextView.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void requestEndedWithError(VolleyError error) {

    }

}
