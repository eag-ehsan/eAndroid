package com.ourartag.eandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;

public class ListViewAdapter extends ArrayAdapter<Item> {

    ListViewAdapter(@NonNull Context context, ArrayList<Item> items) {
        super(context, 0, items);
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

        ImageView picture = listItem.findViewById(R.id.IvPicture);
        if(currentItem.getisImage()){
            picture.setVisibility(View.VISIBLE);
            picture.setBackgroundResource(currentItem.getPicture());
        }else {
            picture.setVisibility(View.GONE);
        }


        LinearLayout rootV = listItem.findViewById(R.id.rootView);
        LinearLayout zirerootV = listItem.findViewById(R.id.zereroot);
        zirerootV.setBackgroundColor(currentItem.getbgColor_t());




        TextView title = listItem.findViewById(R.id.label);
        title.setTextColor(currentItem.gettxtColor_t());
        title.setMinLines(currentItem.getlineNumber_t());
        title.setTextSize((int) convertDpToPixel(currentItem.gettxtSize_dp_t(), getContext()));
        title.setText(currentItem.getTitle());
        title.setTypeface(null, currentItem.gettxtStyle_t());


        TextView mmsg = listItem.findViewById(R.id.mainMessages);
        mmsg.setBackgroundColor(currentItem.getbgColor_m());
        mmsg.setTextColor(currentItem.gettxtColor_m());
        mmsg.setText(currentItem.getMainMessage());
        mmsg.setMinLines(currentItem.getlineNumber_m());
        mmsg.setTextSize((int) convertDpToPixel(currentItem.gettxtSize_dp_m(), getContext()));
        mmsg.setTypeface(null, currentItem.gettxtStyle_m());


        return listItem;
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
}