package be.kuleuven.gent.jpi127;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import be.kuleuven.gent.jpi127.fragments.account.AccountSettingsFragment;
import be.kuleuven.gent.jpi127.fragments.account.LoginFragment;
import be.kuleuven.gent.jpi127.fragments.general.FavoritesFragment;
import be.kuleuven.gent.jpi127.fragments.general.InfoFragment;
import be.kuleuven.gent.jpi127.fragments.general.MainFragment;
import be.kuleuven.gent.jpi127.fragments.general.OnlineFragment;
import be.kuleuven.gent.jpi127.fragments.general.TrackedFragment;
import be.kuleuven.gent.jpi127.model.User;
import be.kuleuven.gent.jpi127.support.DownloadService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    public FragmentManager fragmentManager;
    private Fragment fragment;
    private MainFragment mainFragment;
    private Bundle bundle;
    private Boolean loggedIn;
    private ImageView profilePicture;
    private TextView userNameField;
    private TextView userEMailField;
    private SharedPreferences sharedPref;
    private View headerView;



    CallbackManager callbackManager;


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                List<String> names = bundle.getStringArrayList("result");
                mainFragment.fillList(names);
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode,resultCode,data);



    }

    /**
     * When the main activity is called and initiated, this method will be executed on start up.
     * This method looks for a maybe already present user, initiates the toolbar and navigation drawer.
     * It looks for the user interface elements used throughout the activity.
     * It also adds action listeners for the opening and closing mechanisms of the navigation drawer and
     * a clicklistner for the navigation header.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //start up activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //login value
        loggedIn=false;

        mainFragment=new MainFragment();

        //set shared prefs for use in application
        sharedPref = getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //TODO: set dedicated ip and port number
        String url="http://192.168.10.101:2003/rail4you";
        editor.putString("url",url);
        editor.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        //opening and closing navigation menu
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                changeLayout(sharedPref,headerView);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }

        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent = new Intent(this, DownloadService.class);
        // add infos for the service which file to download and where to store
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(url);
        stringBuilder.append("/stations");
        intent.putExtra("urlpath", url);

        startService(intent);

        //navigation header initialisatie
        profilePicture = (ImageView) headerView.findViewById(R.id.profilePicture);
        userNameField = (TextView) headerView.findViewById(R.id.UserName);
        userEMailField = (TextView) headerView.findViewById(R.id.Useremail);
        checkForUser();



        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean loggedIn2=checkForUser();

                if(loggedIn2){
                    fragment = new AccountSettingsFragment();
                } else {
                    fragment = new LoginFragment();
                }
                changeFragment();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });



        //default start up frament is nieuwe meting.
        int id = R.id.NewSearchMI;
        fragment= chooseFragment(id);
        changeFragment();

        callbackManager = CallbackManager.Factory.create();


    }

    public CallbackManager getCallbackManager(){
        return callbackManager;
    }

    private void printkeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("be.kuleuven.gent.jpi127", PackageManager.GET_SIGNATURES);
            for (Signature signature: info.signatures){
                MessageDigest md =MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * Method used to change the screen area to a selected fragment.
     * No arguments because the destination fragment is preset.
     */
    private void changeFragment() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.screen_area,fragment);
        fragmentTransaction.commit();
    }


    /**
     * Looks in the shared preferences if there is a user object present.
     * @return result of lookup in boolean format.
     */
    private boolean checkForUser() {
        sharedPref = getSharedPreferences("myPrefs",Context.MODE_PRIVATE);

        changeLayout(sharedPref,headerView);

        return sharedPref.contains("user");
    }


    /**
     * Override a back press to start the navigation closing animation if necessary.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * An override method to perform the switch of a fragment when the user selects another option in the navigation drawer.
     * @param item The id of the selected option
     * @return Always true if method succeeds
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        fragment=chooseFragment(id);

        if(fragment!=null){
            changeFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method used to translate the id given as new selected option into the right fragment.
     * @return The corresponding fragment is returned.
     */
    public Fragment chooseFragment(int id){
        Fragment fragment=new MainFragment();

        if (id == R.id.NewSearchMI) {
            fragment = mainFragment;

        } else if (id == R.id.FavoritesMI) {
            if (sharedPref.contains("user")){
                fragment = new FavoritesFragment();
            } else {
                Toast.makeText(this, "Please log in first.", Toast.LENGTH_LONG).show();
                fragment = new LoginFragment();
            }

        } else if (id == R.id.TrackedMI) {
            fragment = new TrackedFragment();

        } else if (id == R.id.SettingsMI) {
            fragment=new AccountSettingsFragment();

        } else if (id == R.id.DelenMI) {
            fragment=new OnlineFragment();

        } else if (id == R.id.Info) {
            fragment=new InfoFragment();

        } else{
            fragment=null;
        }

        return fragment;
    }

    /**
     * This method changes the layout of the navigation header.
     * When the navigation drawer opens, this method will check if the layout has to be changed.
     * @param sharedPreferences The shared preferences of the application
     * @param view View containing the layout elements that needs to be changed.
     */
    public void changeLayout(SharedPreferences sharedPreferences, View view){

        boolean loggedIn= sharedPreferences.contains("user");
        Log.d(TAG, "changeLayout: loggedin: " + loggedIn);
        ImageView profilePicture = (ImageView) view.findViewById(R.id.profilePicture);
        TextView userNameField = (TextView) view.findViewById(R.id.UserName);
        TextView userEMailField = (TextView) view.findViewById(R.id.Useremail);

        if(!loggedIn){
            userNameField.setText("Not logged in");
            userEMailField.setText("");
            profilePicture.setImageDrawable(getDrawable(R.mipmap.ic_launcher_round));
        } else{

            Gson gson = new Gson();
            String json = sharedPreferences.getString("user", "");
            User user = gson.fromJson(json, User.class);
            userNameField.setText(user.getName());
            String emailField;
            if(user.getEmail()!=null) emailField=user.getEmail();
            else emailField=String.valueOf(user.getId());
            userEMailField.setText(emailField);
            profilePicture.setImageDrawable(getDrawable(R.mipmap.ic_launcher_round));

        }
    }


}
