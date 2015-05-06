package com.syncusup;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Created by Owner on 5/5/2015.
 */
public class IgnoreActivity extends ListActivity {
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
        setContentView(R.layout.activity_ignore);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1);
            ListView friendlv = (ListView) findViewById(android.R.id.list);
            friendlv.setAdapter(listAdapter);

            final Button Unignore = (Button) findViewById(R.id.unignore);
            final EditText Number = (EditText) findViewById(R.id.number);
            final TextView ResultText = (TextView) findViewById(R.id.textView);
            ParseRelation relation = currentUser.getRelation("Friends");
            ParseQuery query = relation.getQuery();
            query.whereEqualTo("status", "youIgnoredThem");
            query.whereEqualTo("status", "youRemovedThem");
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() != 0) {
                        ResultText.setText(currentUser.getString("username") + "'s ignored friends:");
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject r = objects.get(i);
                            String username = r.getString("username");
                            String nickname = r.getString("nickname");
                            String name = r.getString("name");
                            //String message = r.getString("message");
                            String inviteCode = r.getString("friend_id");
                            friendIds.add(inviteCode);
                            if (nickname.equals("")) nickname = "None";
                            if (name.equals("")) name = "None";
                            //if (message.equals("")) message = "None";
                            int friendNum = i + 1;
                            String total = "(#" + friendNum + ") Username: " + username + "\nName: " +
                                    name + "\nNickname: " + nickname + "\nPermissions: ";
                            Boolean all = r.getBoolean("all");
                            Boolean family = r.getBoolean("family");
                            Boolean friend = r.getBoolean("friend");
                            Boolean work = r.getBoolean("work");
                            Boolean school = r.getBoolean("school");
                            Integer count = 0;
                            Boolean personal = r.getBoolean("personal");
                            if (all == true) {
                                total += "all";
                            } else {
                                if (family == true) {
                                    total += "family";
                                    count++;
                                }
                                if (friend == true) {
                                    if (count == 0) total += "friend";
                                    else total += ", friend";
                                }
                                if (work == true) {
                                    if (count == 0) total += "work";
                                    else total += ", work";
                                }
                                if (school == true) {
                                    if (count == 0) total += "school";
                                    else total += ", school";
                                }
                                if (personal == true) {
                                    if (count == 0) total += "personal";
                                    else total += ", personal";
                                }
                            }
                            if (count == 0) total += "None";
                            listAdapter.add(total);
                        }
                    } else {
                        ResultText.setText(currentUser.getString("username") + "'s friends:");
                        listAdapter.add("You have no ignored friends");
                    }

                }
            });

            Unignore.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    final int numEntered;
                    try {
                        numEntered = Integer.parseInt(Number.getText().toString());
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
                    String thisId = friendIds.get(numEntered-1);
                    ParseRelation relation = currentUser.getRelation("Friends");
                    ParseQuery query = relation.getQuery();
                    query.whereEqualTo("friend_id", thisId);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {

                        @Override
                        public void done(ParseObject person, ParseException e) {
                            if(person.getString("status").equals("youIgnoredThem")) {
                                person.put("status", "pending");
                                person.saveInBackground();
                                Toast.makeText(getApplicationContext(), person.getString("username") + " is no longer ignored!",
                                        Toast.LENGTH_LONG).show();
                            }else{
                                person.put("status", "friend");
                                person.saveInBackground();
                                Toast.makeText(getApplicationContext(), "Resuming friendship with "+person.getString("username"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("FriendRequests");
                    query1.whereEqualTo("toUser", currentUser.getObjectId());
                    query1.whereEqualTo("fromUser", thisId);
                    query1.getFirstInBackground(new GetCallback<ParseObject>() {

                        @Override
                        public void done(ParseObject friend, ParseException e) {
                            if(friend.getString("status").equals("ignored")) {
                                friend.put("status", "pending");
                                friend.saveInBackground();
                                //reload page?
                            }else{
                                friend.put("status", "accepted");
                                friend.saveInBackground();
                            }
                        }
                    });
                }
            });
        }
    }

}
