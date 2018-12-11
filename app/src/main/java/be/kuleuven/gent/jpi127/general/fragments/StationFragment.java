package be.kuleuven.gent.jpi127.general.fragments;

//import com.android.volley.Response;
import com.android.volley.VolleyError;

        import android.app.ProgressDialog;
import android.content.Context;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

        import java.util.ArrayList;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.model.Station;
import be.kuleuven.gent.jpi127.model.Train;
import be.kuleuven.gent.jpi127.model.WResponse;
import be.kuleuven.gent.jpi127.support.IRailApi;
import be.kuleuven.gent.jpi127.support.NetworkClientIRail;
import be.kuleuven.gent.jpi127.support.TrainAdapter;
import be.kuleuven.gent.jpi127.support.VolleyResponseListener;
        import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;
//import okhttp3.Request;
//import okhttp3.Response;

public class StationFragment extends Fragment implements VolleyResponseListener {

    private static final String TAG = "StationFragment";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Context context;
    private Station station;
    private ArrayList<Train>trains;



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
        recyclerView = (RecyclerView) view.findViewById(R.id.trains_recyclerview);
        recyclerView.setHasFixedSize(true);
        context = this.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        trains= new ArrayList<>();

        performThirdPartySearh();
    }

    private void performThirdPartySearh() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading data...");
        progressDialog.show();
        requestStarted();

        //Obtain an instance of Retrofit by calling the static method.
        Retrofit retrofit = NetworkClientIRail.getRetrofitClient();

        /*
        The main purpose of Retrofit is to create HTTP calls from the Java interface based on the annotation associated with each method. This is achieved by just passing the interface class as parameter to the create method
        */
        IRailApi iRailApi = retrofit.create(IRailApi.class);
        /*
        Invoke the method corresponding to the HTTP request which will return a Call object. This Call object will used to send the actual network request with the specified parameters
        */
        Call call = iRailApi.getStationDatils(alternativeURLBuilder(station.getUri()));
        /*
        This is the line which actually sends a network request. Calling enqueue() executes a call asynchronously. It has two callback listeners which will invoked on the main thread
        */
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                /*This is the success callback. Though the response type is JSON, with Retrofit we get the response in the form of WResponse POJO class
                 */
                progressDialog.dismiss();
                if (response.body() != null) {
                    WResponse wResponse = (WResponse) response.body();
                    Log.d(TAG, "onResponse: "+wResponse.getTrainString());

                }
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                progressDialog.dismiss();
                Log.d(TAG, "onFailure: ");
                /*
                Error callback
                */
            }
        });




        /*
        //String url = alternativeURLBuilder(station.getUri());


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, station.getUri(), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: "+response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();


                    }
                });



        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
        */

    }




    private String alternativeURLBuilder(String uri) {
        int count=0;
        String code=null;
        for(int i=0;i<uri.length();i++){
            if(uri.charAt(i)=='/') count++;
            if(count==5) {
                code=uri.substring(i+1);
                Log.d(TAG, "alternativeURLBuilder: " + code);
                return code;

            }
        }
        return null;
    }




    @Override
    public void requestStarted() {
        Log.d(TAG, "requestStarted: url: " + station.getUri());

    }

    @Override
    public void requestCompleted(String response) {

        try {
            Log.d(TAG, "requestCompleted: response: " + response);
            JSONObject inputResponse= new JSONObject(response);
            JSONArray jsonArray=inputResponse.getJSONArray("@graph");
            for(int i =0;i<jsonArray.length();i++){
                JSONObject obj=jsonArray.getJSONObject(i);
                trains.add(new Train(obj));
            }
            adapter = new TrainAdapter(trains,context,getActivity().getSupportFragmentManager());
            recyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void requestEndedWithError(VolleyError error) {
        Log.d(TAG, "requestEndedWithError: " + error);
    }
}
