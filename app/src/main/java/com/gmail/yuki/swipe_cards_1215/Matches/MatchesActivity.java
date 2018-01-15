package com.gmail.yuki.swipe_cards_1215.Matches;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;

import com.gmail.yuki.swipe_cards_1215.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView =(RecyclerView) findViewById(R.id.recyclevriew);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        mMatchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(),MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);

        getUserMatchId();

    }

    private void getUserMatchId() {

        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    for(DataSnapshot match : dataSnapshot.getChildren()){

                        FetchMatchInformation(match.getKey());

                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation(String key) {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String userId = dataSnapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";

                    if(dataSnapshot.child("name").getValue() != null){
                        name = dataSnapshot.child("name").getValue().toString();
                    }

                    if(dataSnapshot.child("profileImageUrl").getValue() != null){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }

                    MatchesObject obj = new MatchesObject(userId,name,profileImageUrl);
                    resultMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private ArrayList<MatchesObject> resultMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {

        return resultMatches;


    }
}
