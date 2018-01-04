package com.gmail.yuki.swipe_cards_1215.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gmail.yuki.swipe_cards_1215.R;

import java.util.List;

/**
 * Created by yuki on 2017/12/21.
 */


//<cards>ってやることで、cardsクラスを好きに使っていいってことなのかな

public class arrayAdapter extends ArrayAdapter<cards> {

    Context context;

    //ArrayAdapterをextendsすると、こいつをつくる決まりなのかな？
    public arrayAdapter(Context context, int resourceId, List<cards> items) {
        super(context, resourceId, items);

    }

    //getViewは絶対必要。
    public View getView(int position, View convertView, ViewGroup parent) {

        cards card_item = getItem(position);


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        ImageView image = convertView.findViewById(R.id.image);


        name.setText(card_item.getName());


        //Glide使ってFirebaseストレージにある画像をダウンロードURLにアクセスしてimageで表示。ダウンロードURLはcardsクラスから取得する。

        switch (card_item.getProfileImageUrl()) {

            case "default":

                Glide.with(convertView.getContext()).load(R.mipmap.ic_launcher).into(image);
                break;

            default:
                Glide.clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }


        //arrayAdapterを呼び出したら、リターンでconvetViewが返る。
        return convertView;


    }


}
