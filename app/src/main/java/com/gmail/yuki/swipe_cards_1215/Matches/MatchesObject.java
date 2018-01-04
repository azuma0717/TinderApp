package com.gmail.yuki.swipe_cards_1215.Matches;

/**
 * Created by yuki on 2018/01/04.
 */

public class MatchesObject {
    private String userId;
    private String name;
    private String profileImageUrl;
    public MatchesObject (String userId){
        this.userId = userId;

    }

    public String getUserId(){
        return userId;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }
}
