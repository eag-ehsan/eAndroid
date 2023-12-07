package com.ourartag.eandroid;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class myVolley {
    private static myVolley instance = null;
    public RequestQueue queue;
    private StringRequest stringRequest;
    private String myID, myStartUrl;
    private String mParam1, mParam2, mValue1, mValue2;
    private Boolean isSingleRequestAns, isSend2ParamAns;
    private String ansSingleRequest, ansSend2Param;

    public myVolley(Context context){
        // startUrl = "https://www.ourartag.com/live.php?liivx="

        isSingleRequestAns = false;
        ansSingleRequest = "";
        isSend2ParamAns = false;
        ansSend2Param = "";
        queue = Volley.newRequestQueue(context.getApplicationContext());
    }
    public static synchronized myVolley getInstance(Context context)
    {
        if (null == instance)
            instance = new myVolley(context);
        return instance;
    }
    public static synchronized myVolley getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(myVolley.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }


    public void setStarterUrl(String st1){
        myStartUrl = st1;
    }
    public void setmyID(String mii){
        myID = mii;
    }
    public void setParams(String p1, String p2){
        mParam1 = p1;
        mParam2 = p2;
    }
    public void setValues(String v1, String v2){
        mValue1 = v1;
        mValue2 = v2;
    }
    public void singleRequest(int outLenght) {
        String uurl = myStartUrl + myID;
        Log.i("singleRequest", "---------------url: " + uurl);

        StringRequest stringR = new StringRequest(Request.Method.GET, uurl,
                response -> {
                    String resp = response.substring(0, outLenght);
                    ansSingleRequest = resp;
                    isSingleRequestAns = true;
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ansSingleRequest = "verror";
                isSingleRequestAns = true;
            }
        });
        queue.add(stringR);
    }
    public void send2Params(int outLenght) {
        String uurl = myStartUrl + myID + "&" + mParam1 + "=" + mValue1 + "&" + mParam2 + "=" + mValue2;
        Log.i("send2Params", "---------------url: " + uurl);

        StringRequest stringR = new StringRequest(Request.Method.GET, uurl,
                response -> {
                    String resp = response.substring(0, outLenght);
                    ansSend2Param = resp;
                    isSend2ParamAns = true;
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ansSend2Param = "verror";
                isSend2ParamAns = true;
            }
        });
        queue.add(stringR);
    }
    public Boolean get_isSingleRequestAns(){
        return isSingleRequestAns;
    }
    public void set_isSingleRequestAns(Boolean vvv){
        isSingleRequestAns = vvv;
    }
    public String get_ansSingleRequest(){
        return ansSingleRequest;
    }


    public Boolean get_isSend2ParamAns(){
        return isSend2ParamAns;
    }
    public void set_isSend2ParamAns(Boolean vvv){
        isSend2ParamAns = vvv;
    }
    public String get_ansSend2Param(){
        return ansSend2Param;
    }




}
