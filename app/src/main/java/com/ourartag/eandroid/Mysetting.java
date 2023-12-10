package com.ourartag.eandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Mysetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Mysetting extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2, myServerAddress;
    private Button btnSetting_Save, btnSetting_Cancel;
    private View view;
    private Switch sw;
    Config myConfigs;
    EditText serverAsTSave;

    public Mysetting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Mysetting.
     */
    // TODO: Rename and change types and number of parameters
    public static Mysetting newInstance(String param1, String param2) {
        Mysetting fragment = new Mysetting();
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
        myConfigs = new Config();
        myServerAddress = myConfigs.get("myServerAddress", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mysetting, container, false);
        btnSetting_Save = (Button) view.findViewById(R.id.setting_save_btn);
        btnSetting_Save.setOnClickListener(this);
        btnSetting_Cancel = (Button) view.findViewById(R.id.setting_cancel_btn);
        btnSetting_Cancel.setOnClickListener(this);
        serverAsTSave = (EditText) view.findViewById(R.id.setting_server_address_id);
        serverAsTSave.setText(myServerAddress);
        sw = (Switch) view.findViewById(R.id.setting_bg_service);


//        if(myConfigs.get("bgService","").equals("on")){
//            sw.setChecked(true);
//        }else{
//            sw.setChecked(false);
//        }
        if(myConfigs.get("bgService","").equals("on")){
            sw.setChecked(true);
        }else{
            sw.setChecked(false);
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myConfigs.set("bgService","on");
                    //myConfigs.set("isLogin","1");
                    //Log.i("$$$$$$$$$$$$", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ isLogin 1");
                } else {
                    myConfigs.set("bgService","off");
                    //myConfigs.set("isLogin","0");
                    //myConfigs.set("myPass","0");
                    //Log.i("$$$$$$$$$$$$", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ isLogin 0");
                }
            }
        });
        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.setting_cancel_btn){
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(Mysetting.this).commit();
            ((MainActivity)getActivity()).turnOffFrag();
        }
        if(v.getId()==R.id.setting_save_btn){
            myConfigs.set("myServerAddress",serverAsTSave.getText().toString());
            if(myConfigs.get("bgService","off").equals("off")){
                ((MainActivity)getActivity()).stopMyService();
            }else{
                ((MainActivity)getActivity()).sureServiceisStarting();
            }

            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(Mysetting.this).commit();
            ((MainActivity)getActivity()).turnOffFrag();
        }
    }
}