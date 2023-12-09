package com.ourartag.eandroid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
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
        TextView mmsg = listItem.findViewById(R.id.mainMessages);
        mmsg.setBackgroundColor(currentItem.getbgColor_m());
        title.setTextColor(currentItem.gettxtColor_t());
        title.setText(currentItem.getTitle());
        title.setMinLines(currentItem.getlineNumber_t());
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float dp = currentItem.gettxtSize_dp_t();
        float fpixels = metrics.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        title.setTextSize(pixels);
        title.setTypeface(null, currentItem.gettxtStyle_t());

        mmsg.setTextColor(currentItem.gettxtColor_m());
        mmsg.setText(currentItem.getMainMessage());
        mmsg.setMinLines(currentItem.getlineNumber_m());

        dp = currentItem.gettxtSize_dp_m();
        fpixels = metrics.density * dp;
        pixels = (int) (fpixels + 0.5f);
        mmsg.setTextSize(pixels);
        mmsg.setTypeface(null, currentItem.gettxtStyle_m());


        return listItem;
    }

}