package com.gmail.yuki.swipe_cards_1215.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gmail.yuki.swipe_cards_1215.R;

/**
 * Created by yuki on 2018/01/04.
 */

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMessage;
    public LinearLayout mContainer;
    public ImageView mChatImage;

    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
        mChatImage =(ImageView) itemView.findViewById(R.id.ChatImage);

    }


    @Override
    public void onClick(View view) {

    }
}
