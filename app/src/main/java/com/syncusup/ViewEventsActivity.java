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
public class ViewEventsActivity extends ListActivity{

    ParseObject objects;

    @Override
    public void onStart() {
        super.onStart();
        //UAirship.shared().getAnalytics();
    }
    private String value;
    ArrayList<String> eventIds = new ArrayList<String>();
    private Integer thisMonth;
    private Integer thisDay;
    private Integer thisYear;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("EXTRA_SESSION_ID2");
        }
        setContentView(R.layout.activity_view_events);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1);
            ListView friendlv = (ListView)findViewById(android.R.id.list);
            friendlv.setAdapter(listAdapter);
            final Button Edit = (Button)findViewById(R.id.edit);
            final Button Remove = (Button)findViewById(R.id.remove);
            final EditText eventNumber = (EditText) findViewById(R.id.eventNumber);
            final TextView ResultText = (TextView)findViewById(R.id.textView);
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
            //total = month+"/"+day+"/"+year;

            ParseRelation relation = currentUser.getRelation("Events");
            ParseQuery query = relation.getQuery();
            query.whereEqualTo("startDay", thisDay);
            query.whereEqualTo("startMonth", thisMonth);
            query.whereEqualTo("startYear", thisYear);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() != 0) {
                        //ResultText.setText(currentUser.getString("username") + "'s events:");
                        for (int i = 0; i < objects.size(); i++) {
                            ParseObject r = objects.get(i);
                            eventIds.add(r.getObjectId());
                            String startMonth;
                            String startDay;
                            Integer startYear = r.getInt("startYear");
                            String endDay;
                            String endMonth;
                            Integer endYear = r.getInt("endYear");

                            if(r.getInt("startDay") < 10) startDay = "0"+r.getInt("startDay");
                            else startDay = ""+r.getInt("startDay");
                            if(r.getInt("endDay") < 10) endDay = "0"+r.getInt("endDay");
                            else endDay = ""+r.getInt("endDay");
                            if(r.getInt("startMonth") < 10) startMonth = "0"+r.getInt("startMonth");
                            else startMonth = ""+r.getInt("startMonth");
                            if(r.getInt("endMonth") < 10) endMonth = "0"+r.getInt("endMonth");
                            else endMonth = ""+r.getInt("endMonth");

                            String startDate = startMonth+"/"+startDay+"/"+startYear;
                            String endDate = endMonth+"/"+endDay+"/"+endYear;
                            String startTime = r.getString("startTime");
                            String endTime = r.getString("endTime");
                            String eventName = r.getString("name");


                            if (eventName == null) eventName = "None";
                            int eventNum = i + 1;
                            String total2 = "(#" + eventNum + ") Event Name: " + eventName + "\nDate: " +
                                    startDate + "-" + endDate + "\nTime: " + startTime + "-" + endTime +
                                    "\nViewable By: ";
                            Boolean everyone = r.getBoolean("everyone");
                            Boolean family = r.getBoolean("family");
                            Boolean friend = r.getBoolean("friend");
                            Boolean work = r.getBoolean("work");
                            Boolean school = r.getBoolean("school");
                            Boolean privateBox = r.getBoolean("private");
                            Integer count = 0;
                            Boolean personal = r.getBoolean("personal");
                            if (privateBox) {
                                total2+="nobody, it is private";
                                count++;
                            } else {
                                if (everyone == true) {
                                    total2 += "everyone";
                                    count++;
                                } else {
                                    if (family == true) {
                                        total2 += "family";
                                        count++;
                                    }
                                    if (friend == true) {
                                        if (count == 0) total2 += "friend";
                                        else total2 += ", friend";
                                    }
                                    if (work == true) {
                                        if (count == 0) total2 += "work";
                                        else total2 += ", work";
                                    }
                                    if (school == true) {
                                        if (count == 0) total2 += "school";
                                        else total2 += ", school";
                                    }
                                    if (personal == true) {
                                        if (count == 0) total2 += "personal";
                                        else total2 += ", personal";
                                    }
                                }
                            }
                            if (count == 0) total2 += "Nobody";
                            total2+="\nReoccurs: " + r.getString("repeat");
                            listAdapter.add(total2);
                        }
                    } else {
                        //ResultText.setText(currentUser.getString("username") + "'s events:");
                        listAdapter.add("You have no events for this day!");
                    }
                }
            });

            Edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    final int numEntered;
                    try {
                        numEntered = Integer.parseInt(eventNumber.getText().toString());
                    }
                    catch(Exception e2){
                        Toast.makeText(getApplicationContext(), "You entered invalid input!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(numEntered < 1 || numEntered > eventIds.size() ){
                        Toast.makeText(getApplicationContext(), "You entered an invalid friend number!",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(getBaseContext(), EditEventActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID",
                            eventIds.get(numEntered-1));
                    startActivity(intent);
                }
            });

            Remove.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    if(eventNumber.getText().toString() == "") return;
                    final String thisId;
                    try {
                        thisId = eventIds.get(Integer.parseInt(eventNumber.getText().toString()) - 1).toString();
                    }
                    catch (Exception e2){
                        Toast.makeText(getApplicationContext(), "invalid input",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    ParseRelation relation = currentUser.getRelation("Events");
                    ParseQuery query = relation.getQuery();
                    query.whereEqualTo("objectId", thisId);
                    List<ParseObject> returned = new ArrayList<ParseObject>();
                    try {
                        returned = query.find();
                    } catch (ParseException e) {
                        Toast.makeText(getApplicationContext(), "issue finding",
                                Toast.LENGTH_LONG).show();
                    }
                    try {
                        returned.get(0).delete();
                    } catch (ParseException e) {
                        Toast.makeText(getApplicationContext(), "issue deleting",
                                Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(getApplicationContext(), "Event removed",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getBaseContext(), ViewEventsActivity.class);
                    startActivity(intent);

                }
            });
        }
    }
}
