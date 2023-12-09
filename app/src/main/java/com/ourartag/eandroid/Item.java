package com.ourartag.eandroid;

import android.graphics.Typeface;

class Item {

    private int mPicture;
    private String mTitle, mMaintext;

    private Boolean mIsImage;

    private int mBgColot_t, mTxtColor_t, mLineNumber_t;

    private  float mTxtSize_dp_t;

    private int mTxtStyle_t;

    private int mBgColot_m, mTxtColor_m, mLineNumber_m;

    private  float mTxtSize_dp_m;

    private int mTxtStyle_m;

    int getPicture() {
        return mPicture;
    }

    Item(int picture, String title, String mainmsg, Boolean isImage,
         int bgColor_t, int txtColor_t, int lineNumber_t, float txtSize_dp_t, int txtStyle_t,
         int bgColor_m, int txtColor_m, int lineNumber_m, float txtSize_dp_m, int txtStyle_m) {
        mPicture = picture;
        mTitle = title;
        mIsImage = isImage;

        mMaintext = mainmsg;

        mBgColot_t = bgColor_t;
        mTxtColor_t = txtColor_t;
        mLineNumber_t = lineNumber_t;
        mTxtSize_dp_t = txtSize_dp_t;
        mTxtStyle_t = txtStyle_t;

        mBgColot_m = bgColor_m;
        mTxtColor_m = txtColor_m;
        mLineNumber_m = lineNumber_m;
        mTxtSize_dp_m = txtSize_dp_m;
        mTxtStyle_m = txtStyle_m;
    }

    String getTitle() {
        return mTitle;
    }
    String getMainMessage() {
        return mMaintext;
    }

    Boolean getisImage() {
        return mIsImage;
    }

    int getbgColor_t() {
        return mBgColot_t;
    }
    int gettxtColor_t() {
        return mTxtColor_t;
    }
    int getlineNumber_t() {
        return mLineNumber_t;
    }
    float gettxtSize_dp_t() {
        return mTxtSize_dp_t;
    }
    int gettxtStyle_t() {
        return mTxtStyle_t;
    }

    int getbgColor_m() {
        return mBgColot_m;
    }
    int gettxtColor_m() {
        return mTxtColor_m;
    }
    int getlineNumber_m() {
        return mLineNumber_m;
    }
    float gettxtSize_dp_m() {
        return mTxtSize_dp_m;
    }
    int gettxtStyle_m() {
        return mTxtStyle_m;
    }

}