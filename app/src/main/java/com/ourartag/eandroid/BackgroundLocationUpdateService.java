package com.ourartag.eandroid;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundLocationUpdateService  extends Service {
    private NotificationCompat.Builder mBuilder;
    private String NOTIFICATION_CHANNEL_ID = "eAndroid";
    private boolean stopService;
    private String channelName, myID, myMan, myServerAddress;
    int SERVICE_MAIN_LOOP_S;
    SharedPreferences sharedPref;
    SharedPreferences.Editor mEditor;
    Config myConfigs;
    private myVolley mLivelog;
    private esTimer myTtimer, mLivelogTimer;
    private Context context;
    Boolean periodPerocess;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        myServerAddress = myConfigs.get_myServerAddress();
        startTimer();

        setnotif2("eAndroid - Ehsan Ahmadi", "Chat Notification is ON", 2727);

        return START_STICKY;
    }
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        stopService = false;


        if(myConfigs.get_bgService().equals("on")){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SERVICE_MAIN_LOOP_S = 4;
        stopService = false;
        channelName = getString(R.string.site_Address_quick);
        mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        sharedPref = context.getSharedPreferences(getString(R.string.site_Address_quick), Context.MODE_PRIVATE);
        mEditor = sharedPref.edit();
        myID = sharedPref.getString("myID", "0");
        myConfigs = new Config(this);
        if(myID.equals("0")){
            myID = myConfigs.get_myID();
        }

        myMan = sharedPref.getString("man", "");
        myTtimer = new esTimer(new Runnable() {public void run() {runrun();}}, 10, SERVICE_MAIN_LOOP_S*1000, false);
        mLivelogTimer = new esTimer(new Runnable() {public void run() {mLivelogResponseCheck();}}, 10, 1000, false);
        mLivelog = new myVolley(this);
        mLivelog.setmyID(myID);
        myServerAddress = myConfigs.get_myServerAddress();
        mLivelog.setStarterUrl(myServerAddress + "livee.php?liivx=");
        periodPerocess = false;

    }

    private void setnotif(String MainText1, String headd, int notID) {


        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLightColor(ContextCompat.getColor(this, R.color.okcolor));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }
        Intent resultIntent = new Intent(this,MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT);
        Notification notification = mBuilder.setOngoing(true)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.drawable.ee)
                .setContentTitle(MainText1)
                .setContentText(headd)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(MainText1))
                //mBuilder.setColor(getResources().getColor(R.color.adminnotif, getBaseContext().getTheme()));
                .setPriority(NotificationCompat.PRIORITY_HIGH).build();
// notificationID allows you to update the notification later on.
        startForeground(notID, notification);
        //stopForeground()
    }
    private void setnotif2(String MainText1, String headd, int notID) {


        NotificationChannel chan = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLightColor(ContextCompat.getColor(this, R.color.okcolor));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(chan);
        }
        //Intent notificationIntent = new Intent(this, MainActivity.class);
        Intent resultIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, PendingIntent.FLAG_MUTABLE);


        Notification notification = mBuilder.setOngoing(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ee)
                .setContentTitle(MainText1)
                .setContentText(headd)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(MainText1))
                //mBuilder.setColor(getResources().getColor(R.color.adminnotif, getBaseContext().getTheme()));
                .setPriority(NotificationCompat.PRIORITY_HIGH).build();
        startForeground(notID, notification);


    }
    public void startTimer() {
        if(stopService){
            return;
        }else{
            stopService = true;
        }
        myTtimer.startTimer();
    }
    public void runrun(){
        if(myConfigs.get_bgService().equals("off")){
            myTtimer.stopTimer();
        }
        myMan = sharedPref.getString("man", "");
        Log.i("##########", "####################################" + myMan);
        if(myMan.equals("raftam")){
            if(!periodPerocess){

                mLivelog.singleRequest(3);
                mLivelogTimer.startTimer();
            }
        }
    }
    private void mLivelogResponseCheck(){
        if(mLivelog.get_isSingleRequestAns() && !mLivelog.get_ansSingleRequest().isEmpty()){
            mLivelogTimer.stopTimer();
            String isOk = mLivelog.get_ansSingleRequest();
            if(isOk.equals("sqE")){

                Log.i("########single", "################################## sqE ################################");
                periodPerocess = false;
            }else if(isOk.equals("err")){
                periodPerocess = false;
            }else {
                String newchat = isOk.substring(2);

                if(newchat.equals("1")){
                    playAlarmSound();
                }
                periodPerocess = false;
            }
            mLivelog.set_isSingleRequestAns(false);
        }
    }
    public void playAlarmSound()  {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
    }
    public void stoptimertask() {
        myTtimer.stopTimer();
    }


}
