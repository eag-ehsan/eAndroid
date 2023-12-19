package com.ourartag.eandroid;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
public class ViewImageFullscreen2 extends Fragment implements View.OnClickListener {
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private View view;
    private ImageButton cldBtn, saveBtnn, rotateBtnn;
    private ImageView imgfsr;
    private int currentAngle = 0;
    public ViewImageFullscreen2(String prr1) {
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
        rotateBtnn = (ImageButton) view.findViewById(R.id.rotate_fullsscreen_id);
        rotateBtnn.setOnClickListener(this);
        //imgfsr.setOnTouchListener((View.OnTouchListener) this);
        // This function will be called when
        // zoom out button is pressed

        Picasso.with(getContext())
                .load(mParam1)
                .into(imgfsr, new com.squareup.picasso.Callback() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onSuccess() {
                        imgfsr.setOnTouchListener(new View.OnTouchListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {

                                return false;
                            }});
                    }
                    @Override
                    public void onError() {
                    }
                });
        view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                //Log.e(TAG, "newDist===========================================================================================");
                ImageView view = imgfsr;
                view.setScaleType(ImageView.ScaleType.MATRIX);
                float scale;

                dumpEvent(event);
                // Handle touch events here...

                switch (event.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:   // first finger down only
                        matrix.set(view.getImageMatrix());
                        savedMatrix.set(matrix);
                        start.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;

                    case MotionEvent.ACTION_UP: // first finger lifted

                    case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                        mode = NONE;
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                        oldDist = spacing(event);
                        if (oldDist > 5f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (mode == DRAG)
                        {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                        }
                        else if (mode == ZOOM)
                        {
                            // pinch zooming
                            float newDist = spacing(event);
                            if (newDist > 5f)
                            {
                                matrix.set(savedMatrix);
                                scale = newDist / oldDist; // setting the scaling of the
                                // matrix...if scale > 1 means
                                // zoom in...if scale < 1 means
                                // zoom out
                                matrix.postScale(scale, scale, mid.x, mid.y);
                            }
                        }
                        break;
                }

                view.setImageMatrix(matrix); // display the transformation on screen


                return true;
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.rotate_fullsscreen_id){
            roateImage(imgfsr);
        }
        if(v.getId()==R.id.close_fuulscreen_id){
            FragmentManager fm = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.remove(ViewImageFullscreen2.this).commit();
            ((BrowsAllImages)getActivity()).turnOffFrag();
        }
        if(v.getId()==R.id.rotate_fullsscreen_id){
            roateImage(imgfsr);
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

    private void roateImage(ImageView imageView) {
        currentAngle +=90;
        if(currentAngle == 360){ currentAngle = 0;}
        Matrix matrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
        matrix.postRotate((float) currentAngle, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
        imageView.setImageMatrix(matrix);
    }


    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }
}