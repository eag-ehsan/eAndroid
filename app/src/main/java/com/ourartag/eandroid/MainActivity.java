package com.ourartag.eandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    TextView toolbarTxtVeiw, dialogMsg, ourartaglink, mainAppStatus, textViewRespoNotif, txtChatForSend, usernamePleasewait, nameAnyUserSelect_tv, online_txttv, seen_txttv;
    Button btn1, dlgOk;
    LinearLayout dlgPage, mPleaseWaitLinl;
    MenuItem about_menu, logout_menu;
    ListView mainListView;
    ImageButton sendBotton;
    ImageView online_imgv, seen_imgv;
    FragmentContainerView myFrag1;
    esTimer baseTimer, mainTimer, mLivelogTimer, mNotifregTimer, mSetNotifCTXTimer, mSetChatTimer, mGetChatsTimer, myusr0Timer;
    String myID, myName, myPass, notifCTS, cmdCTS, newchat, myLati, myLoni, myServerAddress, mIsLogin, nameAnyUserSelect;
    myVolley mLivelog, mNotifreg, mSetNotifCTX, mSetChat, mGetChats, myusr0;

    SharedPreferences sharedPref;
    SharedPreferences.Editor mEditor;
    Config myConfigs;
    Boolean menu_stat, periodPerocess, chatgetPerocess, myServiceEnable, isAnyUserSelect, isAnyUserSelectOnline, isAnyUserSelectSeen;
    int MAIN_LOOP_SEC = 3, TRY_T0_SEND_CHAT = 4, TRY_T0_GET_CHAT = 4, mainLoopTry, mCounterSendchat, mCounterGetchat;
    //String[] mainListArray = {"Android","IPhone"};
    ArrayList<String> mainListArray=new ArrayList<String>();
    ArrayList<Item> items;

    ListViewAdapter adapter;
    MediaPlayer mMediaPlayer;
    Intent mServiceIntent;
    private BackgroundLocationUpdateService mBackgroundLocationUpdateService;
    NotificationsUtils nNu;
    LinearLayout mRooteroot, mPanelbala;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Item myItems;
    //endregion
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    //region Overrides
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        //region permission check
        int permission = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        int permissionread = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionread != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        //endregion

        //region Toolbar Cods
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTxtVeiw = (TextView)mToolbar.findViewById(R.id.toolbarTextView);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //endregion

        //region Class get Objrcts

        nNu = new NotificationsUtils(getApplicationContext());
        if(!nNu.isNotificationEnabled()){
            Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName())
                    .putExtra(Settings.EXTRA_CHANNEL_ID, 2727);
            startActivity(settingsIntent);
        }
        mLivelog = new myVolley(this);
        mNotifreg = new myVolley(this);
        mSetNotifCTX = new myVolley(this);
        mSetChat = new myVolley(this);
        mGetChats = new myVolley(this);
        myusr0 = new myVolley(this);
        myConfigs = new Config();
        mBackgroundLocationUpdateService = new BackgroundLocationUpdateService();
        mainTimer = new esTimer(new Runnable() {public void run() {mainLoop();}}, 10, 1000 * MAIN_LOOP_SEC, false);
        baseTimer = new esTimer(new Runnable() {public void run() {baseLoop();}}, 10, 500, false);
        mLivelogTimer = new esTimer(new Runnable() {public void run() {mLivelogResponseCheck();}}, 10, 1000, false);
        mNotifregTimer = new esTimer(new Runnable() {public void run() {getNotifCheck();}}, 10, 1000, false);
        mSetNotifCTXTimer = new esTimer(new Runnable() {public void run() {setNotifCTXcheck();}}, 10, 1000, false);
        mSetChatTimer = new esTimer(new Runnable() {public void run() {sendChatToServerCheck();}}, 10, 1000, false);
        mGetChatsTimer = new esTimer(new Runnable() {public void run() {getChatsRequestCheck();}}, 10, 1000, false);
        myusr0Timer = new esTimer(new Runnable() {public void run() {usrLoginCheck();}}, 10, 1000, false);
        //endregion

        //region findViewById
        mRooteroot = findViewById(R.id.rooteroot);
        mPanelbala = findViewById(R.id.panelbala_id);
        final View activityRootView = findViewById(R.id.rooteroot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(mContext, 200)) { // if more than 200 dp, it's probably a keyboard...
                    Log.e("oooo","open");
                    mainAppStatus.setVisibility(View.GONE);
                    mPanelbala.setVisibility(View.GONE);

                }else{
                    mainAppStatus.setVisibility(View.VISIBLE);
                    mPanelbala.setVisibility(View.VISIBLE);
                }
            }
        });
        dlgPage = findViewById(R.id.dialogPage);
        dialogMsg = findViewById(R.id.vtxtDialogMsg);
        myFrag1 = findViewById(R.id.fragmentContainerView);
        dlgOk = findViewById(R.id.dialogOK);
        mainAppStatus = findViewById(R.id.mainAppStatus);
        textViewRespoNotif = findViewById(R.id.txtResponotif);
        mainListView = findViewById(R.id.main_list);
        sendBotton = findViewById(R.id.sendBtn);
        txtChatForSend = findViewById(R.id.txtChatToSend);
        mPleaseWaitLinl = findViewById(R.id.please_wait_for_login_id);
        usernamePleasewait = findViewById(R.id.username_please_wait_login);
        nameAnyUserSelect_tv = findViewById(R.id.nameAnySelectUser_id);
        online_imgv = findViewById(R.id.online_imgid);
        seen_imgv = findViewById(R.id.seen_imgid);
        online_txttv = findViewById(R.id.online_txtid);
        seen_txttv = findViewById(R.id.seen_txtid);
        //endregion

        //region ListView setup
        items = new ArrayList<>();
        adapter = new ListViewAdapter(this, items);
        mainListView.setAdapter(adapter);
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                nameAnyUserSelect = items.get(position).getTitle();
                if(!nameAnyUserSelect.isEmpty()){

                    if(nameAnyUserSelect.equals(myName)){
                        nameAnyUserSelect_tv.setText(getString(R.string.lng_selectusr) + " " + getString(R.string.lng_notyourself));
                        isAnyUserSelect = false;
                    }else{
                        nameAnyUserSelect_tv.setText(nameAnyUserSelect);
                        isAnyUserSelect = true;
                    }
                }else{
                    isAnyUserSelect = false;
                }


                //items.remove(position);

                //adapter.notifyDataSetChanged();
            }
        });

        //endregion

        //region HomeWatcher
        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                myConfigs.set("man","raftam");
                mEditor.putString("man", "raftam");
                mEditor.apply();
            }
            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();
        //endregion

        //region Configs
        sharedPref = mContext.getSharedPreferences(getString(R.string.site_Address_quick), Context.MODE_PRIVATE);
        mEditor = sharedPref.edit();
        mEditor.putString("loginInit", "no");
        mEditor.apply();
        myID = sharedPref.getString("myID", "0");
        myID = myConfigs.get("myID","0");
        myName = myConfigs.get("myName","");
        myPass = myConfigs.get("myPass","");
        myLati = myConfigs.get("myLati","0.0");
        myLoni = myConfigs.get("myLoni","0.0");
        mIsLogin = myConfigs.get("isLogin","0");
        if(myConfigs.get("bgService","nos").equals("on")) {
            myServiceEnable = true;
        }else if(myConfigs.get("bgService","nos").equals("nos")){
            myConfigs.set("bgService","on");
            myServiceEnable = true;
        }else{
            myServiceEnable = false;
        }
        myServerAddress = myConfigs.get("myServerAddress","nos");
        if(myServerAddress.equals("nos")){
            myConfigs.set("myServerAddress",getString(R.string.site_Address));
        }
        myServerAddress = myConfigs.get("myServerAddress", "");
        //endregion

        //region defualt values
        menu_stat = false;
        periodPerocess = false;
        chatgetPerocess = false;
        notifCTS = "";
        cmdCTS = "";
        newchat = "";
        mainLoopTry = 0;
        mCounterSendchat = 0;
        mCounterGetchat = 100;
        isAnyUserSelect = false;
        isAnyUserSelectOnline = false;
        isAnyUserSelectSeen = false;
        nameAnyUserSelect = "";
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

                //setChatToListView(myName, txtChatForSend.getText().toString());
                isAnyUserSelectSeen = false;
                sendChatToServer(txtChatForSend.getText().toString());
                sendBotton.setEnabled(false);
                sendBotton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
            }
        });
        //endregion

        //region Auto login
        if(!isNetworkAvailable(getApplication())){
            dialogMsg.setText(getString(R.string.lng_error1));
            dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
            dlgPage.setVisibility(View.VISIBLE);
        }
        if(myID.equals("0") || myName.toString().isEmpty()){
            myFrag1.setVisibility(View.VISIBLE);
            Fragment mLogin = new Login();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
            fragmentTransaction.commit();
            menu_stat = false;
            invalidateOptionsMenu();
        }else {
            if(mIsLogin.equals("1")){
                loginInit();
            }else{
                myusr0.setmyID("0");
                myusr0.setParams("name","pass", "", "");
                myusr0.setValues(myName, myPass, "", "");
                myusr0.setStarterUrl(myServerAddress + "usr.php?liivx=");
                myusr0.send2Params(5);
                myusr0Timer.startTimer();
                usernamePleasewait.setText(myName);
                mPleaseWaitLinl.setVisibility(View.VISIBLE);
            }

        }
        //endregion

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(mContext, "کجا میخوای بری؟ بخدا اگه بزارم!! شما مهمانید", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        String tnppp = sharedPref.getString("loginInit", "no");
        Log.i("---------", "--------------------------------------------------------------------------------------------------onResume" + tnppp);
        if(tnppp.equals("ok")){
            myConfigs.set("man","hastam");
            mEditor.putString("man", "hastam");
            mEditor.apply();
            //periodPerocess = false;
            if(!mainTimer.isTimerOn()){
                Log.i("---------", "--------------------------------------------------------------------------------------------------onResume on" );
                periodPerocess = false;
                mainTimer.startTimer();
            }
            if(!baseTimer.isTimerOn()){
                baseTimer.startTimer();
            }

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.i("---------", "--------------------------------------------------------------------------------------------------onPause");
        myConfigs.set("man","raftam");
        mEditor.putString("man", "raftam");
        mEditor.apply();
        mainTimer.stopTimer();
        baseTimer.stopTimer();
        mLivelogTimer.stopTimer();
        mNotifregTimer.stopTimer();
        mSetNotifCTXTimer.stopTimer();
        mSetChatTimer.stopTimer();
        mGetChatsTimer.stopTimer();
    }
    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        myConfigs.set("man","raftam");
        mEditor.putString("man", "raftam");
        mEditor.apply();
        mainTimer.stopTimer();
        baseTimer.stopTimer();
        mLivelogTimer.stopTimer();
        mNotifregTimer.stopTimer();
        mSetNotifCTXTimer.stopTimer();
        mSetChatTimer.stopTimer();
        mGetChatsTimer.stopTimer();
        Log.i("---------", "--------------------------------------------------------------------------------------------------onDestroy");
        if(myConfigs.get("bgService","off").equals("on")){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }



        super.onDestroy();
    }

    //endregion

    //region Main Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.eandroid_menu, menu);
        about_menu = (MenuItem) menu.findItem(R.id.menu_about);
        logout_menu = (MenuItem) menu.findItem(R.id.menu_logout);
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
//            View menuItemView = findViewById(R.id.moreVertical);
//            PopupMenu popup = new PopupMenu(getContext(), menuItemView);
//            popup.getMenuInflater().inflate(R.menu.eandroid_menu, popup.getMenu());
//            popup.show();
            return true;
        }else if(item.getItemId()==R.id.menu_sendloc){
            myFrag1.setVisibility(View.VISIBLE);
            Fragment mMapneshan = new Mapneshan();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mMapneshan);
            fragmentTransaction.commit();
            //playAlarmSound();
            return true;
        }else if(item.getItemId()==R.id.menu_setting){
            myFrag1.setVisibility(View.VISIBLE);
            Fragment mMysetting = new Mysetting();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, mMysetting);
            fragmentTransaction.commit();
            return true;
        }else if(item.getItemId()==R.id.menu_refresh){
            mCounterGetchat = 100;
            return true;
        }else if(item.getItemId()==R.id.menu_sendtoserver){
            Intent myIntent = new Intent(MainActivity.this, Sendtoserver.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            MainActivity.this.startActivity(myIntent);
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
        mEditor.putString("myID", myID);
        mEditor.putString("myName", myName);
        mEditor.apply();
        toolbarTxtVeiw.setText(myName);
        myFrag1.setVisibility(View.GONE);
        mLivelog.setmyID(myID);

        mLivelog.setStarterUrl(myServerAddress + "liveu.php?liivx=");
        mNotifreg.setmyID(myID);
        mNotifreg.setStarterUrl(myServerAddress + "reqnotif.php?liivx=");
        mSetNotifCTX.setmyID(myID);
        mSetNotifCTX.setStarterUrl(myServerAddress + "setp.php?liivx=");
        mSetChat.setmyID(myID);
        mSetChat.setStarterUrl(myServerAddress + "setchat.php?liivx=");
        mGetChats.setmyID(myID);
        mGetChats.setStarterUrl(myServerAddress + "getchats.php?liivx=");
        mainTimer.startTimer();
        baseTimer.startTimer();
        menu_stat = true;
        invalidateOptionsMenu();
        myConfigs.set("man","hastam");
        mEditor.putString("man", "hastam");
        mEditor.apply();
        myConfigs.set("isLogin","1");
        if(myServiceEnable){
            sureServiceisStarting();
        }else{
            stopMyService();
        }
        mEditor.putString("loginInit", "ok");
        mEditor.apply();

    }

    public void stopMyService(){
        stopService(new Intent(MainActivity.this, BackgroundLocationUpdateService.class));
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
    public void removeAllItemsFromListview(){
        items.clear();
        adapter.notifyDataSetChanged();
    }

    public void playAlarmSound()  {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
    }
    public void sureServiceisStarting(){
        Boolean ympbool = isMyServiceRunning(mBackgroundLocationUpdateService.getClass());
        if(!ympbool){
            mServiceIntent = new Intent(MainActivity.this, mBackgroundLocationUpdateService.getClass());

            if (!isMyServiceRunning(mBackgroundLocationUpdateService.getClass())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getApplicationContext().startForegroundService(new Intent(getApplicationContext(), mBackgroundLocationUpdateService.getClass()));
                } else {
                    getApplicationContext().startService(new Intent(getApplicationContext(), mBackgroundLocationUpdateService.getClass()));
                }
                //startService(mServiceIntent);
                Log.i("Count", "################## starting service");
            }
        }else{
            Log.i("Count", "################## great. you are alive yourself");
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("^^^^^^^^^^^^^^^^^^", "^^^^^^^^^^^^^^^^^^ Running");
                return true;
            }
        }
        Log.i ("^^^^^^^^^^^^^^^^^^", "^^^^^^^^^^^^^^^^^^ Not running");
        return false;
    }
    //endregion

    //region private methods
    private void baseLoop(){



        if(isAnyUserSelect){
            if(isAnyUserSelectOnline){
                online_imgv.setImageDrawable(getDrawable(R.drawable.ledg));
                online_txttv.setText(getString(R.string.lng_online));
                online_txttv.setTextColor(ContextCompat.getColor(getContext(), R.color.okcolor));
            }else{
                online_imgv.setImageDrawable(getDrawable(R.drawable.ledr));
                online_txttv.setText(getString(R.string.lng_offline));
                online_txttv.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
            }
            if(isAnyUserSelectSeen && isAnyUserSelectOnline){
                seen_imgv.setImageDrawable(getDrawable(R.drawable.ledg));
                seen_txttv.setText(getString(R.string.lng_seen));
                seen_txttv.setTextColor(ContextCompat.getColor(getContext(), R.color.okcolor));
            }else{
                seen_imgv.setImageDrawable(getDrawable(R.drawable.ledr));
                seen_txttv.setText(getString(R.string.lng_unseen));
                seen_txttv.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
            }
        }else {
            online_imgv.setImageDrawable(getDrawable(R.drawable.ledoff));
            seen_imgv.setImageDrawable(getDrawable(R.drawable.ledoff));
            online_txttv.setText(getString(R.string.lng_offline));
            online_txttv.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
            seen_txttv.setText(getString(R.string.lng_unseen));
            seen_txttv.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
        }
    }
    private void mainLoop(){
        Log.i ("|||||||||||||||||", "|||||||||||||||||||||||||||||||||||||| main loop");
        if(!periodPerocess){
            mainLoopTry = 0;
            if(isAnyUserSelect){
                mLivelog.setParams("usrname","", "", "");
                mLivelog.setValues(nameAnyUserSelect, "", "", "");
                mLivelog.send1Params(5);
                periodPerocess = true;
            }else {
                mLivelog.setParams("no","", "", "");
                mLivelog.setValues("0", "", "", "");
                mLivelog.send1Params(5);
                periodPerocess = true;

            }
            if(mLivelogTimer.isTimerOn()){
                mLivelogTimer.stopTimer();
                mLivelogTimer.startTimer();
            }else {
                mLivelogTimer.startTimer();
            }
            Log.i("~~~~~~~LiveLog", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LiveLog~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
        }else {
            mainLoopTry++;
            mainAppStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
            mainAppStatus.setText(getString(R.string.lng_mainStatus_txt1));
        }

        if(!chatgetPerocess && mCounterGetchat>=100 && mCounterGetchat<103){
            getChatsRequest();
        }

        //baseLoop();
    }
    private void mLivelogResponseCheck(){
        if(mLivelog.get_isSend1ParamAns() && !mLivelog.get_ansSend1Param().isEmpty()){
            Log.i("~~~~~~~LiveLog", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LiveLog~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
            mLivelogTimer.stopTimer();
            String isOk = mLivelog.get_ansSend1Param();
            if(isOk.equals("p1Err")){
                mainAppStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.warcolor));
                mainAppStatus.setText(getString(R.string.lng_mainStatus_txt2));
                Log.i("~~~~~~~LiveLog", "                                 sqE END                                try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                periodPerocess = false;
                mainTimer.stopTimer();
                mainTimer.startTimer();
            }else if(isOk.equals("error")){
                mainAppStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.warcolor));
                mainAppStatus.setText(getString(R.string.lng_mainStatus_txt3));
                Log.i("~~~~~~~LiveLog", "                                 err END                                try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                periodPerocess = false;
            }else {
                mainAppStatus.setTextColor(ContextCompat.getColor(getContext(), R.color.okcolor));
                mainAppStatus.setText(getString(R.string.lng_mainStatus_txt4));
                notifCTS = isOk.substring(1,2);
                cmdCTS = isOk.substring(0,1);
                newchat = isOk.substring(2,3);//"00000"
                String tempOnlin = isOk.substring(3,4);
                String tempSeen = isOk.substring(4);
                if(notifCTS.equals("1")){
                    Log.i("???????????", "????????????????????????????????????????????????????????????????????? = " + isOk);
                    getNotif();
                }else{
                    periodPerocess = false;
                }
                if(newchat.equals("1")){
                    if(!chatgetPerocess){
                        mCounterGetchat = 100;
                    }
                }
                if(tempOnlin.equals("1")){
                    isAnyUserSelectOnline = true;
                }else{
                    isAnyUserSelectOnline = false;
                }
                if(tempSeen.equals("0")){
                    isAnyUserSelectSeen = true;
                }else{
                    isAnyUserSelectSeen = false;
                }
            }
            mLivelog.set_isSend1ParamAns(false);


        }
    }


    private void getNotif(){
        mNotifreg.singleRequest(180);
        mNotifregTimer.startTimer();
    }
    private void getNotifCheck(){
        if(mNotifreg.get_isSingleRequestAns() && !mNotifreg.get_ansSingleRequest().isEmpty()){
            Log.i("~~~~~~getNotif", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~getNotif~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
            mNotifregTimer.stopTimer();
            String respo = mNotifreg.get_ansSingleRequest();
            String isOk = respo.substring(0,6);
            if(isOk.equals("sqErro")){
                Log.i("~~~~~~getNotif", "                                 sqErro END                             try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
                long templng1 = mainTimer.getCurrentDuration();
                if(templng1<((1000*MAIN_LOOP_SEC)/2)){
                    getNotif();
                }else{
                    periodPerocess = false;
                }
            }else if(isOk.equals("cancel")){
                periodPerocess = false;
            }else if(isOk.equals("error!")){
                periodPerocess = false;
                
            }else if(isOk.equals("<html>")){
                String responotif = respo.substring(53);
                textViewRespoNotif.setText(responotif);
                setNotifCTXf();
            }else{

            }
            mNotifreg.set_isSingleRequestAns(false);
        }
    }


    private void setNotifCTXf(){
        mSetNotifCTX.setParams("p1","v1", "p2", "v2");
        mSetNotifCTX.setValues("notifCTS", "0", "notifCTR", "1");
        mSetNotifCTX.send4Params(2);
        mSetNotifCTXTimer.startTimer();
    }
    private void setNotifCTXcheck(){
        if(mSetNotifCTX.get_isSend4ParamAns() && !mSetNotifCTX.get_ansSend4Param().isEmpty()){
            Log.i("~~~~~NotifCTXf", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~NotifCTXf~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
            mNotifregTimer.stopTimer();
            String isOk = mSetNotifCTX.get_ansSend4Param();
            if(isOk.equals("ok")){
                periodPerocess = false;

            }else if(isOk.equals("p4")){
                periodPerocess = false;
            }else if(isOk.equals("er")){
                periodPerocess = false;

            }else{
                periodPerocess = false;
            }
            mSetNotifCTX.set_isSend4ParamAns(false);
        }
    }


    private void sendChatToServer(String txtMessage) {
        mSetChat.setParams("msg","", "", "");
        mSetChat.setValues(txtMessage, "", "", "");
        mSetChat.send1Params(2);
        mSetChatTimer.startTimer();
    }
    private void sendChatToServerCheck(){
        if(mSetChat.get_isSend1ParamAns() && !mSetChat.get_ansSend1Param().isEmpty()){
            Log.i("~~~~~~sendChat", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~sendChat~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            mSetChatTimer.stopTimer();
            String isOk = mSetChat.get_ansSend1Param();
            if(isOk.equals("ok")){
                mSetChat.set_isSend1ParamAns(false);
                sendBotton.setEnabled(true);
                txtChatForSend.setText("");
                sendBotton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
                dialogMsg.setText(getString(R.string.lng_msg1));
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.okcolor));
            }else{
                Log.i("~~~~~~sendChat", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Chat Fail~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                mSetChat.set_isSend1ParamAns(false);
                mSetChat.send1Params(2);
                mSetChatTimer.startTimer();
                mCounterSendchat++;
                if(mCounterSendchat>TRY_T0_SEND_CHAT){
                    sendBotton.setEnabled(true);
                    sendBotton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue));
                    dialogMsg.setText(getString(R.string.lng_error2));
                    dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
                }

            }

        }
    }


    private void getChatsRequest(){
        mGetChats.singleRequest(2068);
        mGetChatsTimer.startTimer();
        chatgetPerocess = true;

    }
    private void getChatsRequestCheck(){
        if(mGetChats.get_isSingleRequestAns() && !mGetChats.get_ansSingleRequest().isEmpty()){
            mCounterGetchat++;
            mGetChatsTimer.stopTimer();
            String completeresp = mGetChats.get_ansSingleRequest();
            if(completeresp.equals("sqError")){

            }else{
                String aslStr = "";
                String strttem = completeresp.substring(0,53);
                strttem = strttem.substring(47);
                String isOk = strttem.substring(0,2);
                //int numberofe =Integer.valueOf(strttem.substring(2));
                if(isOk.equals("ok")){
                    Log.i("~~~~~~~getChat", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~get chat ok~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ");
                    int b = completeresp.indexOf("</body>") ;
                    aslStr = completeresp.substring(53, b);
                    String[] echitems = aslStr.split(";");
                    int ii=0, hh=0;
                    String tempstrName, tempstrMsg;
                    removeAllItemsFromListview();
                    for(ii=(echitems.length-1);ii>=0;ii--){
                        hh = echitems[ii].indexOf(">");
                        tempstrName = echitems[ii].substring(0,hh);
                        tempstrMsg = echitems[ii].substring(hh+1);
                        setChatToListView(tempstrName, tempstrMsg);


                    }
                    chatgetPerocess = false;
                }else{
                    if(mCounterGetchat>65000){
                        mCounterGetchat = 0;
                    }
                    chatgetPerocess = false;
                }
            }
            mGetChats.set_isSingleRequestAns(false);


        }

    }

    private void usrLoginCheck(){
        if(myusr0.get_isSend2ParamAns() && !myusr0.get_ansSend2Param().isEmpty()){
            myusr0Timer.stopTimer();
            String isOk = myusr0.get_ansSend2Param();
            if(isOk.substring(0,2).equals("ok")){
                String isID = isOk.substring(2);
                int tmpit = Integer.parseInt(isID);
                myConfigs.set("myID",tmpit + "");
                myusr0.set_isSend2ParamAns(false);
                myConfigs.set("isLogin","1");
                mPleaseWaitLinl.setVisibility(View.GONE);
                loginInit();


            }else{
                mPleaseWaitLinl.setVisibility(View.GONE);
                myFrag1.setVisibility(View.VISIBLE);
                Fragment mLogin = new Login();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, mLogin);
                fragmentTransaction.commit();
                menu_stat = false;
                invalidateOptionsMenu();
            }
        }
    }


    //endregion




}