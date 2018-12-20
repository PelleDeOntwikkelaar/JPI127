package be.kuleuven.gent.jpi127.fragments.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import be.kuleuven.gent.jpi127.MainActivity;
import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.fragments.general.MainFragment;
import be.kuleuven.gent.jpi127.support.Encryptie;
import be.kuleuven.gent.jpi127.model.Token;
import be.kuleuven.gent.jpi127.model.User;
import be.kuleuven.gent.jpi127.support.VolleyResponseListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Fragment called when a user tries to login.
 *
 * @author Pelle Reyniers
 */
public class LoginFragment extends Fragment implements VolleyResponseListener {
    private static final String TAG = "LoginFragment";


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private ProgressDialog progressDialog;
    private View mLoginFormView;
    private LoginButton loginButton;

    private String baseUrl;
    private String url;

    private SharedPreferences sharedPref;

    private View view;

    private CallbackManager callbackManager;

    private ImageView imageView;

    private MainActivity mainActivity;

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
        mainActivity = (MainActivity)getActivity();
        callbackManager=mainActivity.getCallbackManager();
        return inflater.inflate(R.layout.fragment_login, null);
    }



    /**
     * Second method called on start up.
     * Initiates variables and ui elements.
     * Retrieves values from the shared preferences.
     * Adds the on click listener.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.email);

        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();
            }
        });

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);

        sharedPref = getActivity().getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        StringBuilder urlBuilder = new StringBuilder();
        baseUrl=sharedPref.getString("url","http://192.168.0.178:8080/rail4you/");
        urlBuilder.append(baseUrl);
        urlBuilder.append("login");
        url=urlBuilder.toString();

        Log.d(TAG, "onViewCreated: url" + url);


        this.view = view;
        imageView=(ImageView)view.findViewById(R.id.imageView);

        loginButton=(LoginButton)view.findViewById(R.id.login_button_facebook);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                progressDialog=new ProgressDialog(getContext());
                progressDialog.setMessage("Retreiving data...");
                progressDialog.show();

                String accesToken = loginResult.getAccessToken().getToken();
                Log.d(TAG, "onSuccess: accestoken" + accesToken);
                java.util.Date utilDate= new java.util.Date();
                final Token token = new Token(accesToken, new java.sql.Date(utilDate.getTime()));

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        progressDialog.dismiss();
                        getFacebookData(object, token);

                    }
                });

                request.executeAsync();


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        if(AccessToken.getCurrentAccessToken()!= null){

        }


    }


    private void getFacebookData(JSONObject object, Token token) {
        try {
            /*
            URL profile_picture= new URL("https://graph.facebook.com/"+object.getString("id")+ "/picture?width=250&height=250");

            Bitmap bmp = BitmapFactory.decodeStream(profile_picture.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
            */

            User user = new User(Long.parseLong(object.getString("id")),object.getString("name"),token);
            commitUser(user);


            changeLayout(true);

            facebookLoginToServer(user);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void facebookLoginToServer(User user) {
        StringBuilder urlBuilder= new StringBuilder();
        urlBuilder.append(baseUrl);
        urlBuilder.append("/loginFacebook?name=");
        urlBuilder.append(deleteSpaces(user.getName()));
        urlBuilder.append("&id=");
        urlBuilder.append(user.getId());
        String url=urlBuilder.toString();
        requestStarted();
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlBuilder.toString(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        requestCompleted(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
                        //Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private String deleteSpaces(String name) {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<name.length();i++ ){
            if(name.charAt(i)!=' '){
                stringBuilder.append(name.charAt(i));
            }

        }
        return stringBuilder.toString();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.d(TAG, "attemptLogin: cancel is false");
            showProgress(true);
            StringBuilder userCredentialsBuilder = new StringBuilder();
            userCredentialsBuilder.append(email);
            userCredentialsBuilder.append(":");
            try {
                userCredentialsBuilder.append(Encryptie.encodeSHA256(password));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            String userCredentials = userCredentialsBuilder.toString();

            Log.d(TAG, "attemptLogin: userCredentials: " + userCredentials);

            networkLogin(userCredentials);

        }
    }

    /**
     * Method responsible for a fragment switch when the login is completed.
     * @param succes
     */
    private void changeLayout(boolean succes) {
        showProgress(false);
        if(succes){
            Toast.makeText(getContext(),"Login succesvol", Toast.LENGTH_LONG);
            FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
            Fragment fragment=new MainFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen_area,fragment);
            fragmentTransaction.commit();
        }else{
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            mPasswordView.requestFocus();
        }
    }

    /**
     * Method that performs a network request and is responsible for the login functionality.
     * @param userCredentials
     */
    private void networkLogin(final String userCredentials) {
        final String userCredentials1 = userCredentials;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                stringBuilder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.has("id")){
                                    long id = jsonObject.getLong("id");
                                    String name = jsonObject.getString("Name");
                                    String email = jsonObject.getString("email");
                                    User user = new User(id,name,email);
                                    commitUser(user);

                                    //get token from server
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(url);
                                    stringBuilder.append("/token");
                                    if (sharedPref.contains("user")){
                                        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                                                stringBuilder.toString(),
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            if(jsonObject.has("token")){
                                                                //retracting parameters from JSon object.
                                                                String login = jsonObject.getString("loginName");
                                                                String tokenString = jsonObject.getString("token");
                                                                String dateString = jsonObject.getString("date");
                                                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                                                java.util.Date parsed = null;
                                                                try {
                                                                    parsed = sdf.parse(dateString);
                                                                } catch (ParseException e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                                Date dateSql = new Date(parsed.getTime());
                                                                //declare a token object
                                                                Token tokenObj = new Token(tokenString,dateSql);
                                                                //get the User from sharedPreferences
                                                                Gson gson = new Gson();
                                                                String jsonUser = sharedPref.getString("user", "");
                                                                User user = gson.fromJson(jsonUser, User.class);
                                                                //assign token
                                                                user.setToken(tokenObj);
                                                                //user back to JSon
                                                                commitUser(user);

                                                            }else{
                                                                Log.d(TAG, "onResponse: token object not valid");
                                                            }


                                                        } catch (JSONException e) {
                                                            Log.d(TAG, "onResponse: JSON Exception in token getter");
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError volleyError) {
                                                        showProgress(false);
                                                        Toast.makeText(getContext(),volleyError.getMessage(),Toast.LENGTH_LONG).show();
                                                    }
                                                }){
                                            @Override
                                            public Map<String, String> getHeaders() throws AuthFailureError {
                                                Map<String, String> headers = new HashMap<String, String>();
                                                headers.put("UserCredentials", userCredentials1);
                                                return headers;
                                            }
                                        };

                                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                                        requestQueue.add(stringRequest);

                                    }
                                    changeLayout(true);


                                }else{

                                    Toast.makeText(getContext(),response.toString(), Toast.LENGTH_LONG).show();
                                }


                        } catch (JSONException e) {
                            Toast.makeText(getContext(),"Oeps foutje " + e, Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showProgress(false);
                        Toast.makeText(getContext(),volleyError.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("UserCredentials", userCredentials1);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private void commitUser(User user){
        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user",json);
        editor.commit();
    }

    @Override
    public void requestStarted() {
        Log.d(TAG, "requestStarted: ");
    }

    @Override
    public void requestCompleted(String response) {
        Log.d(TAG, "requestCompleted: ");
    }

    @Override
    public void requestEndedWithError(VolleyError error) {
        Log.d(TAG, "requestEndedWithError: ");
    }
}
