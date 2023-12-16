package com.ourartag.eandroid;

import android.view.View;

public interface RecyclerViewClickListener {
    public void recyclerViewListClicked(View v, int position);
    public void rVClicked_delete(View v, int position, int chatID);
    public void rVClicked_reply(View v, int position, int chatID, String chatTitle, String chatMsgFirstLine);
}