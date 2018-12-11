package be.kuleuven.gent.jpi127.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.general.fragments.MainFragment;

/**
 * Fragment called when a user goes to settings or user settings.
 * This fragment gives the user the choice log out.
 *
 * @author Pelle Reyniers
 */
public class AccountSettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private Button button;

    private static final String TAG = "AccountSettingsFragment";

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
        return inflater.inflate(R.layout.fragment_account_settings, null);
    }

    /**
     * Second method called on start up.
     * All actions and variables are initiated here.
     * Click listener is declared here.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        sharedPreferences = getActivity().getSharedPreferences("myPrefs",Context.MODE_PRIVATE);

        button = (Button) view.findViewById(R.id.accountSettingsButton);


        if(sharedPreferences.contains("user")){
            button.setText("Log Out");
        }else {
            button.setVisibility(View.INVISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(sharedPreferences.contains("user")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("user");
                    editor.commit();

                    Log.d(TAG, "onClick: der is dus een userke aanwezig he");
                    boolean loggedin = sharedPreferences.contains("user");
                    Log.d(TAG, "onClick: loggedin: " + loggedin);
                    Fragment fragment=new MainFragment();
                    FragmentManager fragmentManager =getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.screen_area,fragment);
                    fragmentTransaction.commit();

                }
            }

        });

    }
}
