package com.syncusup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class MenuActivity extends Activity implements View.OnClickListener {
    int objectSize;
    private TextView inviteCode;
    private Notif anotif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Menu");
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
        inviteCode = (TextView) findViewById(R.id.my_invite_code);

        /* Updates/Removes friend status from FriendRequests with currentUser    */
        final ParseUser currentUser = ParseUser.getCurrentUser();
        inviteCode.setText("Your Invite Code: "+currentUser.getObjectId());
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
                        if(r.getString("status").equals("accepted") && r.getString("Checked").equals("No")){

                                                anotif = new Notif();
                                                anotif.setTitle("You have a new friend!");
                                                anotif.setUuidString();
                                                anotif.setDraft(true);
                                                anotif.pinInBackground();

                                                ParseRelation relation2 = currentUser.getRelation("Notif");
                                                relation2.add(anotif);
                                                currentUser.saveInBackground();
                                                anotif.saveInBackground(new SaveCallback() {

                                                    @Override
                                                    public void done(ParseException e) {


                                                        ParseRelation relation1 = currentUser.getRelation("Notif");
                                                        relation1.add(anotif);
                                                        currentUser.saveInBackground();

                                                    }
                                                });
                            ParseRelation relation = currentUser.getRelation("Friends");
                            ParseQuery query = relation.getQuery();
                            query.findInBackground(new FindCallback<ParseObject>() {

                                @Override
                                public void done(List<ParseObject> friends, ParseException e) {
                                    String friendId = r.getString("toUser");
                                    if(friends.size() > 0) {
                                        for (int j = 0; j < friends.size(); j++) {
                                            String thisFriend = friends.get(j).getString("friend_id");
                                            if (thisFriend.equals(friendId)) {
                                                if (!friends.get(j).getString("status").equals("friend")) {
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
                                    else {
                                        Toast.makeText(getApplicationContext(), "ERROR",
                                                Toast.LENGTH_LONG).show();
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
                                    if(friends.size() > 0) {
                                        for (int j = 0; j < friends.size(); j++) {
                                            String thisFriend = friends.get(j).getString("friend_id");
                                            if (thisFriend.equals(friendId)) {
                                                if (!friends.get(j).getString("status").equals("theyIgnoredYou")) {
                                                    friends.get(j).put("status", "theyIgnoredYou");
                                                    friends.get(j).saveInBackground();
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "ERROR",
                                                Toast.LENGTH_LONG).show();
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
                                    if(friends.size() > 0) {
                                        for (int j = 0; j < friends.size(); j++) {
                                            String thisFriend = friends.get(j).getString("friend_id");
                                            if (thisFriend.equals(friendId)) {
                                                if (!friends.get(j).getString("status").equals("theyRemovedYou")) {
                                                    friends.get(j).put("status", "theyRemovedYou");
                                                    friends.get(j).saveInBackground();
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "ERROR",
                                                Toast.LENGTH_LONG).show();
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
                getPrivateTodos();
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
                break;
            case R.id.shared_button:
                Intent showLists = new Intent(this, ShowListsActivity.class);
                startActivity(showLists);
                break;

            //Add other buttons here
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Log out the current user
            ParseUser.logOut();
            // Create a new anonymous user
            //ParseAnonymousUtils.logIn(null);
            startActivity(new Intent(this, WelcomeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
    public void getPrivateTodos(){
        //TODO change to launch private todos
        ParseQuery<SyncList> privListQuery= SyncList.getQuery();
        privListQuery.whereEqualTo("name", ParseUser.getCurrentUser().getUsername()+"'s Todos"); // kind of a hack the boolean i was using does not appear in the parse columns
        privListQuery.whereEqualTo("creator", ParseUser.getCurrentUser());
        privListQuery.getFirstInBackground(new GetCallback<SyncList>() {
            @Override
            public void done(SyncList syncList, ParseException e) {
                if (e == null) {
                    if (!isFinishing()) {
                        Intent myTodos = new Intent(MenuActivity.this, TodoListActivity.class);
                        myTodos.putExtra("parentListId", syncList.getObjectId());
                        startActivity(myTodos);
                    }
                } else {
                    Log.i("TodoListActivity",
                            "getPrivateTodos: Error finding private todo list: "
                                    + e.getMessage());
                }
            }
        });


    }

}
