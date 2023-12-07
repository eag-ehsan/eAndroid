package com.ourartag.eandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

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
        Context ctx = getApplicationContext();
        sharedPref = ctx.getSharedPreferences("ourartag.com", Context.MODE_PRIVATE);
        mEditor = sharedPref.edit();
//        mEditor.putString("myID", "3");
        //myID = sharedPref.getString("myID", "0");
        btn1 = findViewById(R.id.button);
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
}