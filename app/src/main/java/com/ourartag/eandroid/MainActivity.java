package com.ourartag.eandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }
    Toolbar mToolbar;
    TextView toolbarTxtVeiw;
    FragmentContainerView myFrag1;
    String myID, myName;
    myVolley mLivelog;
    Button btn1;
    SharedPreferences sharedPref;
    SharedPreferences.Editor mEditor;
    Config myConfigs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTxtVeiw = (TextView)mToolbar.findViewById(R.id.toolbarTextView);


        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mContext = this;
        if(isNetworkAvailable(getApplication())){

        }
        sharedPref = mContext.getSharedPreferences("ourartag.com", Context.MODE_PRIVATE);
        mEditor = sharedPref.edit();
//        mEditor.putString("myID", "3");
        //myID = sharedPref.getString("myID", "0");

        myFrag1 = findViewById(R.id.fragmentContainerView);
        mLivelog = new myVolley(this);
        myConfigs = new Config();

        myID = myConfigs.get("myID","0");
        myName = myConfigs.get("myName","");
        Log.i("onCreate", "ttttttttttttttttttttttttttttttttt ID: " + myID);

        if(myID.equals("0")){
            Fragment mLogin = new Login();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
            fragmentTransaction.commit();
        }else {
            toolbarTxtVeiw.setText(myName);
            myFrag1.setVisibility(View.GONE);
        }

//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Fragment mLogin = new Login();
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
//                fragmentTransaction.commit();
//
//            }
//        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.eandroid_menu, menu);
        Log.i("MMMMMMMMM", "onCreateOptionsMenu");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_about){
            Log.i("MMMMMMMMM", "+++++++++++++++++++++++++++++++++++++++++++++++++Clicked on about");
            return true;

        }else if(item.getItemId()==R.id.menu_logout){
            myConfigs.set("myID","0");
            myConfigs.set("myName","nobody");

            System.exit(0);
            Log.i("MMMMMMMMM", "+++++++++++++++++++++++++++++++++++++++++++++++++Clicked on Logout");
//            View menuItemView = findViewById(R.id.moreVertical);
//            PopupMenu popup = new PopupMenu(getContext(), menuItemView);
//            popup.getMenuInflater().inflate(R.menu.eandroid_menu, popup.getMenu());
//            popup.show();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }


    private Boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
}