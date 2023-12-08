package com.ourartag.eandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Button loginBtn, btnBack, dlgOk, signupBtn;
    private EditText txtNameLogin, txtNameSignup;
    private TextView resLogin, dialogMsg;
    private View view;
    private esTimer timer1000, timer1001;
    private myVolley myusr0;
    SharedPreferences sharedPref;
    SharedPreferences.Editor mEditor;
    private LinearLayout dlgPage;
    private Config myConfigs;
    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        myusr0 = new myVolley(getContext());
        sharedPref = getContext().getSharedPreferences("ourartag.com", Context.MODE_PRIVATE);
        mEditor = sharedPref.edit();
        myConfigs = new Config();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);
        signupBtn = (Button) view.findViewById(R.id.btnSignup);
        signupBtn.setOnClickListener(this);
        loginBtn = (Button) view.findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(this);
        btnBack = (Button) view.findViewById(R.id.dialogBack);
        btnBack.setOnClickListener(this);
        dlgPage = (LinearLayout) view.findViewById(R.id.dialogPage);
        dlgOk = (Button) view.findViewById(R.id.dialogOK);
        dlgOk.setOnClickListener(this);
        txtNameLogin = (EditText) view.findViewById(R.id.txtNameLogin);
        txtNameSignup = (EditText) view.findViewById(R.id.txtNameSignup);
        dialogMsg = (TextView) view.findViewById(R.id.vtxtDialogMsg);
        resLogin = (TextView) view.findViewById(R.id.vtxtDialogMsg);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.dialogBack){
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(Login.this).commit();
        }
        if(v.getId()==R.id.btnLogin){
            myusr0.setmyID("0");
            myusr0.setParams("name","spare");
            myusr0.setValues(txtNameLogin.getText().toString(), "spare");
            myusr0.setStarterUrl("https://www.ourartag.com/usr.php?liivx=");
            myusr0.send2Params(5);
            timer1000 = new esTimer(new Runnable() {public void run() {usrLoginCheck();}}, 1000, true);
        }
        if(v.getId()==R.id.dialogOK){
            dlgPage.setVisibility(View.GONE);
        }
        if(v.getId()==R.id.btnSignup){
            myusr0.setmyID("add");
            myusr0.setParams("name","spare");
            myusr0.setValues(txtNameSignup.getText().toString(), "spare");
            myusr0.setStarterUrl("https://www.ourartag.com/usr.php?liivx=");
            myusr0.send2Params(5);
            timer1001 = new esTimer(new Runnable() {public void run() {usrSignupCheck();}}, 1000, true);

        }
    }
    public void usrLoginCheck(){
        if(myusr0.get_isSend2ParamAns()){
            String strout = myusr0.get_ansSend2Param();
            String isOk = strout.substring(0, 2);
            if(isOk.equals("no")){
                resLogin.setText("This Name is not register to our servers, Please register through the section below and click Signup");
                dialogMsg.setText("This Name is not register to our servers, Please register through the section below and click Signup");
                dlgPage.setVisibility(View.VISIBLE);
                myusr0.set_isSend2ParamAns(false);
                timer1000.stopTimer();
                Log.i("LLOOGGIINN", "***************************************** no: ");
            }else if(isOk.equals("ok")){
                String isID = strout.substring(2);
                int tmpit = Integer.parseInt(isID);
                myConfigs.set("myID",tmpit + "");
                Log.i("LLOOGGIINN", "***************************************** myConfigs.set " + tmpit);
                myusr0.set_isSend2ParamAns(false);
                timer1000.stopTimer();
                dialogMsg.setText("Login is Ok, restart app to login automatically");
                dlgPage.setVisibility(View.VISIBLE);
                Log.i("LLOOGGIINN", "***************************************** >> " + strout);
            }else{

            }
        }
    }
    public void usrSignupCheck(){
        if(myusr0.get_isSend2ParamAns()){
            String strout = myusr0.get_ansSend2Param();
            String isOk = strout.substring(0, 2);
            if(isOk.equals("ex")){
                dialogMsg.setText("This Name '" + txtNameSignup.getText().toString() + "' already exist in the database, please select another one");
                dlgPage.setVisibility(View.VISIBLE);
                myusr0.set_isSend2ParamAns(false);
                timer1001.stopTimer();
                Log.i("LLOOGGIINN", "***************************************** no: ");
            }else if(isOk.equals("ok")){
                String isID = strout.substring(2);
                int tmpit = Integer.parseInt(isID);
                myConfigs.set("myID",tmpit + "");
                myConfigs.set("myName",txtNameSignup.getText().toString());
                myusr0.set_isSend2ParamAns(false);
                timer1001.stopTimer();
                dialogMsg.setText("Signup and Login is Ok, restart app to login automatically");
                dlgPage.setVisibility(View.VISIBLE);
                Log.i("LLOOGGIINN", "***************************************** >> " + strout);
            }else if(isOk.equals("er")){
                dialogMsg.setText(" Connecting to Server Failed. Please check you internet and try again");
                dlgPage.setVisibility(View.VISIBLE);
                myusr0.set_isSend2ParamAns(false);
                timer1001.stopTimer();
                Log.i("LLOOGGIINN", "***************************************** >> er");
            }else{

            }
        }
    }
}