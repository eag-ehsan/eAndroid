package com.ourartag.eandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
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
    private String mParam2, mParam1, loginproc, myServerAddress;
    private Button loginBtn, btnBack, dlgOk, signupBtn;
    private EditText txtNameLogin, txtNameSignup, txtPassLogin, txtPassSignup;
    private TextView resLogin, dialogMsg, titleLogin;
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
        myConfigs = new Config();
        myServerAddress = myConfigs.get("myServerAddress", "");
        loginproc = "";
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
        txtPassLogin = (EditText) view.findViewById(R.id.txtPassLogin);
        txtPassSignup = (EditText) view.findViewById(R.id.txtPassSignup);
        dialogMsg = (TextView) view.findViewById(R.id.vtxtDialogMsgLogin);
        resLogin = (TextView) view.findViewById(R.id.vtxtDialogMsg);
        titleLogin = (TextView) view.findViewById(R.id.textView);
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
            if(txtNameLogin.getText().toString().isEmpty() || txtPassLogin.getText().toString().isEmpty()){
                dialogMsg.setText(getString(R.string.lng_login_txt8));
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
                dlgPage.setVisibility(View.VISIBLE);
            }else{
                myusr0.setmyID("0");
                myusr0.setParams("name","pass", "", "");
                myusr0.setValues(txtNameLogin.getText().toString(), txtPassLogin.getText().toString(), "", "");
                myusr0.setStarterUrl(getString(R.string.site_Address) + "usr.php?liivx=");
                myusr0.send2Params(5);
                loginBtn.setText(getString(R.string.lng_login_txt7));
                loginBtn.setEnabled(false);
                signupBtn.setText(getString(R.string.lng_login_txt7));
                signupBtn.setEnabled(false);

                timer1000 = new esTimer(new Runnable() {public void run() {usrLoginCheck();}}, 10, 1000, true);
            }

        }
        if(v.getId()==R.id.dialogOK){
            if(loginproc.equals("lgnok")){
                dlgPage.setVisibility(View.GONE);
                ((MainActivity)getActivity()).loginInit();
                FragmentManager fm = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.remove(Login.this).commit();
                ((MainActivity)getActivity()).turnOffFrag();
            }else if(loginproc.equals("sgnok")){
                txtNameLogin.setText(txtNameSignup.getText().toString());
                txtPassLogin.setText(txtPassSignup.getText().toString());
                txtNameSignup.setText("");
                txtPassSignup.setText("");
                titleLogin.setText(getString(R.string.lng_login_txt9));
                resLogin.setText(getString(R.string.lng_login_txt10));
                dlgPage.setVisibility(View.GONE);

            }else{
                dlgPage.setVisibility(View.GONE);
            }

        }
        if(v.getId()==R.id.btnSignup){
            if(txtNameSignup.getText().toString().isEmpty() || txtPassSignup.getText().toString().isEmpty()){
                dialogMsg.setText(getString(R.string.lng_login_txt8));
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
                dlgPage.setVisibility(View.VISIBLE);
            }else{
                myusr0.setmyID("add");
                myusr0.setParams("name","pass", "", "");
                myusr0.setValues(txtNameSignup.getText().toString(), txtPassSignup.getText().toString(), "", "");
                myusr0.setStarterUrl(getString(R.string.site_Address) + "usr.php?liivx=");
                myusr0.send2Params(5);
                loginBtn.setText(getString(R.string.lng_login_txt6));
                loginBtn.setEnabled(false);
                signupBtn.setText(getString(R.string.lng_login_txt6));
                signupBtn.setEnabled(false);
                timer1001 = new esTimer(new Runnable() {public void run() {usrSignupCheck();}}, 10, 1000, true);
            }


        }
    }
    public void usrLoginCheck(){
        if(myusr0.get_isSend2ParamAns()){
            String isOk = myusr0.get_ansSend2Param();
            if(isOk.equals("no000")){
                resLogin.setText(getString(R.string.lng_login_txt6));
                dialogMsg.setText(getString(R.string.lng_login_txt6));
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
                loginproc = "nousr";
                dlgPage.setVisibility(View.VISIBLE);
                myusr0.set_isSend2ParamAns(false);
                timer1000.stopTimer();
                loginBtn.setText(getString(R.string.lng_Login));
                loginBtn.setEnabled(true);
                signupBtn.setText(getString(R.string.lng_Signup));
                signupBtn.setEnabled(true);
            }else if(isOk.equals("ps000")){
                resLogin.setText(getString(R.string.lng_login_txt5) + " " + txtNameLogin.getText().toString());
                dialogMsg.setText(getString(R.string.lng_login_txt5) + " " + txtNameLogin.getText().toString());
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
                loginproc = "psinv";
                dlgPage.setVisibility(View.VISIBLE);
                myusr0.set_isSend2ParamAns(false);
                timer1000.stopTimer();
                loginBtn.setText(getString(R.string.lng_Login));
                loginBtn.setEnabled(true);
                signupBtn.setText(getString(R.string.lng_Signup));
                signupBtn.setEnabled(true);

            }else if(isOk.substring(0,2).equals("ok")){
                String isID = isOk.substring(2);
                int tmpit = Integer.parseInt(isID);
                myConfigs.set("myID",tmpit + "");
                myConfigs.set("myName",txtNameLogin.getText().toString());
                myusr0.set_isSend2ParamAns(false);
                timer1000.stopTimer();
                dialogMsg.setText(getString(R.string.lng_login_txt4));
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.okcolor));
                loginproc = "lgnok";
                dlgPage.setVisibility(View.VISIBLE);


            }else{

            }
        }
    }
    public void usrSignupCheck(){
        if(myusr0.get_isSend2ParamAns()){
            String isOk = myusr0.get_ansSend2Param();
            if(isOk.equals("ex000")){
                dialogMsg.setText(txtNameSignup.getText().toString() + " " + getString(R.string.lng_login_txt1));
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
                loginproc = "exusr";
                dlgPage.setVisibility(View.VISIBLE);
                myusr0.set_isSend2ParamAns(false);
                timer1001.stopTimer();
                loginBtn.setText(getString(R.string.lng_Login));
                loginBtn.setEnabled(true);
                signupBtn.setText(getString(R.string.lng_Signup));
                signupBtn.setEnabled(true);
            }else if(isOk.equals("ok000")){
                myusr0.set_isSend2ParamAns(false);
                timer1001.stopTimer();
                dialogMsg.setText(getString(R.string.lng_login_txt2));
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.okcolor));
                loginproc = "sgnok";
                dlgPage.setVisibility(View.VISIBLE);
                loginBtn.setText(getString(R.string.lng_Login));
                loginBtn.setEnabled(true);
                signupBtn.setText(getString(R.string.lng_Signup));
                signupBtn.setEnabled(true);

            }else if(isOk.equals("ee000") || isOk.equals("er000")){
                dialogMsg.setText(getString(R.string.lng_login_txt3));
                dialogMsg.setTextColor(ContextCompat.getColor(getContext(), R.color.errcolo));
                dlgPage.setVisibility(View.VISIBLE);
                myusr0.set_isSend2ParamAns(false);
                timer1001.stopTimer();
                loginBtn.setText(getString(R.string.lng_Login));
                loginBtn.setEnabled(true);
                signupBtn.setText(getString(R.string.lng_Signup));
                signupBtn.setEnabled(true);
            }else{

            }
        }
    }
}