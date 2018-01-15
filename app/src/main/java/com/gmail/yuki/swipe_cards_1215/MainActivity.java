package com.gmail.yuki.swipe_cards_1215;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.gmail.yuki.swipe_cards_1215.Cards.arrayAdapter;
import com.gmail.yuki.swipe_cards_1215.Cards.cards;
import com.gmail.yuki.swipe_cards_1215.Matches.MatchesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private cards cards_data[];

    //↓自作アダプター
    private com.gmail.yuki.swipe_cards_1215.Cards.arrayAdapter arrayAdapter;
    Button bt1, bt2, bt3;
    FirebaseAuth mAuth;

    private String userSex;
    private String oppositeUserSex;


    private String currentUID;
    private DatabaseReference usersDb;


    ListView listView;
    //List<objects> 変数名 = new ArrayList<型>( );
    List<cards> rowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //↓ここはコピペしたら自分のアクティビティ名に変える。
        setContentView(R.layout.activity_main);

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        bt1 = findViewById(R.id.logout);
        bt2 = findViewById(R.id.setting);
        bt3 = findViewById(R.id.matches);
        mAuth = FirebaseAuth.getInstance();
        currentUID = mAuth.getCurrentUser().getUid();


        //サインアウトボタン///
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
                return;

            }
        });

        //setting botton///////
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
                return;

            }
        });

        //matches botton///////
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MatchesActivity.class);
                startActivity(intent);
                return;

            }
        });


        ///////////////

        checkUserSex();

        //////////////

        //大きさが決まってない配列
        rowItems = new ArrayList<cards>();

        //引数に、このアクティビティ、itemレイアウト、rouItemsの配列を渡している。(arrayAdapterは、自作のアダプター)
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        //idと紐付け。
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);


        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //↓デフォルトのトーストの文が、最新バージョンじゃないので自分で直す。

                //カードを左にスワイプした時、dataObjectをcardsオブジェクトに格納して、getUserID()にアクセス。
                //userIdをゲットしたら、データベースに接続する。

                //↓ここちょっと不明っすわ。cardsクラスにアクセスして、userIdを取って来てるらしいが、dataObjectを変換するあたりの動きがわからん。
                cards obj = (cards) dataObject;
                String userId = obj.getUserID();
                usersDb.child(userId).child("connections").child("nope").child(currentUID).setValue(true);

                Toast.makeText(getApplicationContext(), "Dislike!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {


                cards obj = (cards) dataObject;
                String userId = obj.getUserID();
                usersDb.child(userId).child("connections").child("yep").child(currentUID).setValue(true);

                //カードに出ているユーザーのIDを引き渡す。
                isConnectionMatch(userId);

                Toast.makeText(getApplicationContext(), "Like!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getApplicationContext(), "Clicked!!!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    ////////右にスワイプしたときに、下のファンクションが発動される///////////////////////////////////////////////
    ////////マッチしてもしなくても発動。checkは下のif文でやってる///////////////////////////////////////////////

    private void isConnectionMatch(final String userId) {

        //カードに出ているユーザーが、ログインしているユーザーのことをlikeしていたら、「yep」の配下に存在する。
        DatabaseReference currentUserConectionsDb = usersDb.child(currentUID).child("connections").child("yep").child(userId);
        currentUserConectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    //dataSnapshot.getKey()でカードに出ている人のユーザーIDを取得。その配下にconnectionsとmatchesを作成する。
//                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).setValue(true);
                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("ChatId").setValue(key);

                    //こっちは、自分のところにマッチした人を格納する。dataSnapshot.getKey()はカードに出ている人のユーザーID
//                    usersDb.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                    usersDb.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);

                    Toast.makeText(getApplicationContext(), "New Conections", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////

    public void checkUserSex() {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userDb = usersDb.child(user.getUid());

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {


            //今ログインしているユーザーの性別の逆の性別をoppositeUserSexに格納する

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("sex") != null) {

                        userSex = dataSnapshot.child("sex").getValue().toString();

                        switch (userSex) {

                            case "female":
                                oppositeUserSex = "male";
                                break;

                            case "male":
                                oppositeUserSex = "female";
                                break;
                        }

                        //その上でログインしているユーザーの性別の逆の性別のユーザーをカード(リスト)に格納する
                        getOppositeSexUsers();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getOppositeSexUsers() {

        //ログインしたユーザーの性別の逆を取得する。

        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.child("sex").getValue() != null) {

                    //dataが存在した場合かつ、すでにyepかnopeで、振り分けられてない場合に発動。
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("yep").hasChild(currentUID) && !dataSnapshot.child("connections").child("nope").hasChild(currentUID) && dataSnapshot.child("sex").getValue().equals(oppositeUserSex)) {

                        //基本はデフォルトを入れとく
                        String profileImageUrl = "default";

                        //もし、プロフィールイメージURLがデフォルトじゃなかったら、画像のURLを取得して格納する。
                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {

                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                        }

                        //ここで、DBから情報を引っ張ってきてる.ユーザID,名前、プロフィール画像のURLを引き渡してる
                        cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl);

                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


}