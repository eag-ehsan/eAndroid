package com.ourartag.eandroid;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //region class Objects
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    ProgressDialog dialog = null;
    ArrayList<Model> mList;
    RecyclerView mRecyclerView;
    MultiViewTypeAdapter adapter;
    Toolbar mToolbar;
    TextView ourartaglink;
    myVolley mNotifreg, mSetNotifCTX;
    TextView toolbarTxtVeiw, dialogMsg, mainAppStatus, textViewRespoNotif, txtChatForSend, usernamePleasewait, nameAnyUserSelect_tv, online_txttv, seen_txttv, txt_record_alert;
    Button btn1, dlgOk;
    FloatingActionButton btn_mainFab1, btn_mainFab2, btn_mainFab3, btn_mainFab4, btn_mainFab5, btn_mainFab6, btn_fabPlay, btn_fabCancel;
    LinearLayout dlgPage, mPleaseWaitLinl;
    MenuItem about_menu, logout_menu, setting_menu, sendloc_menu, refresh_menu;
    ImageButton sendBotton, sendImageButton;
    ImageView online_imgv, seen_imgv, myTumbnail;
    FragmentContainerView myFrag1;
    esTimer baseTimer, mainTimer, mLivelogTimer, mNotifregTimer, mSetNotifCTXTimer, mSetChatTimer, mGetChatsTimer, myusr0Timer;
    String myID, myName, myPass, notifCTS, cmdCTS, newchat, myLati, myLoni, myServerAddress, mIsLogin, nameAnyUserSelect, pathapp, upLoadVoiceServerUri;
    myVolley mLivelog, mSetChat, mGetChats, myusr0;
    SharedPreferences sharedPref;
    SharedPreferences.Editor mEditor;
    Config myConfigs;
    MediaRecorder recorder;
    MediaPlayer mPlayer;
    Boolean menu_stat, periodPerocess, chatgetPerocess, myServiceEnable, isAnyUserSelect, isAnyUserSelectOnline, isAnyUserSelectSeen, iduserSeenNewChat, permissionTpRecVoice;
    private boolean fabStateRecord = false, fabStatePlay = false;
    int MAIN_LOOP_SEC = 3, TRY_T0_SEND_CHAT = 4, mainLoopTry, mCounterSendchat, mCounterGetchat, getModcounter, lastChatID = 0;
    //String[] mainListArray = {"Android","IPhone"};
    ArrayList<String> mainListArray=new ArrayList<String>();



    MediaPlayer mMediaPlayer;
    Intent mServiceIntent;
    private BackgroundLocationUpdateService mBackgroundLocationUpdateService;
    NotificationsUtils nNu;
    LinearLayout mRooteroot, mPanelbala;



    //endregion

    //region static items
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }
    //endregion


    //region Overrides

    @SuppressLint({"CutPasteId", "SdCardPath"})
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
        askForFilePermissions();
        //endregion

        //region Toolbar Cods
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
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
        //mNotifreg = new myVolley(this);
        //mSetNotifCTX = new myVolley(this);
        mSetChat = new myVolley(this);
        mGetChats = new myVolley(this);
        myusr0 = new myVolley(this);
        myConfigs = new Config(getContext());
        mBackgroundLocationUpdateService = new BackgroundLocationUpdateService();
        mainTimer = new esTimer(new Runnable() {public void run() {mainLoop();}}, 10, 1000 * MAIN_LOOP_SEC, false);
        baseTimer = new esTimer(new Runnable() {public void run() {baseLoop();}}, 10, 500, false);
        mLivelogTimer = new esTimer(new Runnable() {public void run() {mLivelogResponseCheck();}}, 10, 1000, false);
        //mNotifregTimer = new esTimer(new Runnable() {public void run() {getNotifCheck();}}, 10, 1000, false);
        //mSetNotifCTXTimer = new esTimer(new Runnable() {public void run() {setNotifCTXcheck();}}, 10, 1000, false);
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

        sendBotton = findViewById(R.id.sendBtn);
        sendImageButton = findViewById(R.id.sendImageBtn_id);
        txtChatForSend = findViewById(R.id.txtChatToSend);
        mPleaseWaitLinl = findViewById(R.id.please_wait_for_login_id);
        usernamePleasewait = findViewById(R.id.username_please_wait_login);
        nameAnyUserSelect_tv = findViewById(R.id.nameAnySelectUser_id);
        online_imgv = findViewById(R.id.online_imgid);
        seen_imgv = findViewById(R.id.seen_imgid);
        online_txttv = findViewById(R.id.online_txtid);
        seen_txttv = findViewById(R.id.seen_txtid);
        myTumbnail = findViewById(R.id.tumbnail_id);
        btn_mainFab1 = (FloatingActionButton) findViewById(R.id.main_fab1);
        btn_mainFab2 = (FloatingActionButton) findViewById(R.id.main_fab2);
        btn_mainFab3 = (FloatingActionButton) findViewById(R.id.main_fab3);
        btn_mainFab4 = (FloatingActionButton) findViewById(R.id.main_fab4);
        btn_mainFab5 = (FloatingActionButton) findViewById(R.id.main_fab5);
        btn_mainFab6 = (FloatingActionButton) findViewById(R.id.main_fab6);
        btn_fabPlay = (FloatingActionButton) findViewById(R.id.fabPlayvoce);
        btn_fabCancel = (FloatingActionButton) findViewById(R.id.fabVoiceCancel);
        txt_record_alert = findViewById(R.id.txtRecordAlert);
        //endregion

        //region Intefaces
        HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                myConfigs.set_man("oftadam");
                mEditor.putString("man", "oftadam");
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

        myID = myConfigs.get_myID();
        if(myID.equals("0")) {
            myID = sharedPref.getString("myID", "0");
        }
        myName = myConfigs.get_myName();
        myPass = myConfigs.get_myPass();
        myLati = myConfigs.get_myLati();
        myLoni = myConfigs.get_myLoni();
        mIsLogin = sharedPref.getString("isLogin","0");
        myServiceEnable = !myConfigs.get_bgService().equals("off");
        myServerAddress = myConfigs.get_myServerAddress();

        //endregion

        //region defualt values
        upLoadVoiceServerUri = myServerAddress + "upvoice.php";
        pathapp =  "/data/data/com.ourartag.eandroid/" ;
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
        getModcounter = 0;
        iduserSeenNewChat = false;
        permissionTpRecVoice = false;
        //endregion

        //region recyclerview setup
        mList= new ArrayList<>();
//        mList.add(new Model(Model.TEXT_TYPE, R.drawable.d4, "Ehsan Ahmadi", getString(R.string.lng_login_txt1), ""));
//        mList.add(new Model(Model.IMAGE_TYPE,R.drawable.d4, "Dorsa", "", myServerAddress + "upload/2_1702396287700.jpg"));
//        mList.add(new Model(Model.AUDIO_TYPE, R.raw.fart, "Dorsa", "", ""));


        adapter = new MultiViewTypeAdapter(mList,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapter.setOnItemClickListener(new RecyclerViewClickListener(){

            @SuppressLint("SetTextI18n")
            @Override
            public void recyclerViewListClicked(View v, int position) {
                nameAnyUserSelect = mList.get(position).getTitle();
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
                String tmpBooli = mList.get(position).getImageURL();
                if(!tmpBooli.isEmpty()){
                    myFrag1.setVisibility(View.VISIBLE);
                    Fragment mViewImageFullscreen = new ViewImageFullscreen(tmpBooli);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView, mViewImageFullscreen);
                    fragmentTransaction.commit();
                }

            }

            @Override
            public void rVClicked_delete(View v, int position) {

            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);


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

                if(txt_record_alert.getVisibility()==View.VISIBLE){
                    if(fabStatePlay){
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                        fabStatePlay = false;
                        btn_fabPlay.setImageResource(R.drawable.volume_50);
                    }
                    dialog = ProgressDialog.show(MainActivity.this, "", "Uploading file...", true);
                    //String ttt = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/ourartag/tr2.3gp";
                    String ttt = pathapp + "tr2.3gp";

                    new Thread(new Runnable() {
                        public void run() {
                            String tmyID = myConfigs.get_myID();
                            uploadVoiceFile(ttt, myID);

                        }
                    }).start();

                }else{
                    isAnyUserSelectSeen = false;
                    if(!txtChatForSend.getText().toString().isEmpty()){
                        sendChatToServer(txtChatForSend.getText().toString());
                        sendBotton.setEnabled(false);
                        sendBotton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
                    }

                }

            }
        });
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, BrowseImageForSend.class);
                MainActivity.this.startActivity(myIntent);
                //setChatToListView(myName, txtChatForSend.getText().toString());
                //isAnyUserSelectSeen = false;
                //sendChatToServer(txtChatForSend.getText().toString());
                //sendImageButton.setEnabled(false);
                //sendImageButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
            }
        });
        btn_mainFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myFrag1.setVisibility(View.VISIBLE);
                Fragment mMysetting = new Mysetting();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, mMysetting);
                fragmentTransaction.commit();
            }
        });
        btn_mainFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastChatID = 0;
                removeAllItemsFromListview();
                mCounterGetchat = 100;
            }
        });
        btn_mainFab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, BrowsAllImages.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        btn_mainFab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myFrag1.setVisibility(View.VISIBLE);
                Fragment mMapneshan = new Mapneshan();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, mMapneshan);
                fragmentTransaction.commit();
            }
        });
        btn_mainFab5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAudioPermissions();
                if(permissionTpRecVoice){
                    if(!fabStateRecord){
                        btn_mainFab5.setImageResource(R.drawable.microphone_slash_50);
                        txtChatForSend.setVisibility(View.GONE);
                        sendBotton.setVisibility(View.GONE);
                        txt_record_alert.setVisibility(View.VISIBLE);
                        txt_record_alert.setText(getString(R.string.lng_record_alert));
                        txt_record_alert.setTextColor(ContextCompat.getColor(getContext(),R.color.errcolo));
                        fabStateRecord = true;
                        recorder = new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        //String ttt = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/ourartag/tr2.3gp";
                        String ttt = pathapp + "tr2.3gp";
                        recorder.setOutputFile(ttt);
                        try {
                            recorder.prepare();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        recorder.start();
                    }else {
                        btn_mainFab5.setImageResource(R.drawable.microphone_50);
                        fabStateRecord = false;

                        recorder.stop();
                        recorder.release();
                        recorder = null;
                        sendBotton.setVisibility(View.VISIBLE);
                        btn_fabCancel.setVisibility(View.VISIBLE);
                        btn_fabPlay.setVisibility(View.VISIBLE);
                        txt_record_alert.setVisibility(View.VISIBLE);
                        txt_record_alert.setText(getString(R.string.lng_record_prev));
                        txt_record_alert.setTextColor(ContextCompat.getColor(getContext(),R.color.okcolor));
                    }
                }
            }
        });
        btn_mainFab6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, BrowseImageForSend.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        btn_fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fabStatePlay){
                    mPlayer.stop();
                    mPlayer.release();
                    mPlayer = null;
                    fabStatePlay = false;
                    btn_fabPlay.setImageResource(R.drawable.volume_50);
                }else {
                    try {
                        //String ttt = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/ourartag/tr2.3gp";
                        String ttt = pathapp + "tr2.3gp";
                        //Uri uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                        mPlayer = new MediaPlayer();
                        //mPlayer = MediaPlayer.create(getContext(), uri);
                        mPlayer.setDataSource(ttt);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mPlayer.setLooping(true);
                    mPlayer.start();
                    btn_fabPlay.setImageResource(R.drawable.volume_xmark_50);
                    fabStatePlay = true;
                }


            }
        });
        btn_fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtChatForSend.setVisibility(View.VISIBLE);
                btn_fabCancel.setVisibility(View.GONE);
                btn_fabPlay.setVisibility(View.GONE);
                txt_record_alert.setVisibility(View.GONE);


            }
        });
        //endregion

        //region Auto login
        if(!isNetworkAvailable(getApplication())){
            dialogMsg.setText(getString(R.string.lng_error1));
            dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
            dlgPage.setVisibility(View.VISIBLE);
        }
        if(myID.equals("0") || myName.isEmpty()){
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

//            if (myFrag1.getVisibility() == View.VISIBLE) {
//
//            }
//            else {
//
//            }
            Toast.makeText(mContext, "No No", Toast.LENGTH_LONG).show();
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
            myConfigs.set_man("hastam");
            mEditor.putString("man", "hastam");
            mEditor.apply();
            //periodPerocess = false;
//            if(!mainTimer.isTimerOn()){
//                Log.i("---------", "--------------------------------------------------------------------------------------------------onResume on" );
//                periodPerocess = false;
//                mainTimer.startTimer();
//            }
//            if(!baseTimer.isTimerOn()){
//                baseTimer.startTimer();
//            }

        }
    }
    @Override
    protected void onPause() {
        super.onPause();
//        Log.i("---------", "--------------------------------------------------------------------------------------------------onPause");
        myConfigs.set_man("oftadam");
        mEditor.putString("man", "oftadam");
        mEditor.apply();
//        mainTimer.stopTimer();
//        baseTimer.stopTimer();
//        mLivelogTimer.stopTimer();
//        mNotifregTimer.stopTimer();
//        mSetNotifCTXTimer.stopTimer();
//        mSetChatTimer.stopTimer();
//        mGetChatsTimer.stopTimer();
    }
    @Override
    protected void onDestroy() {
        //stopService(mServiceIntent);
        myConfigs.set_man("raftam");
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
        if(myConfigs.get_bgService().equals("on")){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }



        super.onDestroy();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // permission was granted, yay!
//
//            } else {
//                // permission denied, boo!
//            }
//
//
//        }
    }
    //endregion



    //region functions

    //region Main Menu Functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.eandroid_menu, menu);
        about_menu = (MenuItem) menu.findItem(R.id.menu_about);
        logout_menu = (MenuItem) menu.findItem(R.id.menu_logout);
        setting_menu = (MenuItem) menu.findItem(R.id.menu_setting);
        sendloc_menu = (MenuItem) menu.findItem(R.id.menu_sendloc);
        refresh_menu = (MenuItem) menu.findItem(R.id.menu_refresh);
        if(sharedPref.getString("loginInit", "").equals("ok")){
            setting_menu.setVisible(true);
            sendloc_menu.setVisible(true);
            refresh_menu.setVisible(true);
        }else {
            setting_menu.setVisible(false);
            sendloc_menu.setVisible(false);
            refresh_menu.setVisible(false);
        }
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
            myConfigs.set_myID("0");
            myConfigs.set_myName("nobody");
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
            removeAllItemsFromListview();
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

    //region ask permission Functions
    public void askForFilePermissions() {
        boolean hasPermission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!hasPermission) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            int MY_PERMISSIONS_RECORD_AUDIO = 1;
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            permissionTpRecVoice = true;

        }
    }
    //endregion

    //region Loop Functions
    @SuppressLint("UseCompatLoadingForDrawables")
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
                mLivelog.setmyID(myID);
                mLivelog.setStarterUrl(myServerAddress + "liveu.php?liivx=");
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
        getModcounter++;
        if(getModcounter>65000){ getModcounter = 0;}
        if(Math.floorMod(getModcounter, 10)==0){
            String mman = sharedPref.getString("man", "");
            if(mman.equals("oftadam")){
                if(!iduserSeenNewChat){
                    playAlarmSound();
                    iduserSeenNewChat = true;
                }
            }
            if(mman.equals("hastam")){
                mCounterGetchat = 100;
            }

            Log.e ("|||||||||||||||||", "|||||||||||||||||||||||||||||||||||||| main loop - " + chatgetPerocess.toString());
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
                String mman = sharedPref.getString("man", "");
                if(newchat.equals("1") && mman.equals("hastam")){

                    if(!chatgetPerocess){
                        mCounterGetchat = 100;
                    }
                }
                isAnyUserSelectOnline = tempOnlin.equals("1");
                isAnyUserSelectSeen = tempSeen.equals("0");
                periodPerocess = false;
            }
            mLivelog.set_isSend1ParamAns(false);


        }
    }
    //endregion

    //region chat Functions
    private void sendChatToServer(String txtMessage) {
        mSetChat.setmyID(myID);
        mSetChat.setStarterUrl(myServerAddress + "setchat.php?liivx=");
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
        mGetChats.setmyID(myID);
        mGetChats.setStarterUrl(myServerAddress + "getchats2.php?liivx=");
        mGetChats.singleRequest(2068);
        mGetChatsTimer.startTimer();
        chatgetPerocess = true;

    }
    private void getChatsRequestCheck(){
        if(mGetChats.get_isSingleRequestAns() && !mGetChats.get_ansSingleRequest().isEmpty()){
            mCounterGetchat++;
            mGetChatsTimer.stopTimer();
            String completeresp = mGetChats.get_ansSingleRequest();
            if(completeresp.startsWith("sqError")){
                chatgetPerocess = false;
            }else{
                String withoutHtmlHeader = completeresp.substring(47);
                if(withoutHtmlHeader.startsWith("ok")){
                    String justChatsWithendBody = withoutHtmlHeader.substring(2);
                    Log.i("~~~~~~~getChat", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~get chat ok~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ");
                    int b = justChatsWithendBody.indexOf("</body>") ;
                    String justChat = justChatsWithendBody.substring(0, b);
                    String[] echitems = justChat.split(";");
                    int ii=0, hh=0;
                    String[] wholemsgfortest = echitems[0].split(">");
                    int lastChatIDD = Integer.parseInt(wholemsgfortest[0]);
                    if(lastChatIDD!=lastChatID){
                        String strName, strMsg, strdatetime;
                        int userIDD = 0, chatIDD=0;
                        removeAllItemsFromListview();
                        for(ii=(echitems.length-1);ii>=0;ii--){
                            String[] wholemsg = echitems[ii].split(">");
                            chatIDD = Integer.parseInt(wholemsg[0]);
                            userIDD = Integer.parseInt(wholemsg[1]);
                            strName = wholemsg[2];
                            strdatetime = wholemsg[3];
                            strMsg = wholemsg[4];
                            //hh = echitems[ii].indexOf(">");
                            //tempstrName = echitems[ii].substring(0,hh);
                            //tempstrMsg = echitems[ii].substring(hh+1);
                            boolean mtisMe = (userIDD==Integer.parseInt(myID));;
                            setChatToRecyclerview(mtisMe, chatIDD, userIDD, strName, strdatetime, strMsg);
                            lastChatID = chatIDD;
                        }
                    }
                    String mman = sharedPref.getString("man", "");
                    iduserSeenNewChat = !mman.equals("oftadam");
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
    private void setChatToRecyclerview(Boolean itsmee,int usrID, int chatID, String txtTitle, String dtdt, String txtMessage) {
        boolean temp_itischat = false;
        boolean temp_itischatvoc = false;
        //Log.e("in","---------------------------" + txtMessage);
        if(txtMessage.length()>10){
            if(txtMessage.startsWith("IMG")){

                temp_itischat = true;
            }else if(txtMessage.startsWith("VOC")){

                temp_itischat = true;
                temp_itischatvoc = true;
            }
        }
        if(temp_itischat){
            if(temp_itischatvoc){
                String[] echitems = txtMessage.split(" ");
                String filen = echitems[2].replace(" ", "");
                mList.add(new Model(itsmee, Model.AUDIO_TYPE, R.drawable.d4, dtdt, txtTitle, "",myServerAddress + "voice/" + filen, usrID, chatID));
            }else {
                String[] echitems = txtMessage.split(" ");
                String filen = echitems[2].replace(" ", "");
                mList.add(new Model(itsmee, Model.IMAGE_TYPE, R.drawable.d4, dtdt, txtTitle, "",myServerAddress + "upload/" + filen, usrID, chatID));
            }

        }else{
            mList.add(new Model(itsmee, Model.TEXT_TYPE, R.drawable.d4, dtdt,  txtTitle, txtMessage, "", usrID, chatID));

        }
        mRecyclerView.setAdapter(adapter);
        //mRecyclerView.smoothScrollToPosition(mList.size()-1);
        mRecyclerView.scrollToPosition(mList.size()-1);

    }
    private void removeAllItemsFromListview(){
        mList.clear();
        mRecyclerView.setAdapter(adapter);
    }

    //endregion

    //region Network Functions
    private void uploadVoiceFile(String pathh, String hereMyid) {
        int serverResponseCode = 0;
        //String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        //File sourceFile = new File(sourceFileUri);

        try {

            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(pathh);
            URL url = new URL(upLoadVoiceServerUri);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            String filenametosend = hereMyid + "_" + System.currentTimeMillis() + ".3gp";
            conn.setRequestProperty("uploaded_file", filenametosend);
            Log.e("filename", "::::::::::::::::::::::::::::::::::::::::::::::::::::" + filenametosend);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + filenametosend + "" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            //ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //FixBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            //byte[] mybytesRead = bos.toByteArray();
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            //dos.write(mybytesRead);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("oooooooooooooooooo", "oooooooooooooooooooooooooooooooooooooooooooooooooo  HTTP Response is :" + serverResponseMessage + ": " + serverResponseCode);

            if (serverResponseCode == 200) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.i("uuuuuuuuuuuuu", "uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu  File Upload Complete.");
                        int temi = Integer.parseInt(hereMyid);
                        String temp2 = "";
                        if (temi < 10)
                            temp2 = "00" + temi;
                        else if (temi < 100)
                            temp2 = "0" + temi;
                        else
                            temp2 = "" + temi;
                        sendChatToServer("VOC " + temp2 + " " + filenametosend);
                        txtChatForSend.setVisibility(View.VISIBLE);
                        btn_fabCancel.setVisibility(View.GONE);
                        btn_fabPlay.setVisibility(View.GONE);
                        txt_record_alert.setVisibility(View.GONE);
                    }
                });
            }

            //close the streams //
            //fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {

            dialog.dismiss();
            ex.printStackTrace();

            runOnUiThread(new Runnable() {
                public void run() {
                    Log.i("uuuuuuuuuuuuu", "uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu  MalformedURLException");

                }
            });
            Log.i("uuuuuuuuuuuuu", "uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu  error: " + ex.getMessage());
        } catch (Exception e) {

            dialog.dismiss();
            e.printStackTrace();

            runOnUiThread(new Runnable() {
                public void run() {

                    Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                            Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("Upload file to server Exception", "Exception : "
                    + e.getMessage(), e);
        }
        dialog.dismiss();


    }
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
    public void sureServiceisStarting(){
        boolean ympbool = isMyServiceRunning(mBackgroundLocationUpdateService.getClass());
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
    public boolean isMyServiceRunning(Class<?> serviceClass) {
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

    //region Login Functions
    private void usrLoginCheck(){
        if(myusr0.get_isSend2ParamAns() && !myusr0.get_ansSend2Param().isEmpty()){
            myusr0Timer.stopTimer();
            String isOk = myusr0.get_ansSend2Param();
            if(isOk.startsWith("ok")){
                String isID = isOk.substring(2);
                int tmpit = Integer.parseInt(isID);
                myConfigs.set_myID(tmpit + "");
                myusr0.set_isSend2ParamAns(false);
                mEditor.putString("isLogin", "1");
                mEditor.apply();
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
    public void loginInit(){
        myID = myConfigs.get_myID();
        myName = myConfigs.get_myName();
        mEditor.putString("myID", myID);
        mEditor.putString("myName", myName);
        mEditor.apply();
        toolbarTxtVeiw.setText(myName);
        myFrag1.setVisibility(View.GONE);

        //mNotifreg.setmyID(myID);
        //mNotifreg.setStarterUrl(myServerAddress + "reqnotif.php?liivx=");
        //mSetNotifCTX.setmyID(myID);
        //mSetNotifCTX.setStarterUrl(myServerAddress + "setp.php?liivx=");


        mainTimer.startTimer();
        baseTimer.startTimer();
        menu_stat = true;
        invalidateOptionsMenu();
        myConfigs.set_man("hastam");
        mEditor.putString("man", "hastam");
        mEditor.apply();
        mEditor.putString("isLogin", "1");
        mEditor.apply();
        if(myServiceEnable){
            sureServiceisStarting();
        }else{
            stopMyService();
        }
        mEditor.putString("loginInit", "ok");
        mEditor.apply();


    }

    //endregion

    //region Utilities
    public void turnOffFrag(){
        myFrag1.setVisibility(View.GONE);
    }

    public void stopMyService(){
        stopService(new Intent(MainActivity.this, BackgroundLocationUpdateService.class));
    }
    public void playAlarmSound()  {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
    }

    //endregion

    //endregion

    //region None usage functions
//    private void getNotif(){
//        mNotifreg.singleRequest(180);
//        mNotifregTimer.startTimer();
//    }
//    private void getNotifCheck(){
//        if(mNotifreg.get_isSingleRequestAns() && !mNotifreg.get_ansSingleRequest().isEmpty()){
//            Log.i("~~~~~~getNotif", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~getNotif~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
//            mNotifregTimer.stopTimer();
//            String respo = mNotifreg.get_ansSingleRequest();
//            String isOk = respo.substring(0,6);
//            if(isOk.equals("sqErro")){
//                Log.i("~~~~~~getNotif", "                                 sqErro END                             try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
//                long templng1 = mainTimer.getCurrentDuration();
//                if(templng1<((1000*MAIN_LOOP_SEC)/2)){
//                    //getNotif();
//                }else{
//                    periodPerocess = false;
//                }
//            }else if(isOk.equals("cancel")){
//                periodPerocess = false;
//            }else if(isOk.equals("error!")){
//                periodPerocess = false;
//
//            }else if(isOk.equals("<html>")){
//                String responotif = respo.substring(53);
//                textViewRespoNotif.setText(responotif);
//                setNotifCTXf();
//            }else{
//
//            }
//            mNotifreg.set_isSingleRequestAns(false);
//        }
//    }

//    private void setNotifCTXf(){
//        mSetNotifCTX.setParams("p1","v1", "p2", "v2");
//        mSetNotifCTX.setValues("notifCTS", "0", "notifCTR", "1");
//        mSetNotifCTX.send4Params(2);
//        mSetNotifCTXTimer.startTimer();
//    }
//    private void setNotifCTXcheck(){
//        if(mSetNotifCTX.get_isSend4ParamAns() && !mSetNotifCTX.get_ansSend4Param().isEmpty()){
//            Log.i("~~~~~NotifCTXf", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~NotifCTXf~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~try(" + mainLoopTry + ") delay: " + Math.floor(mainTimer.getCurrentDuration()));
//            mNotifregTimer.stopTimer();
//            String isOk = mSetNotifCTX.get_ansSend4Param();
//            if(isOk.equals("ok")){
//                periodPerocess = false;
//
//            }else if(isOk.equals("p4")){
//                periodPerocess = false;
//            }else if(isOk.equals("er")){
//                periodPerocess = false;
//
//            }else{
//                periodPerocess = false;
//            }
//            mSetNotifCTX.set_isSend4ParamAns(false);
//        }
//    }
//


    //endregion

}