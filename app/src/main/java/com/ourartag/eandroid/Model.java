package com.ourartag.eandroid;

public class Model {

    public static final int TEXT_TYPE=0;
    public static final int TEXT_IMG_TYPE=3;
    public static final int IMAGE_TYPE=1;
    public static final int AUDIO_TYPE=2;

    public int type;
    public int mWhichicon;
    public String mMsg, mTitle, mImageUrl, mDatetime;
    public Boolean mItsMe;

    public Model(Boolean itsMe, int type, int whichicon, String datetime, String title, String msg, String imageUrl)
    {
        this.type=type;
        this.mWhichicon = whichicon;
        this.mMsg = msg;
        this.mTitle = title;
        this.mImageUrl = imageUrl;
        this.mDatetime = datetime;
        this.mItsMe = itsMe;
    }

    public String getTitle(){
        return mTitle;
    }
    public String getImageURL(){
        return mImageUrl;
    }
    public Boolean getItisme(){
        return mItsMe;
    }
}