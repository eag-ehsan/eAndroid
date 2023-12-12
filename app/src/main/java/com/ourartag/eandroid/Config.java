package com.ourartag.eandroid;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Ananti
 */
public class Config {

    private String myID, myName, myServerAddress, mMan, myPass, myLati, myLoni, bgService;
    private Properties configuration;
    private String configurationFile;
    private Context mContext;

    private String pathapp ;
    public Config(Context context) {
        mContext = context;
        configuration = new Properties();
        pathapp =  "/data/data/com.ourartag.eandroid/" ;
        configurationFile = pathapp + "config.ini";
        myID = "";
        myName = "";
        myServerAddress = "";
        mMan = "";
        myPass = "";
        myLati = "";
        myLoni = "";
        bgService = "";
        refresh();
    }
    public void refresh(){
        myID = get("myID","0");
        myName = get("myName","");
        myServerAddress = get("myServerAddress",mContext.getString(R.string.site_Address));
        mMan = get("man","");
        myPass = get("myPass","");
        myLati = get("myLati","");
        myLoni = get("myLoni","");
        bgService = get("bgService","");
    }
    public String get_myID(){
        myID = get("myID","0");
        return myID;
    }
    public String get_myName(){
        myName = get("myName","");
        return myName;
    }
    public String get_myPass(){
        myPass = get("myPass","");
        return myPass;
    }
    public String get_bgService(){
        bgService = get("bgService","");
        return bgService;
    }
    public String get_myServerAddress(){
        myServerAddress = get("myServerAddress",mContext.getString(R.string.site_Address));
        return myServerAddress;
    }
    public String get_myLati(){
        myLati = get("myLati","");
        return myLati;
    }
    public String get_myLoni(){
        myLoni = get("myLoni","");
        return myLoni;
    }
    public String get_man(){
        mMan = get("man","");
        return mMan;
    }


    public void set_myID(String strval){
        myID = strval;
        set("myID", strval);
    }
    public void set_myName(String strval){
        myName = strval;
        set("myName", strval);
    }
    public void set_myPass(String strval){
        myPass = strval;
        set("myPass", strval);
    }
    public void set_man(String strval){
        mMan = strval;
        set("man", strval);
    }
    public void set_myLati(String strval){
        myLati = strval;
        set("myLati", strval);
    }
    public void set_myLoni(String strval){
        myLoni = strval;
        set("myLoni", strval);
    }
    public void set_bgService(String strval){
        bgService = strval;
        set("bgService", strval);
    }
    public void set_myServerAddress(String strval){
        myServerAddress = strval;
        set("myServerAddress", strval);
    }




    public boolean load() {
        boolean retval = false;

        try {
            configuration.load(new FileInputStream(this.configurationFile));
            retval = true;
        } catch (IOException e) {
            System.out.println("Configuration error: " + e.getMessage());
        }

        return retval;
    }

    public boolean store() {
        boolean retval = false;

        try {
            configuration.store(new FileOutputStream(this.configurationFile), null);
            retval = true;
        } catch (IOException e) {
            System.out.println("Configuration error: " + e.getMessage());
        }

        return retval;
    }

    private void set(String key, String value) {
        configuration.setProperty(key, value);
        store();
    }

    private String get(String key, String defaulti) {
        load();
        String tmpconfig = configuration.getProperty(key);
        if(tmpconfig==null){
            return defaulti;
        }else{
            return tmpconfig;
        }
    }
}
