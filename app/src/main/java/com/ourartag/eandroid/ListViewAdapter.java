package com.ourartag.eandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<Item> {
    ArrayList<Item> mitems;
    Context mContext;
    public static int IMAGE_MAX_WIDTH=1024;
    public static int IMAGE_MAX_HEIGHT=768;
    ListViewAdapter(@NonNull Context context, ArrayList<Item> items) {
        super(context, 0, items);
        mContext = context;
        this.mitems = items;
    }
    @SuppressLint("WrongConstant")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
        }

        // Get the {@link Word} object located at this position in the list
        Item currentItem = getItem(position);
        LinearLayout rootV = listItem.findViewById(R.id.rootView);
        LinearLayout zirerootV = listItem.findViewById(R.id.zereroot);
        LinearLayout zireroo2 = listItem.findViewById(R.id.zereroot2);
        ImageView picture = listItem.findViewById(R.id.IvPicture);
        ImageView bigpicture = listItem.findViewById(R.id.bigImage_id);
        TextView title = listItem.findViewById(R.id.label);
        TextView mmsg = listItem.findViewById(R.id.mainMessages);
        ViewGroup.LayoutParams params = zireroo2.getLayoutParams();
        if(currentItem.getisBigImage()) {
            zireroo2.setBackgroundResource(currentItem.getbigPicture());
            File imgFile = new  File(currentItem.getimgUrl());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();


            Picasso.with(mContext)
                    .load(currentItem.getimgUrl())
                    .into(bigpicture, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                        }
                    });

            //params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            //zireroo2.setLayoutParams(params);
            picture.setVisibility(View.GONE);
            title.setTextColor(currentItem.gettxtColor_t());
            title.setMinLines(currentItem.getlineNumber_t());
            title.setTextSize((int) convertDpToPixel(currentItem.gettxtSize_dp_t()+1.0f, getContext()));
            title.setText(currentItem.getTitle());
            title.setTypeface(null, currentItem.gettxtStyle_t());
            mmsg.setVisibility(View.GONE);
        }else {

            bigpicture.setVisibility(View.GONE);
            if(currentItem.getisImage()){
                picture.setVisibility(View.VISIBLE);
                picture.setBackgroundResource(currentItem.getPicture());
            }else {
                picture.setVisibility(View.GONE);
            }


            title.setTextColor(currentItem.gettxtColor_t());
            title.setMinLines(currentItem.getlineNumber_t());
            title.setTextSize((int) convertDpToPixel(currentItem.gettxtSize_dp_t(), getContext()));
            title.setText(currentItem.getTitle());
            title.setTypeface(null, currentItem.gettxtStyle_t());


            mmsg.setBackgroundColor(currentItem.getbgColor_m());
            mmsg.setTextColor(currentItem.gettxtColor_m());
            mmsg.setText(currentItem.getMainMessage());
            mmsg.setMinLines(currentItem.getlineNumber_m());
            mmsg.setTextSize((int) convertDpToPixel(currentItem.gettxtSize_dp_m(), getContext()));
            mmsg.setTypeface(null, currentItem.gettxtStyle_m());

        }
        zirerootV.setBackgroundColor(currentItem.getbgColor_t());


        return listItem;
    }



    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mitems.size();
    }



















    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    private Bitmap decodeFile(File f) throws Exception{
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = new FileInputStream(f);
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();

        int scale = 1;
        int IMAGE_MAX_SIZE=Math.max(IMAGE_MAX_HEIGHT,IMAGE_MAX_WIDTH);
        if (o.outHeight > IMAGE_MAX_HEIGHT || o.outWidth > IMAGE_MAX_WIDTH) {
            scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        fis = new FileInputStream(f);
        b = BitmapFactory.decodeStream(fis, null, o2);
        fis.close();

        return b;
    }
}