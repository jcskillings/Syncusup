package com.syncusup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;


public class MenuActivity extends Activity implements View.OnClickListener {
    int objectSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        View todoButton = findViewById(R.id.todo_button);
        todoButton.setOnClickListener(this);
        View calendarButton = findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(this);
        View notificationsButton = findViewById(R.id.notifs_button);
        notificationsButton.setOnClickListener(this);
        View sharedlistsButton = findViewById(R.id.shared_button);
        sharedlistsButton.setOnClickListener(this);
        View myfriendsButton = findViewById(R.id.friends_button);
        myfriendsButton.setOnClickListener(this);

        /* Updates/Removes friend status from FriendRequests with currentUser    */
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequests");
        query.whereEqualTo("fromUser", currentUser.getObjectId());
        query.orderByAscending("createDate");
        query.findInBackground(new FindCallback<ParseObject>() {//if user sent a request, and that
            //request is now accepted, update that friend and remove the FriendRequest.  Also,
            //delete newer copies of requests if multiple exist.  Also, update status of all friends
            //if they changed.

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(objects.size() > 0){
                    objectSize = objects.size();
                    for (int i = 0; i < objectSize; i++) {
                        final ParseObject r = objects.get(i);
                        if(r.getString("status").equals("accepted")){
                            ParseRelation relation = currentUser.getRelation("Friends");
                            ParseQuery query = relation.getQuery();
                            query.findInBackground(new FindCallback<ParseObject>() {

                                @Override
                                public void done(List<ParseObject> friends, ParseException e) {
                                    String friendId = r.getString("toUser");
                                    for (int j = 0; j < friends.size(); j++) {
                                        String thisFriend = friends.get(j).getString("friend_id");
                                        if(thisFriend.equals(friendId)){
                                            if(!friends.get(j).getString("status").equals("friend")) {
                                                friends.get(j).put("status", "friend");
                                                friends.get(j).saveInBackground();
                                            }
                                            try {
                                                r.delete();
                                                objectSize--;
                                            } catch (ParseException e1) {
                                                Toast.makeText(getApplicationContext(), "unable to delete",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else if(r.getString("status").equals("ignored")){
                            ParseRelation relation = currentUser.getRelation("Friends");
                            ParseQuery query = relation.getQuery();
                            query.findInBackground(new FindCallback<ParseObject>() {

                                @Override
                                public void done(List<ParseObject> friends, ParseException e) {
                                    String friendId = r.getString("toUser");
                                    for (int j = 0; j < friends.size(); j++) {
                                        String thisFriend = friends.get(j).getString("friend_id");
                                        if(thisFriend.equals(friendId)){
                                            if(!friends.get(j).getString("status").equals("theyIgnoredYou")) {
                                                friends.get(j).put("status", "theyIgnoredYou");
                                                friends.get(j).saveInBackground();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else if(r.getString("status").equals("removed")){
                            ParseRelation relation = currentUser.getRelation("Friends");
                            ParseQuery query = relation.getQuery();
                            query.findInBackground(new FindCallback<ParseObject>() {

                                @Override
                                public void done(List<ParseObject> friends, ParseException e) {
                                    String friendId = r.getString("toUser");
                                    for (int j = 0; j < friends.size(); j++) {
                                        String thisFriend = friends.get(j).getString("friend_id");
                                        if(thisFriend.equals(friendId)){
                                            if(!friends.get(j).getString("status").equals("theyRemovedYou")) {
                                                friends.get(j).put("status", "theyRemovedYou");
                                                friends.get(j).saveInBackground();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        else if(r.getString("status").equals("pending")){
                            ParseRelation relation = currentUser.getRelation("Friends");
                            ParseQuery query = relation.getQuery();
                            query.findInBackground(new FindCallback<ParseObject>() {

                                @Override
                                public void done(List<ParseObject> friends, ParseException e) {
                                    String friendId = r.getString("toUser");
                                    for (int j = 0; j < friends.size(); j++) {
                                        String thisFriend = friends.get(j).getString("friend_id");
                                        if(thisFriend.equals(friendId)){
                                            if(!friends.get(j).getString("status").equals("pending")) {
                                                friends.get(j).put("status", "pending");
                                                friends.get(j).saveInBackground();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                } else {
                    //Toast.makeText(getApplicationContext(), "No requests to for this user",
                        //    Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.todo_button:
                Intent Todo = new Intent(this, ShowListsActivity.class);
                startActivity(Todo);
                break;
            case R.id.friends_button:
                Intent friend = new Intent(this, FriendActivity.class);
                startActivity(friend);
                break;
            case R.id.notifs_button:
                Intent Notification = new Intent(this, Notifications.class);
                startActivity(Notification);
                break;
            case R.id.calendar_button:
                Intent Calendar = new Intent(this, MyCalendarActivity.class);
                startActivity(Calendar);
            //Add other buttons here
        }
    }

}
