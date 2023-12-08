package com.ourartag.eandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }

    FragmentContainerView myFrag1;
    String myID;
    myVolley mLivelog;
    Button btn1;
    SharedPreferences sharedPref;
    SharedPreferences.Editor mEditor;
    Config myConfigs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        if(isNetworkAvailable(getApplication())){

        }
        sharedPref = mContext.getSharedPreferences("ourartag.com", Context.MODE_PRIVATE);
        mEditor = sharedPref.edit();
//        mEditor.putString("myID", "3");
        //myID = sharedPref.getString("myID", "0");
        btn1 = findViewById(R.id.button);
        myFrag1 = findViewById(R.id.fragmentContainerView);
        mLivelog = new myVolley(this);
        myConfigs = new Config();

        myID = myConfigs.get("myID","0");
        Log.i("onCreate", "ttttttttttttttttttttttttttttttttt ID: " + myID);

        if(myID.equals("0")){
            Fragment mLogin = new Login();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
            fragmentTransaction.commit();
        }else {
            myFrag1.setVisibility(View.GONE);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment mLogin = new Login();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
                fragmentTransaction.commit();

            }
        });

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