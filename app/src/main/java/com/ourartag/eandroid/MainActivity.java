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
import android.graphics.Typeface;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //region class Objects
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }
    Toolbar mToolbar;
    TextView toolbarTxtVeiw, dialogMsg, ourartaglink, mainAppStatus, textViewRespoNotif, txtChatForSend;
    FragmentContainerView myFrag1;
    esTimer mainTimer, mLivelogTimer, mNotifregTimer, mSetNotifCTXTimer, mSetChatTimer;
    String myID, myName, notifCTS, cmdCTS;
    myVolley mLivelog, mNotifreg, mSetNotifCTX, mSetChat;
    Button btn1, dlgOk;
    SharedPreferences sharedPref;
    SharedPreferences.Editor mEditor;
    Config myConfigs;
    LinearLayout dlgPage;
    MenuItem about_menu, logout_menu;
    Boolean menu_stat, periodPerocess;
    int MAIN_LOOP_SEC = 10, TRY_T0_SEND_CHAT = 4, mainLoopTry, mCounterSendchat;
    //String[] mainListArray = {"Android","IPhone"};
    ArrayList<String> mainListArray=new ArrayList<String>();
    ArrayList<Item> items;
    ListView mainListView;
    ImageButton sendBotton;
    ListViewAdapter adapter;
    Item myItems;
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
        mNotifreg = new myVolley(this);
        mSetNotifCTX = new myVolley(this);
        mSetChat = new myVolley(this);
        myConfigs = new Config();
        mainTimer = new esTimer(new Runnable() {public void run() {mainLoop();}}, 10, 1000 * MAIN_LOOP_SEC, false);
        mLivelogTimer = new esTimer(new Runnable() {public void run() {mLivelogResponseCheck();}}, 10, 1000, false);
        mNotifregTimer = new esTimer(new Runnable() {public void run() {getNotifCheck();}}, 10, 1000, false);
        mSetNotifCTXTimer = new esTimer(new Runnable() {public void run() {setNotifCTXcheck();}}, 10, 1000, false);
        mSetChatTimer = new esTimer(new Runnable() {public void run() {sendChatToServerCheck();}}, 10, 1000, false);
        //endregion

        //region findViewById
        dlgPage = findViewById(R.id.dialogPage);
        dialogMsg = findViewById(R.id.vtxtDialogMsg);
        myFrag1 = findViewById(R.id.fragmentContainerView);
        dlgOk = findViewById(R.id.dialogOK);
        mainAppStatus = findViewById(R.id.mainAppStatus);
        textViewRespoNotif = findViewById(R.id.txtResponotif);
        mainListView = findViewById(R.id.main_list);
        sendBotton = findViewById(R.id.sendBtn);
        txtChatForSend = findViewById(R.id.txtChatToSend);
        //endregion
        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, mainListArray);
        //mainListView.setAdapter(adapter);
        items = new ArrayList<>();
        adapter = new ListViewAdapter(this, items);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub



                items.remove(position);

                adapter.notifyDataSetChanged();
            }
        });
        //region Configs
        sharedPref = mContext.getSharedPreferences("ourartag.com", Context.MODE_PRIVATE);
        mEditor = sharedPref.edit();
        //mEditor.putString("myID", "3");
        //myID = sharedPref.getString("myID", "0");
        myID = myConfigs.get("myID","0");
        myName = myConfigs.get("myName","");
        //endregion

        //region defualt values
        menu_stat = false;
        periodPerocess = false;
        notifCTS = "";
        cmdCTS = "";
        mainLoopTry = 0;
        mCounterSendchat=0;
        //endregion

        //region setOnClickListeners
        dlgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.exit(0);

            }
        });
        sendBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setChatToListView(myName, txtChatForSend.getText().toString());
                sendChatToServer(txtChatForSend.getText().toString());
                sendBotton.setEnabled(false);
                sendBotton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
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
            myFrag1.setVisibility(View.VISIBLE);
            Fragment mLogin = new Login();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
            fragmentTransaction.commit();
            menu_stat = false;
            invalidateOptionsMenu();
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
        about_menu = (MenuItem) menu.findItem(R.id.menu_about);
        logout_menu = (MenuItem) menu.findItem(R.id.menu_logout);
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
            myFrag1.setVisibility(View.VISIBLE);
            Fragment mLogin = new Login();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
            fragmentTransaction.commit();
            menu_stat = false;
            invalidateOptionsMenu();
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
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        logout_menu.setEnabled(menu_stat);
        about_menu.setEnabled(menu_stat);
        super.onPrepareOptionsMenu(menu);
        return true;
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

    public void loginInit(){
        myID = myConfigs.get("myID","0");
        myName = myConfigs.get("myName","");
        toolbarTxtVeiw.setText(myName);
        myFrag1.setVisibility(View.GONE);
        mLivelog.setmyID(myID);
        mLivelog.setStarterUrl("https://www.ourartag.com/live.php?liivx=");
        mNotifreg.setmyID(myID);
        mNotifreg.setStarterUrl("https://www.ourartag.com/reqnotif.php?liivx=");
        mSetNotifCTX.setmyID(myID);
        mSetNotifCTX.setStarterUrl("https://www.ourartag.com/setp.php?liivx=");
        mSetChat.setmyID(myID);
        mSetChat.setStarterUrl("https://www.ourartag.com/setchat.php?liivx=");
        mainTimer.startTimer();
        menu_stat = true;
        invalidateOptionsMenu();
    }

    public void setChatToListView(String txtTitle, String txtMessage) {

        items.add(new Item(R.drawable.d3,
                txtTitle,txtMessage,
                true,
                ContextCompat.getColor(getContext(), R.color.dark_bg6),
                ContextCompat.getColor(getContext(), R.color.light_text2),
                1,
                7f,
                Typeface.BOLD,
                ContextCompat.getColor(getContext(), R.color.light_bg2),
                ContextCompat.getColor(getContext(), R.color.black),
                3,
                7.5f,
                Typeface.NORMAL));
        adapter.notifyDataSetChanged();
    }


    //endregion

    //region private methods
    private void mainLoop(){
        Log.i("oooooooooooo", "++++++++++++++++++periodPerocess is " + periodPerocess + " ++++++++++++++++++++++++++++++mainLoop");
        if(!periodPerocess){
            mainLoopTry = 0;
            mLivelog.singleRequest(2);
            mLivelogTimer.startTimer();
        }else {
            mainLoopTry++;
            mainAppStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
            mainAppStatus.setText("Server response timeout. check internet");
        }
    }
    private void mLivelogResponseCheck(){
        if(mLivelog.get_isSingleRequestAns() && !mLivelog.get_ansSingleRequest().isEmpty()){
            Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~mLivelogTimer.stopTimer()~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
            mLivelogTimer.stopTimer();
            String isOk = mLivelog.get_ansSingleRequest();
            if(isOk.equals("sq")){
                mainAppStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.warcolor));
                mainAppStatus.setText("Poor connection. Internet is weak.");
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~sq END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                periodPerocess = false;
                mainTimer.stopTimer();
                mainTimer.startTimer();
            }else if(isOk.equals("er")){
                mainAppStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.warcolor));
                mainAppStatus.setText("Server said Error in its response.");
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ er END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                periodPerocess = false;
            }else if(isOk.equals("11") || isOk.equals("00") || isOk.equals("10") || isOk.equals("01")){
                mainAppStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.okcolor));
                mainAppStatus.setText("Connection OK, you are Online");
                notifCTS = isOk.substring(1);
                cmdCTS = isOk.substring(0,1);
                if(notifCTS.equals("1")){
                    getNotif();
                }else{
                    Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ done END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                    periodPerocess = false;
                }


            }else{

            }
            mLivelog.set_isSingleRequestAns(false);
        }
    }
    private void getNotif(){
        Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~mNotifreg.singleRequest~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
        mNotifreg.singleRequest(180);
        mNotifregTimer.startTimer();
    }
    private void getNotifCheck(){
        if(mNotifreg.get_isSingleRequestAns() && !mNotifreg.get_ansSingleRequest().isEmpty()){
            Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~mNotifregTimer.stopTimer~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
            mNotifregTimer.stopTimer();
            String respo = mNotifreg.get_ansSingleRequest();
            String isOk = respo.substring(0,6);
            if(isOk.equals("sqErro")){
                long templng1 = mainTimer.getCurrentDuration();
                if(templng1<((1000*MAIN_LOOP_SEC)/2)){
                    Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~sqErro -> getNotif() ~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                    getNotif();
                }else{
                    Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~sqErro -> END~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                    periodPerocess = false;
                }
            }else if(isOk.equals("cancel")){
                periodPerocess = false;
            }else if(isOk.equals("error!")){
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~mNotifreg error! END~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                periodPerocess = false;
                
            }else if(isOk.equals("<html>")){
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~<html>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                String responotif = respo.substring(53);
                textViewRespoNotif.setText(responotif);
                setNotifCTXf();
            }else{

            }
            mNotifreg.set_isSingleRequestAns(false);
        }
    }
    private void setNotifCTXf(){
        Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~setNotifCTXf.send4Params~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
        mSetNotifCTX.setParams("p1","v1", "p2", "v2");
        mSetNotifCTX.setValues("notifCTS", "0", "notifCTR", "1");
        mSetNotifCTX.send4Params(2);
        mSetNotifCTXTimer.startTimer();
    }
    private void setNotifCTXcheck(){
        if(mSetNotifCTX.get_isSend4ParamAns() && !mSetNotifCTX.get_ansSend4Param().isEmpty()){
            Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~setNotifCTXf.stopTimer~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
            mNotifregTimer.stopTimer();
            String isOk = mSetNotifCTX.get_ansSend4Param();
            if(isOk.equals("ok")){
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ok END~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                periodPerocess = false;

            }else if(isOk.equals("p4")){
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~setNotifCTXf. p4 END~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                periodPerocess = false;
            }else if(isOk.equals("er")){
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~setNotifCTXf. er END~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                periodPerocess = false;

            }else{
                periodPerocess = false;
            }
            mSetNotifCTX.set_isSend4ParamAns(false);
        }
    }

    private void sendChatToServer(String txtMessage) {
        Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~setNotifCTXf.send4Params~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
        mSetChat.setParams("msg","", "", "");
        mSetChat.setValues(txtMessage, "", "", "");
        mSetChat.send1Params(2);
        mSetChatTimer.startTimer();
    }
    private void sendChatToServerCheck(){
        if(mSetChat.get_isSend1ParamAns() && !mSetChat.get_ansSend1Param().isEmpty()){
            Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~mSetChat.stopTimer~`~~~~~~~~~~~~~~~~~~~~~~~~~~~ delay: " + Math.floor(mSetChatTimer.getCurrentDuration()));
            mSetChatTimer.stopTimer();
            String isOk = mSetChat.get_ansSend1Param();
            if(isOk.equals("ok")){
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~chat ok~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ");
                mSetChat.set_isSend1ParamAns(false);
                sendBotton.setEnabled(true);
                txtChatForSend.setText("");
                sendBotton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
                dialogMsg.setText("Your Message sent successfully");
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.okcolor));
            }else{
                Log.i("ckk", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~chat Fail~~~~~~~~~~~~~~~~~~try(" + String.valueOf(mCounterSendchat) + ")~~~~~~~~ ");
                mSetChat.set_isSend1ParamAns(false);
                mSetChat.send1Params(2);
                mSetChatTimer.startTimer();
                mCounterSendchat++;
                if(mCounterSendchat>TRY_T0_SEND_CHAT){
                    sendBotton.setEnabled(true);
                    sendBotton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
                    dialogMsg.setText("Sending Message Fail. No internet");
                    dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
                }

            }

        }
    }

    //endregion
}