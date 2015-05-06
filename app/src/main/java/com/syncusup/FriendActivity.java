package com.syncusup;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
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
public class FriendActivity extends Activity{

    @Override
    public void onStart() {
        super.onStart();
    }

    ParseObject userObject;
    String username;
    String name;
    String friendId;
    String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        final EditText inviteCode = (EditText)findViewById(R.id.editText);
        final EditText nicknameEdit = (EditText)findViewById(R.id.nickname);
        final EditText Message = (EditText)findViewById(R.id.message);
        final CheckBox allBox = (CheckBox)findViewById(R.id.all);
        final CheckBox friendBox = (CheckBox)findViewById(R.id.friend);
        final CheckBox familyBox = (CheckBox)findViewById(R.id.family);
        final CheckBox workBox = (CheckBox)findViewById(R.id.work);
        final CheckBox schoolBox = (CheckBox)findViewById(R.id.school);
        final CheckBox personalBox = (CheckBox)findViewById(R.id.personal);
        Button view = (Button)findViewById(R.id.view);
        Button search = (Button)findViewById(R.id.find);
        final Button Add = (Button)findViewById(R.id.Add);
        Button pending = (Button)findViewById(R.id.pending);
        final TextView ResultText = (TextView)findViewById(R.id.ResultTextView);
        final TextView ResultText2 = (TextView)findViewById(R.id.ResultTextView2);
        final TextView ResultText3 = (TextView)findViewById(R.id.ResultTextView3);
        final FrameLayout ResultFrame = (FrameLayout)findViewById(R.id.ResultFrameLayout);


        //ResultFrame.setVisibility(View.GONE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(FriendActivity.this, ViewFriends.class);
                startActivity(intent);
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(FriendActivity.this, PendingFriends.class);
                startActivity(intent);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String code = inviteCode.getText().toString();

                final ParseQuery query = ParseUser.getQuery();
                query.whereEqualTo("objectId", code);
                query.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        try {
                            userObject = objects.get(0);
                            Add.setVisibility(View.VISIBLE);
                            password = userObject.getString("password");
                            username = userObject.getString("username");
                            name = userObject.getString("name");
                            friendId = userObject.getObjectId();
                            ResultText.setText(username);
                            ResultText2.setText(name);
                            ResultText3.setText(friendId);
                            ResultFrame.setVisibility(View.VISIBLE);
                            nicknameEdit.setVisibility(View.VISIBLE);
                            nicknameEdit.setText("");
                            Message.setText("");
                            allBox.setChecked(false);
                            friendBox.setChecked(false);
                            workBox.setChecked(false);
                            familyBox.setChecked(false);
                            personalBox.setChecked(false);
                            schoolBox.setChecked(false);
                            Add.setVisibility(View.VISIBLE);
                            Message.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "User Found",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                            Toast.makeText(getApplicationContext(), "User Not Found",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        Add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    {
                        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("FriendRequests");
                        query1.whereEqualTo("toUser", currentUser.getObjectId());
                        query1.whereEqualTo("fromUser", friendId);
                        query1.getFirstInBackground(new GetCallback<ParseObject>() {

                            @Override
                            public void done(ParseObject object1, ParseException e) {
                                if(object1 != null){
                                    Toast.makeText(getApplicationContext(), "You already have a request pending for this person!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("FriendRequests");
                        query2.whereEqualTo("fromUser", currentUser.getObjectId());
                        query2.whereEqualTo("toUser", friendId);
                        query2.getFirstInBackground(new GetCallback<ParseObject>() {

                            @Override
                            public void done(ParseObject object1, ParseException e) {
                                if(object1 != null){
                                    Toast.makeText(getApplicationContext(), "You already sent a request to this person!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        final ParseObject friend = new ParseObject("Friends");
                        friend.put("username", username);
                        if(name != null) friend.put("name", name);
                        friend.put("nickname", nicknameEdit.getText().toString());
                        friend.put("message", Message.getText().toString());
                        friend.put("all", allBox.isChecked());
                        friend.put("friend", friendBox.isChecked());
                        friend.put("family", familyBox.isChecked());
                        friend.put("work", workBox.isChecked());
                        friend.put("school", schoolBox.isChecked());
                        friend.put("personal", personalBox.isChecked());
                        friend.put("friend_id", friendId);
                        friend.put("status", "sent");


                        final ParseObject friendRequest = new ParseObject("FriendRequests");
                        friendRequest.put("fromUser", currentUser.getObjectId());
                        friendRequest.put("toUser", userObject.getObjectId());
                        friendRequest.put("status", "pending");
                        friendRequest.put("message", Message.getText().toString());
                        ParseACL acl = new ParseACL();
                        acl.setPublicReadAccess(true);
                        acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                        acl.setWriteAccess(userObject.getObjectId(), true);
                        friendRequest.setACL(acl);
                        friendRequest.saveInBackground();

                        friend.saveInBackground(new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                ParseRelation relation = currentUser.getRelation("Friends");
                                relation.add(friend);
                                currentUser.saveInBackground();
                            }

                        });
                        Toast.makeText(getApplicationContext(), "Friend request has been sent!",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(FriendActivity.this, FriendActivity.class);
                        startActivity(intent);
                    }
                }

            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();

    }

}
