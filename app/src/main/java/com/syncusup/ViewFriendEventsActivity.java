package com.syncusup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
public class ViewFriendEventsActivity extends ListActivity{

    ParseObject objects;

    @Override
    public void onStart() {
        super.onStart();
        //UAirship.shared().getAnalytics();
    }

    private String value;
    private List valueArray = new ArrayList<String>();
    ArrayList eventIds = new ArrayList();
    private Integer thisMonth;
    private Integer thisDay;
    private Integer thisYear;
    private final HashMap<String, Integer> idMap = new HashMap<String, Integer>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("EXTRA_SESSION_ID3");
            valueArray = Arrays.asList(value.split(","));
        }
        value = valueArray.get(0).toString();
        final String thisUser = valueArray.get(1).toString();
        setContentView(R.layout.activity_view_friend_events);
        final TextView ResultText = (TextView)findViewById(R.id.textView4);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1);
            ListView friendlv = (ListView)findViewById(android.R.id.list);
            friendlv.setAdapter(listAdapter);

            String day = value.substring(0, 2);
            String month = value.substring(3, value.length()-5);
            String year = value.substring(value.length()-4, value.length());

            if(month.equals("January")) month = "01";
            else if(month.equals("February")) month = "02";
            else if(month.equals("March")) month = "03";
            else if(month.equals("April")) month = "04";
            else if(month.equals("May")) month = "05";
            else if(month.equals("June")) month = "06";
            else if(month.equals("July")) month = "07";
            else if(month.equals("August")) month = "08";
            else if(month.equals("September")) month = "09";
            else if(month.equals("October")) month = "10";
            else if(month.equals("November")) month = "11";
            else if(month.equals("December")) month = "12";
            thisDay = Integer.parseInt(day);
            thisMonth = Integer.parseInt(month);
            thisYear = Integer.parseInt(year);

            final ParseQuery query1 = ParseUser.getQuery();
            query1.whereEqualTo("objectId", thisUser);
            query1.getFirstInBackground(new GetCallback<ParseObject>() {

                @Override
                public void done(ParseObject person, ParseException e) {
                    ResultText.setText(person.getString("username")+"'s events for " +
                            thisMonth+"/"+thisDay+"/"+thisYear+":");
                }
            });

            if (valueArray.size() > 2) {
                for (int j = 2; j < valueArray.size(); j++) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
                    query.whereEqualTo("creator", thisUser);
                    query.whereEqualTo("startDay", thisDay);
                    query.whereEqualTo("startMonth", thisMonth);
                    query.whereEqualTo("startYear", thisYear);

                    if (valueArray.get(2).toString().equals("everyone")) {
                        query.findInBackground(new FindCallback<ParseObject>() {

                            @Override
                            public void done(List<ParseObject> events, com.parse.ParseException e) {
                                if (events.size() > 0) {
                                    for (int i = 0; i < events.size(); i++) {
                                        ParseObject r = events.get(i);
                                        if(r.getBoolean("private"))continue;
                                        String startMonth;
                                        String startDay;
                                        Integer startYear = r.getInt("startYear");
                                        String endDay;
                                        String endMonth;
                                        Integer endYear = r.getInt("endYear");

                                        if (r.getInt("startDay") < 10)
                                            startDay = "0" + r.getInt("startDay");
                                        else startDay = "" + r.getInt("startDay");
                                        if (r.getInt("endDay") < 10)
                                            endDay = "0" + r.getInt("endDay");
                                        else endDay = "" + r.getInt("endDay");
                                        if (r.getInt("startMonth") < 10)
                                            startMonth = "0" + r.getInt("startMonth");
                                        else startMonth = "" + r.getInt("startMonth");
                                        if (r.getInt("endMonth") < 10)
                                            endMonth = "0" + r.getInt("endMonth");
                                        else endMonth = "" + r.getInt("endMonth");

                                        String startDate = startMonth + "/" + startDay + "/" + startYear;
                                        String endDate = endMonth + "/" + endDay + "/" + endYear;
                                        String startTime = r.getString("startTime");
                                        String endTime = r.getString("endTime");
                                        String eventName = r.getString("name");
                                        if (eventName == null) eventName = "None";
                                        int eventNum = i + 1;
                                        String total2 = "(#" + eventNum + ") Event Name: " + eventName + "\nDate: " +
                                                startDate + "-" + endDate + "\nTime: " + startTime + "-" + endTime +
                                                "\nReoccurs: " + r.getString("repeat");
                                        listAdapter.add(total2);
                                    }
                                }
                                else {
                                    listAdapter.add("There are no events for this day");
                                }
                            }
                        });
                        break;
                    }
                    else {
                        String perm = valueArray.get(j).toString();
                        query.whereEqualTo(perm, true);
                        query.findInBackground(new FindCallback<ParseObject>() {

                            @Override
                            public void done(List<ParseObject> events, com.parse.ParseException e) {
                                if (events.size() > 0) {
                                    for (int i = 0; i < events.size(); i++) {
                                        if (events.get(i).getBoolean("private")) continue;
                                        String id = events.get(i).getObjectId();
                                        if (idMap.containsKey(id)) continue;
                                        else {
                                            idMap.put(id, 1);
                                        }
                                        ParseObject r = events.get(i);

                                        String startMonth;
                                        String startDay;
                                        Integer startYear = r.getInt("startYear");
                                        String endDay;
                                        String endMonth;
                                        Integer endYear = r.getInt("endYear");

                                        if (r.getInt("startDay") < 10)
                                            startDay = "0" + r.getInt("startDay");
                                        else startDay = "" + r.getInt("startDay");
                                        if (r.getInt("endDay") < 10)
                                            endDay = "0" + r.getInt("endDay");
                                        else endDay = "" + r.getInt("endDay");
                                        if (r.getInt("startMonth") < 10)
                                            startMonth = "0" + r.getInt("startMonth");
                                        else startMonth = "" + r.getInt("startMonth");
                                        if (r.getInt("endMonth") < 10)
                                            endMonth = "0" + r.getInt("endMonth");
                                        else endMonth = "" + r.getInt("endMonth");

                                        String startDate = startMonth + "/" + startDay + "/" + startYear;
                                        String endDate = endMonth + "/" + endDay + "/" + endYear;
                                        String startTime = r.getString("startTime");
                                        String endTime = r.getString("endTime");
                                        String eventName = r.getString("name");

                                        if (eventName == null) eventName = "None";
                                        int eventNum = i + 1;
                                        String total2 = "(#" + eventNum + ") Event Name: " + eventName + "\nDate: " +
                                                startDate + "-" + endDate + "\nTime: " + startTime + "-" + endTime +
                                                "\nReoccurs: " + r.getString("repeat");
                                        listAdapter.add(total2);
                                    }
                                }
                                else {
                                    listAdapter.add("No events to show for this day");
                                }
                            }
                        });
                    }
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "No objects found",
                        Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(getApplicationContext(), "No user signed in",
                    Toast.LENGTH_LONG).show();
        }
    }
}
