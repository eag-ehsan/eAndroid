package com.ourartag.eandroid;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BrowseImageForSend extends AppCompatActivity {
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);
    protected final BetterActivityResult<Intent, ActivityResult> activityLauncher2 = BetterActivityResult.registerActivityForResult(this);
    private static final int REQUEST_IMAGE = 100;
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    TextView tvPath;
    TextView txtHaut;
    ImageView preview, mImgfromurl;
    Bitmap FixBitmap;
    File destination;
    String imagePath;
    ImageButton takePhoto;
    Button btn_b_brows, btn_n_send, btn_b_back;
    Config myConfigs;
    myVolley mSetChat;
    esTimer mSetChatTimer;
    int serverResponseCode = 0, tryToSendChat = 3, countertrysend = 0;
    ProgressDialog dialog = null;
    private int GALLERY = 1, CAMERA = 2;
    String upLoadServerUri = null, myServerAddress;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void sendChatToServer(String txtMessage) {
        mSetChat.setParams("msg", "", "", "");
        mSetChat.setValues(txtMessage, "", "", "");
        mSetChat.send1Params(2);
        mSetChatTimer.startTimer();
        countertrysend = 0;
    }

    private void sendChatToServerCheck() {
        if (mSetChat.get_isSend1ParamAns() && !mSetChat.get_ansSend1Param().isEmpty()) {
            Log.i("~~~~~~sendChat", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~sendChat~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            mSetChatTimer.stopTimer();
            String isOk = mSetChat.get_ansSend1Param();
            if (isOk.equals("ok")) {
                mSetChat.set_isSend1ParamAns(false);
                Intent myIntent = new Intent(BrowseImageForSend.this, MainActivity.class);
                myIntent.putExtra("key", "1"); //Optional parameters
                BrowseImageForSend.this.startActivity(myIntent);
            } else {
                Log.i("~~~~~~sendChat", "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Chat Fail~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                mSetChat.set_isSend1ParamAns(false);

                countertrysend++;
                if (countertrysend < tryToSendChat) {
                    mSetChat.send1Params(2);
                    mSetChatTimer.startTimer();
                } else {

                }

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_image_for_send);

        mContext = this;
        myConfigs = new Config(this);
        myServerAddress = myConfigs.get_myServerAddress();
        upLoadServerUri = myServerAddress + "upimg.php";
        btn_b_back = findViewById(R.id.btn_sback_browsimg_id);
        btn_b_brows = findViewById(R.id.btn_brows_browsimg_id);
        btn_n_send = findViewById(R.id.btn_send_browsimg_id);
        preview = findViewById(R.id.img_send_browsimg_id);
        mSetChat = new myVolley(this);
        mSetChat.setmyID(myConfigs.get_myID());
        mSetChat.setStarterUrl(myConfigs.get_myServerAddress() + "setchat.php?liivx=");
        mSetChatTimer = new esTimer(new Runnable() {
            public void run() {
                sendChatToServerCheck();
            }
        }, 10, 1000, false);
        btn_b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_b_brows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
        btn_n_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(BrowseImageForSend.this, "", "Uploading file...", true);

                new Thread(new Runnable() {
                    public void run() {
                        String tmyID = myConfigs.get_myID();
                        uploadFile(tmyID);

                    }
                }).start();
            }
        });
        int permission = ActivityCompat.checkSelfPermission(BrowseImageForSend.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    BrowseImageForSend.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        int permissionread = ActivityCompat.checkSelfPermission(BrowseImageForSend.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionread != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    BrowseImageForSend.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        if (checkPermission()) {
            showPictureDialog();

        } else {
            requestPermission();
            showPictureDialog();
        }

    }
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BrowseImageForSend.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Photo Gallery",
                "Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //choosePhotoFromGallary();
                                openGalleryActivityForResult();
                                break;
                            case 1:
                                //takePhotoFromCamera();
                                openCameraActivityForResult();
                                //choosePhotoFromGallary();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        startActivityForResult(galleryIntent, GALLERY);
        //startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        //startActivityForResult(intent, REQUEST_IMAGE);
        startActivityForResult(intent, CAMERA);
    }
    public void openGalleryActivityForResult() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activityLauncher2.launch(intent, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                Intent data = result.getData();
                if (data != null) {
                    Uri contentURI = data.getData();
                    String FileImagePath = getPath(contentURI);
                    assert FileImagePath != null;
                    destination = new File(FileImagePath);
                    try {
                        preview.setVisibility(View.VISIBLE);
                        FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                        preview.setImageBitmap(FixBitmap);
                        imagePath = destination.getAbsolutePath();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
        });
    }
    public void openCameraActivityForResult() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


        activityLauncher.launch(intent, result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // There are no request codes
                preview.setVisibility(View.VISIBLE);
                Intent data = result.getData();
                FixBitmap = (Bitmap) data.getExtras().get("data");
                preview.setImageBitmap(FixBitmap);
            }
        });
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
                String FileImagePath = getPath(contentURI);
                assert FileImagePath != null;
                destination = new File(FileImagePath);
                try {
                    preview.setVisibility(View.VISIBLE);
                    FixBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    preview.setImageBitmap(FixBitmap);
                    imagePath = destination.getAbsolutePath();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } else if (requestCode == CAMERA) {
            FixBitmap = (Bitmap) data.getExtras().get("data");
            preview.setImageBitmap(FixBitmap);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public int uploadFile(String hereMyid) {

        //String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        //File sourceFile = new File(sourceFileUri);

        try {

            // open a URL connection to the Servlet
            //FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            String filenametosend = hereMyid + "_" + System.currentTimeMillis() + ".jpg";
            conn.setRequestProperty("uploaded_file", filenametosend);
            Log.e("filename", "::::::::::::::::::::::::::::::::::::::::::::::::::::" + filenametosend);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + filenametosend + "" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            //bytesAvailable = fileInputStream.available();

            //bufferSize = Math.min(bytesAvailable, maxBufferSize);
            //buffer = new byte[bufferSize];

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FixBitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            byte[] mybytesRead = bos.toByteArray();
            // read file and write it into form...
            //bytesRead = fileInputStream.read(buffer, 0, bufferSize);

//                while (bytesRead > 0) {
//
//                    dos.write(buffer, 0, bufferSize);
//                    bytesAvailable = fileInputStream.available();
//                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
//
//                }

            // send multipart form data necesssary after file data...
            dos.write(mybytesRead);
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
                        int temi = Integer.valueOf(hereMyid);
                        String temp2 = "";
                        if (temi < 10)
                            temp2 = "00" + temi;
                        else if (temi < 100)
                            temp2 = "0" + temi;
                        else
                            temp2 = "" + temi;
                        sendChatToServer("IMG " + temp2 + " " + filenametosend);
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

                    Toast.makeText(BrowseImageForSend.this, "Got Exception : see logcat ",
                            Toast.LENGTH_SHORT).show();
                }
            });
            Log.e("Upload file to server Exception", "Exception : "
                    + e.getMessage(), e);
        }
        dialog.dismiss();
        return serverResponseCode;


    }
}