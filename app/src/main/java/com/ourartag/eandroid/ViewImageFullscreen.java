package com.ourartag.eandroid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewImageFullscreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewImageFullscreen extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private View view;
    private ImageButton cldBtn, saveBtnn;
    private ImageView imgfsr;

    public ViewImageFullscreen(String prr1) {
        mParam1 = prr1;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     *
     * @return A new instance of fragment ViewImageFullscreen.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewImageFullscreen newInstance(String param1) {
        ViewImageFullscreen fragment = new ViewImageFullscreen(param1);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_image_fullscreen, container, false);
        cldBtn = (ImageButton) view.findViewById(R.id.close_fuulscreen_id);
        cldBtn.setOnClickListener(this);
        imgfsr = (ImageView) view.findViewById(R.id.img_fullscreen_id);
        saveBtnn = (ImageButton) view.findViewById(R.id.save_fullsscreen_id);
        saveBtnn.setOnClickListener(this);
        Picasso.with(getContext()).load(mParam1).into(imgfsr);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.close_fuulscreen_id){
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(ViewImageFullscreen.this).commit();
            ((MainActivity)getActivity()).turnOffFrag();
        }
        if(v.getId()==R.id.save_fullsscreen_id){
            BitmapDrawable draw = (BitmapDrawable) imgfsr.getDrawable();
            Bitmap bitmap = draw.getBitmap();

            FileOutputStream outStream = null;

            String folder_main = "Ourartag";

            File f = new File(Environment.getExternalStorageDirectory(), folder_main);
            if (!f.exists()) {
                f.mkdirs();
            }
            String ttp = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM + "/" + folder_main;
            File dir = new File(ttp);
            Log.e("pp",ttp);
            dir.mkdirs();
            String fileName = String.format("%d.jpg", System.currentTimeMillis());
            File outFile = new File(dir, fileName);
            try {
                outStream = new FileOutputStream(outFile);

            } catch (FileNotFoundException e) {
                Toast.makeText(getContext(), getString(R.string.lng_err_save_img), Toast.LENGTH_LONG).show();
                throw new RuntimeException(e);

            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            try {
                outStream.flush();
                outStream.close();
                Toast.makeText(getContext(), getString(R.string.lng_ok_save_img), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), getString(R.string.lng_err_save_img), Toast.LENGTH_LONG).show();
                throw new RuntimeException(e);
            }


        }
    }
}