package com.ourartag.eandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
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
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    //region class Objects
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }
    Toolbar mToolbar;
    TextView toolbarTxtVeiw, dialogMsg, ourartaglink, mainAppStatus;
    FragmentContainerView myFrag1;
    esTimer mainTimer, mLivelogTimer;
    String myID, myName;
    myVolley mLivelog;
    Button btn1, dlgOk;
    SharedPreferences sharedPref;
    SharedPreferences.Editor mEditor;
    Config myConfigs;
    LinearLayout dlgPage;
    int MAIN_LOOP_SEC = 15;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //region Toolbar Cods
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTxtVeiw = (TextView)mToolbar.findViewById(R.id.toolbarTextView);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //endregion

        //region Class get Objrcts
        mContext = this;
        mLivelog = new myVolley(this);
        myConfigs = new Config();
        mainTimer = new esTimer(new Runnable() {public void run() {mainLoop();}}, 1000 * MAIN_LOOP_SEC, false);
        mLivelogTimer = new esTimer(new Runnable() {public void run() {mLivelogResponseCheck();}}, 1000, false);
        //endregion

        //region findViewById
        dlgPage = findViewById(R.id.dialogPage);
        dialogMsg = findViewById(R.id.vtxtDialogMsg);
        myFrag1 = findViewById(R.id.fragmentContainerView);
        dlgOk = findViewById(R.id.dialogOK);
        mainAppStatus = findViewById(R.id.mainAppStatus);
        //endregion

        //region Configs
        sharedPref = mContext.getSharedPreferences("ourartag.com", Context.MODE_PRIVATE);
        mEditor = sharedPref.edit();
        //mEditor.putString("myID", "3");
        //myID = sharedPref.getString("myID", "0");
        myID = myConfigs.get("myID","0");
        myName = myConfigs.get("myName","");
        //endregion

        //region defualt values

        //endregion

        //region setOnClickListeners
        dlgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.exit(0);

            }
        });

        //endregion

        //region Auto login
        if(!isNetworkAvailable(getApplication())){
            dialogMsg.setText("No Internet. please check your data or wifi");
            dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
            dlgPage.setVisibility(View.VISIBLE);
        }
        if(myID.equals("0")){
            Fragment mLogin = new Login();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
            fragmentTransaction.commit();
        }else {
            loginInit();
        }
        //endregion

    }

    //region Main Menu
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
            myFrag1.setVisibility(View.VISIBLE);
            Fragment mAbout = new About();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mAbout);
            fragmentTransaction.commit();
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

    //endregion

    //region public methods
    public Boolean isNetworkAvailable(Application application) {
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

    public void turnOffFrag(){
        myFrag1.setVisibility(View.GONE);
    }
    //endregion

    //region private methods
    private void mainLoop(){
        if(!mLivelogTimer.isTimerOn()){
            mLivelog.singleRequest(2);
            mLivelogTimer.startTimer();
        }
    }
    private void mLivelogResponseCheck(){
        if(mLivelog.get_isSingleRequestAns()){
            String isOk = mLivelog.get_ansSingleRequest();

            if(isOk.equals("sq")){
                mLivelog.singleRequest(2);
            }else if(isOk.equals("er")){
                mainAppStatus.setText("Server said Error in its response.");
                mLivelogTimer.stopTimer();
            }else if(isOk.equals("11") || isOk.equals("00") || isOk.equals("10") || isOk.equals("01")){
                mainAppStatus.setText("Connection OK, you are Online");

                mLivelogTimer.stopTimer();
            }else{

            }
        }
    }
    private void loginInit(){
        toolbarTxtVeiw.setText(myName);
        myFrag1.setVisibility(View.GONE);
        mLivelog.setmyID(myID);
        mLivelog.setStarterUrl("https://www.ourartag.com/live.php?liivx=");
        mainTimer.startTimer();
    }
    //endregion
}