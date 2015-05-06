package com.syncusup;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
/**
 * Created by Owner on 5/1/2015.
 */
public class ViewFriends extends ListActivity{
    private static final String TAG = null;

    ParseObject objects;

    @Override
    public void onStart() {
        super.onStart();
        //UAirship.shared().getAnalytics();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friends);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1);
            ListView friendlv = (ListView)findViewById(android.R.id.list);
            friendlv.setAdapter(listAdapter);

            ParseRelation relation = currentUser.getRelation("Friends");
            ParseQuery query = relation.getQuery();
            query.whereEqualTo("status", "friend");
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if(objects.size() != 0){
                        listAdapter.add(currentUser.getString("username")+"'s friends:");
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject r = objects.get(i);
                            String username = r.getString("username");
                            String nickname = r.getString("nickname");
                            String name = r.getString("name");
                            String message = r.getString("message");
                            if (nickname.equals("")) nickname = "None";
                            if (name.equals("")) name = "None";
                            if (message.equals("")) message = "None";
                            int friendNum = i + 1;
                            String total = friendNum + ".) Username: "+username + "\nName: " +
                                    name + "\nNickname: "+ nickname +"\nMessageToThem: "+message+
                                    "\nPermissions: ";
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
                            if(count == 0)total+="None";
                            listAdapter.add(total);
                        }
                    }
                    else{
                        listAdapter.add("You don't have friends yet");
                    }

                }
            });
        }

    }

}
