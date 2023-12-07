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

    private Properties configuration;
    private String configurationFile;

    private String pathapp ;
    public Config() {
        configuration = new Properties();
        pathapp =  "/data/data/com.ourartag.eandroid/" ;
        configurationFile = pathapp + "config.ini";
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

    public void set(String key, String value) {
        configuration.setProperty(key, value);
    }

    public String get(String key, String defaulti) {
        String tmpconfig = configuration.getProperty(key);
        if(tmpconfig==null){
            return defaulti;
        }else{
            return tmpconfig;
        }
    }
}
