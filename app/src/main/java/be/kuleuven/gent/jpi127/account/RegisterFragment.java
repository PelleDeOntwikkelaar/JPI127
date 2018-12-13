package be.kuleuven.gent.jpi127.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.login.widget.LoginButton;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.support.VolleyResponseListener;

public class RegisterFragment extends Fragment implements VolleyResponseListener {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, null);
        mEmailView = (AutoCompleteTextView) view.findViewById(R.id.register_email);

        mPasswordView = (EditText) view.findViewById(R.id.register_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

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
        //todo: correct url en register frqgment afwerken
        urlBuilder.append("login");
        url=urlBuilder.toString();

        Log.d(TAG, "onViewCreated: url" + url);


        this.view = view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
