package com.gmail.yuki.swipe_cards_1215.Matches;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gmail.yuki.swipe_cards_1215.R;

/**
 * Created by yuki on 2018/01/04.
 */

public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMatchId;


    public MatchesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId =(TextView)itemView.findViewById(R.id.Matchid);
    }

    @Override
    public void onClick(View view) {

    }
}
