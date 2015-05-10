package com.syncusup;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Owner on 5/1/2015.
 */
public class PendingFriends extends ListActivity{

    @Override
    public void onStart() {
        super.onStart();
        //UAirship.shared().getAnalytics();
    }
    ParseObject topRequest;
    String username;
    String name;
    String friendId;
    String password;
    Boolean work;
    Boolean school;
    Boolean all;
    Boolean personal;
    Boolean family;
    Boolean friend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1);
            final ListView friendlv = (ListView) findViewById(android.R.id.list);
            friendlv.setAdapter(listAdapter);

            final LinearLayout ResultFrame1 = (LinearLayout) findViewById(R.id.frame1);
            final FrameLayout ResultFrame2 = (FrameLayout) findViewById(R.id.frame2);
            final LinearLayout ResultFrame3 = (LinearLayout) findViewById(R.id.frame3);
            final EditText nicknameEdit = (EditText) findViewById(R.id.nickname);
            final EditText Message = (EditText) findViewById(R.id.message);
            final CheckBox allBox = (CheckBox) findViewById(R.id.all);
            final CheckBox friendBox = (CheckBox) findViewById(R.id.friend);
            final CheckBox familyBox = (CheckBox) findViewById(R.id.family);
            final CheckBox workBox = (CheckBox) findViewById(R.id.work);
            final CheckBox schoolBox = (CheckBox) findViewById(R.id.school);
            final CheckBox personalBox = (CheckBox) findViewById(R.id.personal);
            final Button Accept = (Button)findViewById(R.id.accept);
            final Button Deny = (Button)findViewById(R.id.decline);

            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("FriendRequests");
            query1.whereEqualTo("toUser", currentUser.getObjectId());
            query1.whereEqualTo("status", "pending");
            query1.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    // TODO Auto-generated method stub
                    //Toast.makeText(getApplicationContext(), objects.get(0).getObjectId(),
                    //        Toast.LENGTH_LONG).show();
                    if (objects.size() != 0) {
                        //Toast.makeText(getApplicationContext(), "retrieved " + objects.size(),
                        //              Toast.LENGTH_LONG).show();
                        ResultFrame1.setVisibility(View.VISIBLE);
                        ResultFrame2.setVisibility(View.VISIBLE);
                        ResultFrame3.setVisibility(View.VISIBLE);
                        topRequest = objects.get(0);
                        String message;
                        if(topRequest.getString("message")==null || topRequest.getString("message")=="")
                            message = "No message";
                        else message = topRequest.getString("message");
                        listAdapter.insert("This person says to you:\n'"+message+"'", 0);
                        ParseQuery query = ParseUser.getQuery();
                        query.whereEqualTo("objectId", topRequest.getString("fromUser"));//find that friend
                        query.getFirstInBackground(new GetCallback<ParseUser>() {

                            @Override
                            public void done(ParseUser friends, ParseException e) {
                                try {
                                    username = friends.getString("username");
                                    if(friends.getString("name") == null) name = "None given";
                                    else name = friends.getString("name");
                                    friendId = friends.getObjectId();
                                    final TextView UsernameText = (TextView) findViewById(R.id.username);
                                    final TextView NameText = (TextView) findViewById(R.id.name);
                                    UsernameText.setText("From: " + username);
                                    NameText.setText("Name: " + name);


                                } catch (Exception e3) {
                                    Toast.makeText(getApplicationContext(), "friend no longer exists",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        if(objects.size() > 1) {
                            listAdapter.add("Requests sent to you:");
                            for (int i = 1; i < objects.size(); i++) {
                                ParseObject r = objects.get(i);//get next request to the user
                                //if(r.getString("toUser").equals(ParseUser.getCurrentUser().getObjectId())) {

                                ParseQuery query2 = ParseUser.getQuery();
                                query2.whereEqualTo("objectId", r.getString("fromUser"));
                                query2.getFirstInBackground(new GetCallback<ParseUser>() {

                                    @Override
                                    public void done(ParseUser friends2, ParseException e) {
                                        try {
                                            String username = friends2.getString("username");
                                            String name;
                                            if(friends2.getString("name") == null) name = "No name given";
                                            else name = friends2.getString("name");
                                            String total = "Username:" + username + "\nName:" + name;
                                            listAdapter.add(total);

                                        } catch (Exception e3) {
                                            Toast.makeText(getApplicationContext(), "friend no longer exists",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                //}
                            }
                        }
                        else{
                            listAdapter.insert("No other requests sent to accept/ignore", 1);
                        }
                    } else {
                        listAdapter.add("No pending requests to accept/ignore");
                        Toast.makeText(getApplicationContext(), "No requests found1",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            ParseQuery<ParseObject> query4 = ParseQuery.getQuery("FriendRequests");
            query4.whereEqualTo("fromUser", currentUser.getObjectId());
            query4.whereEqualTo("status", "pending");
            query4.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> objects2, ParseException e) {
                    if (objects2.size() != 0) {
                        listAdapter.add("Requests you sent still pending:");
                        for (int k = 0; k < objects2.size(); k++) {
                            ParseObject r = objects2.get(k);
                            //final int num = k+1;
                            ParseQuery query2 = ParseUser.getQuery();
                            query2.whereEqualTo("objectId", r.getString("toUser"));
                            query2.getFirstInBackground(new GetCallback<ParseUser>() {

                                @Override
                                public void done(ParseUser friends3, ParseException e) {
                                    try {
                                        String username = friends3.getString("username");
                                        String name;
                                        if(friends3.getString("name") == null) name = "No name given";
                                        else name = friends3.getString("name");
                                        String total = "Username:" + username + "\nName:" + name;
                                        listAdapter.add(total);

                                    } catch (Exception e3) {
                                        Toast.makeText(getApplicationContext(), "friend no longer exists",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "No requests found2",
                                Toast.LENGTH_LONG).show();
                        listAdapter.add("No requests you sent are still pending");
                    }
                }
            });

            Accept.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){

                    topRequest.put("status", "accepted");
                    topRequest.saveInBackground();
                    final Friend friend = new Friend();
                    friend.put("username", username);
                    if (name != null) friend.put("name", name);
                    else friend.put("name", "No name given");
                    friend.put("nickname", nicknameEdit.getText().toString());
                    friend.put("message", Message.getText().toString());
                    friend.put("all", allBox.isChecked());
                    friend.put("friend", friendBox.isChecked());
                    friend.put("family", familyBox.isChecked());
                    friend.put("work", workBox.isChecked());
                    friend.put("school", schoolBox.isChecked());
                    friend.put("personal", personalBox.isChecked());
                    friend.put("friend_id", friendId);
                    friend.put("friend_id2", currentUser.getObjectId());
                    friend.put("status", "friend");
                    ParseACL acl = new ParseACL();
                    acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                    acl.setReadAccess(topRequest.getString("fromUser"), true);
                    acl.setReadAccess(ParseUser.getCurrentUser(), true);
                    friend.setACL(acl);
                    try {
                        friend.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ParseRelation relation = currentUser.getRelation("Friends");
                    relation.add(friend);
                    try {
                        currentUser.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Friend status updated!",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PendingFriends.this, PendingFriends.class);
                    startActivity(intent);
                }

            });

            Deny.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    topRequest.put("status", "ignored");
                    try {
                        topRequest.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Friend status updated!",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PendingFriends.this, PendingFriends.class);
                    startActivity(intent);
                }

            });
        }
    }
}





