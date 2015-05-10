package com.syncusup;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
public class ViewFriends extends ListActivity{

    ParseObject objects;

    @Override
    public void onStart() {
        super.onStart();
        //UAirship.shared().getAnalytics();
    }

    ArrayList<String> friendIds = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1);
            ListView friendlv = (ListView)findViewById(android.R.id.list);
            friendlv.setAdapter(listAdapter);
            final Button Edit = (Button)findViewById(R.id.edit);
            final Button Remove = (Button)findViewById(R.id.remove);
            final Button Ignore = (Button)findViewById(R.id.ignored);
            final Button Calendar = (Button)findViewById(R.id.calendar);
            final EditText inviteCode = (EditText) findViewById(R.id.inviteCode);
            final TextView ResultText = (TextView)findViewById(R.id.textView);

            ParseRelation relation = currentUser.getRelation("Friends");
            ParseQuery query = relation.getQuery();
            query.whereEqualTo("status", "friend");
            //query.whereNotEqualTo("friend_id", currentUser.getObjectId());
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(objects.size() != 0){
                        ResultText.setText(currentUser.getString("username")+"'s friends:");
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject r = objects.get(i);
                            String username = r.getString("username");
                            String nickname = r.getString("nickname");
                            String name = r.getString("name");
                            String inviteCode = r.getString("friend_id");
                            friendIds.add(inviteCode);
                            if (nickname == null) nickname = "None";
                            if (name == null) name = "None";
                            int friendNum = i + 1;
                            String total = "(#"+friendNum + ") Username: "+username + "\nName: " +
                                    name + "\nNickname: "+ nickname +"\nPermissions: ";
                            Boolean all = r.getBoolean("all");
                            Boolean family = r.getBoolean("family");
                            Boolean friend = r.getBoolean("friend");
                            Boolean work = r.getBoolean("work");
                            Boolean school = r.getBoolean("school");
                            Integer count = 0;
                            Boolean personal = r.getBoolean("personal");
                            if (all == true) {
                                total += "all";
                                count++;
                            } else {
                                if (family == true) {
                                    total += "family";
                                    count++;
                                }
                                if (friend == true) {
                                    if (count == 0) total += "friend";
                                    else total += ", friend";
                                    count++;
                                }
                                if (work == true) {
                                    if (count == 0) total += "work";
                                    else total += ", work";
                                    count++;
                                }
                                if (school == true) {
                                    if (count == 0) total += "school";
                                    else total += ", school";
                                    count++;
                                }
                                if (personal == true) {
                                    if (count == 0) total += "personal";
                                    else total += ", personal";
                                    count++;
                                }
                            }
                            if(count == 0)total+="None";
                            listAdapter.add(total);
                        }
                    }
                    else{
                        ResultText.setText(currentUser.getString("username")+"'s friends:");
                        listAdapter.add("You have no friends :_(");
                    }
                }
            });

            Edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inviteCode.getWindowToken(), 0);
                    final int numEntered;
                    try {
                        numEntered = Integer.parseInt(inviteCode.getText().toString());
                    }
                    catch(Exception e2){
                        Toast.makeText(getApplicationContext(), "You entered invalid input!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(numEntered < 1 || numEntered > friendIds.size() ){
                        Toast.makeText(getApplicationContext(), "You entered an invalid friend number!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(getBaseContext(), EditFriendActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID",
                            friendIds.get(numEntered-1));
                    startActivity(intent);
                }
            });

            Ignore.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inviteCode.getWindowToken(), 0);
                    Intent intent = new Intent(ViewFriends.this, IgnoreActivity.class);
                    startActivity(intent);
                }
            });

            Calendar.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inviteCode.getWindowToken(), 0);

                    final int numEntered;
                    try {
                        numEntered = Integer.parseInt(inviteCode.getText().toString());
                    }
                    catch(Exception e2){
                        Toast.makeText(getApplicationContext(), "You entered invalid input!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(numEntered < 1 || numEntered > friendIds.size() ){
                        Toast.makeText(getApplicationContext(), "You entered an invalid friend number!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent(getBaseContext(), FriendCalendarActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID",
                            friendIds.get(numEntered-1));
                    startActivity(intent);
                }
            });

            Remove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    if(inviteCode.getText().toString() == "") return;
                    final String thisId;
                    try {
                        thisId = friendIds.get(Integer.parseInt(inviteCode.getText().toString()) - 1).toString();
                    }
                    catch (Exception e2){
                        Toast.makeText(getApplicationContext(), "invalid input",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    ParseRelation relation = currentUser.getRelation("Friends");
                    ParseQuery query = relation.getQuery();
                    query.whereEqualTo("friend_id", thisId);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {

                        @Override
                        public void done(ParseObject person, ParseException e) {
                            person.put("status", "youRemovedThem");
                            person.saveInBackground();
                            Toast.makeText(getApplicationContext(), person.getString("username")+" is now ignored!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                    final ParseObject friendRequest = new ParseObject("FriendRequests");
                    friendRequest.put("toUser", currentUser.getObjectId().toString());
                    friendRequest.put("fromUser", thisId);
                    friendRequest.put("status", "removed");
                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);
                    acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                    acl.setWriteAccess(thisId, true);
                    friendRequest.setACL(acl);
                    friendRequest.saveInBackground();
                    Intent intent = new Intent(ViewFriends.this, ViewFriends.class);
                    startActivity(intent);
                }
            });
        }
    }
}
