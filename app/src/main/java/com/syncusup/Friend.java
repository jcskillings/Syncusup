package com.syncusup;

/**
 * Created by Justin on 5/4/2015.
 */
import android.util.Log;

import java.util.Date;
import java.util.UUID;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Friends")
public class Friend extends ParseObject{
    public String getUserName(){
        return getString("username");
    }
    public void setUsername(String username){
        put("username", username);
    }
    public String getName(){
        return getString("name");
    }
    public void setName(String name){
        put("name", name);
    }
    public String getNickname(){
        return getString("nickname");
    }
    public void setNickname(String nickname){
        put("nickname", nickname);
    }
    public Boolean isAll(){
        return getBoolean("all");
   }
   public void setAll(Boolean all){
       put("all", all);
   }
    public Boolean isFamily(){
        return getBoolean("family");
    }
    public void setFamily(Boolean family){
        put("family", family);
    }
    public Boolean isFriend(){
        return getBoolean("friend");
    }
    public void setFriend(Boolean friend){
        put("friend", friend);
    }
    public Boolean isPersonal(){
        return getBoolean("personal");
    }
    public void setPersonal(Boolean personal){
        put("personal", personal);
    }
    public Boolean isSchool(){
        return getBoolean("school");
    }
    public void setSchool(Boolean school){
        put("school", school);
    }
    public Boolean isWork(){
        return getBoolean("work");
    }
    public void setWork(Boolean work){
        put("work", work);
    }
    public String getStatus(){
        return getString("status");
    }
    public void setStatus(String status){
        put("status", status);
    }
    public String getFriendId(){
        return getString("friend_id");
    }
    public void setFriendId(String friendId){
        put("friend_id", friendId);
    }
    public String getMessage(){
        return getString("message");
    }
    public void setMessage(String msg){
        put("message", msg);
    }
    public static ParseQuery<Friend> getQuery(){
        return ParseQuery.getQuery(Friend.class);
    }
}
