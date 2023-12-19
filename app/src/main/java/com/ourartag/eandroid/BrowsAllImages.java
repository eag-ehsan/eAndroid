package com.ourartag.eandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class BrowsAllImages extends AppCompatActivity {
    ArrayList<Model> mListAll;
    RecyclerView mRecyclerViewAll;
    MultiViewTypeAdapter adapterAll;
    myVolley mGetFiles;
    esTimer mGetFilesTimer;
    Config myConfigs;
    Boolean filegetPerocess;
    String myServerAddress;
    FragmentContainerView myFrag1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brows_all_images);
        myFrag1 = findViewById(R.id.fragmentContainerView2);
        myConfigs = new Config(this);
        mGetFiles = new myVolley(this);
        myServerAddress = myConfigs.get_myServerAddress();
        mGetFilesTimer = new esTimer(new Runnable() {public void run() {getFilesRequestCheck();}}, 10, 1000, false);
        mGetFiles.setmyID(myConfigs.get_myID());
        mGetFiles.setStarterUrl(myServerAddress + "getfiles.php?liivx=");
        filegetPerocess = false;

        mListAll= new ArrayList();
//        mList.add(new Model(Model.TEXT_TYPE, R.drawable.d4, "Ehsan Ahmadi", getString(R.string.lng_login_txt1), ""));
//        mList.add(new Model(Model.IMAGE_TYPE,R.drawable.d4, "Dorsa", "", myServerAddress + "upload/2_1702396287700.jpg"));
//        mList.add(new Model(Model.AUDIO_TYPE, R.raw.fart, "Dorsa", "", ""));


        adapterAll = new MultiViewTypeAdapter(mListAll,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        adapterAll.setOnItemClickListener(new RecyclerViewClickListener(){



            @Override
            public void recyclerViewListClicked(View v, int position, String chatTitle) {

            }

            @Override
            public void rVClicked_delete(View v, int position, int chatID) {

            }

            @Override
            public void rVClicked_reply(View v, int position, int chatID, String chatTitle, String chatMsgFirstLine) {

            }

            @Override
            public void rVClicked_openImage(View v, int position, String imageUrl) {
                if(!imageUrl.isEmpty()){
                    myFrag1.setVisibility(View.VISIBLE);
                    Fragment mViewImageFullscreen = new ViewImageFullscreen2(imageUrl);
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView2, mViewImageFullscreen);
                    fragmentTransaction.commit();
                }
            }


        });
        mRecyclerViewAll = (RecyclerView) findViewById(R.id.recyclerView2);
        mRecyclerViewAll.setLayoutManager(linearLayoutManager);
        mRecyclerViewAll.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewAll.setAdapter(adapterAll);

        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mRecyclerViewAll.setLayoutManager(manager);
        getFilesRequest();
    }
    public void turnOffFrag(){
        myFrag1.setVisibility(View.GONE);
    }
    private void getFilesRequest(){
        mGetFiles.singleRequest(2068);
        mGetFilesTimer.startTimer();


    }
    private void getFilesRequestCheck(){
        if(mGetFiles.get_isSingleRequestAns() && !mGetFiles.get_ansSingleRequest().isEmpty()){
            mGetFilesTimer.stopTimer();
            String completeresp = mGetFiles.get_ansSingleRequest();
            if(!completeresp.startsWith("sqError")){

                String withoutHtmlHeader = completeresp.substring(47);
                if(withoutHtmlHeader.startsWith("ok")){
                    String justChatsWithendBody = withoutHtmlHeader.substring(2);
                    int b = justChatsWithendBody.indexOf("</body>") ;
                    String justFiles = justChatsWithendBody.substring(0, b);

                    String[] echitems = justFiles.split(";");
                    int ii=0, hh=0;

                    removeAllItemsFromListview();
                    for(ii=(echitems.length-1);ii>=0;ii--){
                        String tempstrName = echitems[ii];

                        setChatToRecyclerview(tempstrName);


                    }
                }

            }
            mGetFiles.set_isSingleRequestAns(false);


        }

    }
    public void setChatToRecyclerview(String txtfilen) {
        Boolean temp_itischat = false;
        //Log.e("in","---------------------------" + txtMessage);
        String[] findUserId = txtfilen.split("_");
        int userIddd = Integer.parseInt(findUserId[0]);
        mListAll.add(new Model(false, Model.IMAGE_TYPE, R.drawable.d4, "_", txtfilen, "",myServerAddress +  "upload/" + txtfilen, userIddd,0, ""));
        mRecyclerViewAll.setAdapter(adapterAll);
        //mRecyclerView.smoothScrollToPosition(mList.size()-1);
        //mRecyclerViewAll.scrollToPosition(mListAll.size()-1);

    }
    public void removeAllItemsFromListview(){
        mListAll.clear();
        mRecyclerViewAll.setAdapter(adapterAll);
    }

}