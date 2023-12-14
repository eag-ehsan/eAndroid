package com.ourartag.eandroid;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by anupamchugh on 09/02/16.
 */
public class MultiViewTypeAdapter extends RecyclerView.Adapter {
    private static RecyclerViewClickListener mItemListener;
    private ArrayList<Model>dataSet;
    Context mContext;
    int total_types;
    MediaPlayer mPlayer;
    private boolean fabStateVolume = false;

    public void setOnItemClickListener(RecyclerViewClickListener listener) {
        mItemListener = listener;
    }



    public MultiViewTypeAdapter(ArrayList<Model>data, Context context) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case Model.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_type, parent, false);
                return new TextTypeViewHolder(view, mItemListener);
            case Model.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_type, parent, false);
                return new ImageTypeViewHolder(view, mItemListener);
            case Model.AUDIO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_type, parent, false);
                return new AudioTypeViewHolder(view, mItemListener);
        }
        return null;
    }


    @Override
    public int getItemViewType(int position) {

        switch (dataSet.get(position).type) {
            case 0:
                return Model.TEXT_TYPE;
            case 1:
                return Model.IMAGE_TYPE;
            case 2:
                return Model.AUDIO_TYPE;
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int listPosition) {

        Model object = dataSet.get(listPosition);
        if (object != null) {
            switch (object.type) {
                case Model.TEXT_TYPE:
                    ((TextTypeViewHolder) holder).mText_type_icon.setImageResource(object.mWhichicon);
                    ((TextTypeViewHolder) holder).mText_type_title.setText(object.mTitle);
                    ((TextTypeViewHolder) holder).mText_type_msg.setText(object.mMsg);
                    ((TextTypeViewHolder) holder).mText_type_dt.setText(object.mDatetime);



                    break;
                case Model.IMAGE_TYPE:
                    ((ImageTypeViewHolder) holder).mImage_type_icon.setImageResource(object.mWhichicon);
                    ((ImageTypeViewHolder) holder).mImage_type_title.setText(object.mTitle);
                    ((ImageTypeViewHolder) holder).mImage_type_dt.setText(object.mDatetime);
                    //File imgFile = new  File(object.mImageUrl);
                    //BitmapFactory.Options bmOptions = new BitmapFactory.Options();


                    Picasso.with(mContext)
                            .load(object.mImageUrl)
                            .into(((ImageTypeViewHolder) holder).mImage_type_image, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                }
                                @Override
                                public void onError() {
                                }
                            });
                    break;
                case Model.AUDIO_TYPE:

                    ((AudioTypeViewHolder) holder).mAudio_type_icon.setImageResource(object.mWhichicon);
                    ((AudioTypeViewHolder) holder).mAudio_type_title.setText(object.mTitle);
                    ((AudioTypeViewHolder) holder).mAudio_type_dt.setText(object.mDatetime);
                    ((AudioTypeViewHolder) holder).fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (fabStateVolume) {
                                if (mPlayer.isPlaying()) {
                                    mPlayer.stop();

                                }
                                ((AudioTypeViewHolder) holder).fab.setImageResource(R.drawable.volume_50);
                                fabStateVolume = false;

                            } else {
                                mPlayer = MediaPlayer.create(mContext, object.mWhichicon);
                                mPlayer.setLooping(true);
                                mPlayer.start();
                                ((AudioTypeViewHolder) holder).fab.setImageResource(R.drawable.volume_xmark_50);
                                fabStateVolume = true;

                            }
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }





    public static class TextTypeViewHolder extends RecyclerView.ViewHolder {

        TextView mText_type_title, mText_type_msg, mText_type_dt;
        CardView mText_type_card_view;
        ImageView mText_type_icon;

        public TextTypeViewHolder(View itemView, final RecyclerViewClickListener listener) {
            super(itemView);

            this.mText_type_card_view = (CardView) itemView.findViewById(R.id.text_type_card_view);
            this.mText_type_icon = (ImageView) itemView.findViewById(R.id.text_type_icon);
            this.mText_type_title = (TextView) itemView.findViewById(R.id.text_type_title);
            this.mText_type_msg = (TextView) itemView.findViewById(R.id.text_type_msg);
            this.mText_type_dt = (TextView) itemView.findViewById(R.id.text_type_dt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.recyclerViewListClicked(v, position);

                        }
                    }
                }
            });


        }









    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {

        TextView mImage_type_title, mImage_type_dt;
        ImageView mImage_type_image;
        ImageView mImage_type_icon;
        public ImageTypeViewHolder(View itemView, final RecyclerViewClickListener listener) {
            super(itemView);

            this.mImage_type_icon = (ImageView) itemView.findViewById(R.id.image_type_icon);
            this.mImage_type_title = (TextView) itemView.findViewById(R.id.image_type_title);
            this.mImage_type_image = (ImageView) itemView.findViewById(R.id.image_type_image);
            this.mImage_type_dt = (TextView) itemView.findViewById(R.id.image_type_dt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.recyclerViewListClicked(v, position);

                        }
                    }
                }
            });
        }
    }

    public static class AudioTypeViewHolder extends RecyclerView.ViewHolder {

        TextView mAudio_type_title, mAudio_type_dt;
        ImageView mAudio_type_icon;
        TextView txtType;
        FloatingActionButton fab;

        public AudioTypeViewHolder(View itemView, final RecyclerViewClickListener listener) {
            super(itemView);

            this.mAudio_type_icon = (ImageView) itemView.findViewById(R.id.audio_type_icon);
            this.mAudio_type_title = (TextView) itemView.findViewById(R.id.audio_type_title);
            this.fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
            this.mAudio_type_dt = (TextView) itemView.findViewById(R.id.audio_type_dt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.recyclerViewListClicked(v, position);

                        }
                    }
                }
            });
        }
    }













}