package com.ourartag.eandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Mysetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Mysetting extends Fragment implements View.OnClickListener {



    //region for image upload
    Button GetImageFromGalleryButton, UploadImageOnServerButton;

    ImageView ShowSelectedImage;

    EditText imageName;

    Bitmap FixBitmap;

    String ImageTag = "image_tag" ;

    String ImageName = "image_data" ;

    ProgressDialog progressDialog ;

    ByteArrayOutputStream byteArrayOutputStream ;

    byte[] byteArray ;

    String ConvertImage ;

    String GetImageNameFromEditText;

    HttpURLConnection httpURLConnection ;

    URL url;

    OutputStream outputStream;

    BufferedWriter bufferedWriter ;

    int RC ;

    BufferedReader bufferedReader ;

    StringBuilder stringBuilder;

    boolean check = true;

    private int GALLERY = 1, CAMERA = 2;

    //endregion




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

    private String mFileImagePath;
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
        myConfigs = new Config(getContext());
        myServerAddress = myConfigs.get_myServerAddress();



    }

    @SuppressLint("WrongThread")
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
        if(myConfigs.get_bgService().equals("on")){
            sw.setChecked(true);
        }else{
            sw.setChecked(false);
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    myConfigs.set_bgService("on");
                    //myConfigs.set("isLogin","1");
                    //Log.i("$$$$$$$$$$$$", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ isLogin 1");
                } else {
                    myConfigs.set_bgService("off");
                    //myConfigs.set("isLogin","0");
                    //myConfigs.set("myPass","0");
                    //Log.i("$$$$$$$$$$$$", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ isLogin 0");
                }
            }
        });
        // Inflate the layout for this fragment









        //region for image upload
        GetImageFromGalleryButton =(Button) view.findViewById(R.id.buttonSelect);

        UploadImageOnServerButton = (Button) view.findViewById(R.id.buttonUpload);

        ShowSelectedImage = (ImageView) view.findViewById(R.id.imageView);

        imageName= (EditText) view.findViewById(R.id.imageName);

        byteArrayOutputStream = new ByteArrayOutputStream();

        GetImageFromGalleryButton.setOnClickListener(this);


        UploadImageOnServerButton.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        5);
            }
        }

        //endregion

        return view;

    }

    @Override
    public void onClick(View v) {

        //region for image upload
        if(v.getId()==R.id.buttonSelect){
            showPictureDialog();
        }
        if(v.getId()==R.id.buttonUpload){
            GetImageNameFromEditText = imageName.getText().toString();

            try {
                UploadImageToServer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        //endregion

        if(v.getId()==R.id.setting_cancel_btn){
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(Mysetting.this).commit();
            ((MainActivity)getActivity()).turnOffFrag();
        }
        if(v.getId()==R.id.setting_save_btn){
            myConfigs.set_myServerAddress(serverAsTSave.getText().toString());
            if(myConfigs.get_bgService().equals("off")){
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


    //region for image upload
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                mFileImagePath = contentURI.getPath();
                Log.i("pppppppppp", "popopopoopopooopo" + mFileImagePath);
                try {
                    FixBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), contentURI);
                    // String path = saveImage(bitmap);
                    //Toast.makeText(MainActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    ShowSelectedImage.setImageBitmap(FixBitmap);
                    UploadImageOnServerButton.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            ShowSelectedImage.setImageBitmap(FixBitmap);
            UploadImageOnServerButton.setVisibility(View.VISIBLE);
            //  saveImage(thumbnail);
            //Toast.makeText(ShadiRegistrationPart5.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }
    public void UploadImageToServer() throws IOException {

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //GetImageNameFromEditText

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }
            else {

                Toast.makeText(getContext(), "Unable to use Camera..Please Allow us to use Camera", Toast.LENGTH_LONG).show();

            }
        }
    }

    //endregion

}