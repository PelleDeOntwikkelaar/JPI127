package be.kuleuven.gent.jpi127.fragments.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.fragments.general.MainFragment;
import be.kuleuven.gent.jpi127.model.User;
import be.kuleuven.gent.jpi127.support.Encryptie;
import be.kuleuven.gent.jpi127.support.VolleyResponseListener;

public class RegisterFragment extends Fragment implements VolleyResponseListener {
    private static final String TAG = "LoginFragment";


    // UI references.
    private EditText mNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordRepeatView;
    private View mProgressView;
    private ProgressDialog progressDialog;
    private View mLoginFormView;
    private LoginButton loginButton;

    private String baseUrl;
    private String url;

    private SharedPreferences sharedPref;

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.register_email);
        mNameView = (EditText) view.findViewById(R.id.register_name);

        mPasswordView = (EditText) view.findViewById(R.id.register_password);
        mPasswordRepeatView = (EditText) view.findViewById(R.id.register_password_repeat);

        Button mEmailSignInButton = (Button) view.findViewById(R.id.email_register_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = view.findViewById(R.id.email_register_form);
        mProgressView = view.findViewById(R.id.register_progress);

        sharedPref = getActivity().getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        StringBuilder urlBuilder = new StringBuilder();
        baseUrl=sharedPref.getString("url","http://192.168.0.178:8080/rail4you/");
        urlBuilder.append(baseUrl);
        //todo: correct url en register fragment afwerken
        urlBuilder.append("/addMail");
        url=urlBuilder.toString();

        Log.d(TAG, "onViewCreated: url" + url);


        this.view = view;
    }



    private void attemptRegister() {
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

            networkRegister(userCredentials);

        }

    }

    private void networkRegister(String userCredentials) {
        final String userCredentials1 = userCredentials;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        stringBuilder.append("?name=");
        stringBuilder.append(mNameView.getText().toString());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                stringBuilder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.has("user_id")){
                                long id = jsonObject.getLong("user_id");
                                String name = jsonObject.getString("name");
                                String email = jsonObject.getString("email");
                                User user = new User(id,name,email);
                                commitUser(user);

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

    }

    @Override
    public void requestCompleted(String response) {

    }

    @Override
    public void requestEndedWithError(VolleyError error) {

    }
}
