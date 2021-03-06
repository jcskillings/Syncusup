package com.syncusup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Owner on 5/7/2015.
 */
public class EditEventActivity extends Activity{

    private String value;
    private ParseObject editFriend;
    private String radio;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioButton radioButton5;
    private RadioButton radioButton6;
    private ParseObject currObject;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("EXTRA_SESSION_ID");
            Toast.makeText(getApplicationContext(), value,
                    Toast.LENGTH_LONG).show();
        }
        setContentView(R.layout.activity_add_event);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final EditText eventName = (EditText)findViewById(R.id.eventName);
            final EditText endDate = (EditText)findViewById(R.id.endDate);
            final EditText startDate = (EditText)findViewById(R.id.startDate);
            final EditText startTime = (EditText)findViewById(R.id.startTime);
            final EditText endTime = (EditText)findViewById(R.id.endTime);
            final EditText description = (EditText)findViewById(R.id.description);
            final CheckBox privateBox = (CheckBox)findViewById(R.id.privatebox);
            final CheckBox everyoneBox = (CheckBox)findViewById(R.id.everyone);
            final CheckBox friendBox = (CheckBox)findViewById(R.id.friends);
            final CheckBox familyBox = (CheckBox)findViewById(R.id.family);
            final CheckBox workBox = (CheckBox)findViewById(R.id.work);
            final CheckBox schoolBox = (CheckBox)findViewById(R.id.school);
            final CheckBox personalBox = (CheckBox)findViewById(R.id.personal);
            radioButton1 = (RadioButton)findViewById(R.id.radioButton1);
            radioButton2 = (RadioButton)findViewById(R.id.radioButton2);
            radioButton3 = (RadioButton)findViewById(R.id.radioButton3);
            radioButton4 = (RadioButton)findViewById(R.id.radioButton4);
            radioButton5 = (RadioButton)findViewById(R.id.radioButton5);
            radioButton6 = (RadioButton)findViewById(R.id.radioButton6);

            Button Save = (Button)findViewById(R.id.save);
            Button Cancel = (Button)findViewById(R.id.cancel);
            final TextView creatorId = (TextView)findViewById(R.id.creatorId);
            creatorId.setText(currentUser.getString("username")+"'s");

            ParseRelation relation = currentUser.getRelation("Events");
            ParseQuery query = relation.getQuery();
            query.whereEqualTo("objectId", value);
            List<ParseObject> returned = new ArrayList<>();
            try {
                returned = query.find();
            } catch (ParseException e) {
                Toast.makeText(getApplicationContext(), "There was an error finding event",
                        Toast.LENGTH_LONG).show();
                return;
            }

            ParseObject event = returned.get(0);
            currObject = event;
            eventName.setText(event.getString("name"));
            startTime.setText(event.getString("startTime"));
            endTime.setText(event.getString("endTime"));
            description.setText(event.getString("description"));
            privateBox.setChecked(event.getBoolean("private"));
            personalBox.setChecked(event.getBoolean("personal"));
            everyoneBox.setChecked(event.getBoolean("everyone"));
            workBox.setChecked(event.getBoolean("work"));
            schoolBox.setChecked(event.getBoolean("school"));
            familyBox.setChecked(event.getBoolean("family"));
            friendBox.setChecked(event.getBoolean("friend"));
            if(event.getString("repeat").equals("oneTime")) radioButton1.setChecked(true);
            else if(event.getString("repeat").equals("weekdays")) radioButton2.setChecked(true);
            else if(event.getString("repeat").equals("weekends")) radioButton3.setChecked(true);
            else if(event.getString("repeat").equals("weekly")) radioButton4.setChecked(true);
            else if(event.getString("repeat").equals("biweekly")) radioButton5.setChecked(true);
            else if(event.getString("repeat").equals("monthly")) radioButton6.setChecked(true);
            String sDay;
            String sMonth;
            if(event.getInt("startDay") < 10) sDay = "0"+event.getInt("startDay");
            else sDay = ""+event.getInt("startDay");
            if(event.getInt("startMonth") < 10) sMonth = "0"+event.getInt("startMonth");
            else sMonth = ""+event.getInt("startMonth");
            String eDay;
            String eMonth;
            if(event.getInt("endDay") < 10) eDay = "0"+event.getInt("endDay");
            else eDay = ""+event.getInt("endDay");
            if(event.getInt("endMonth") < 10) eMonth = "0"+event.getInt("endMonth");
            else eMonth = ""+event.getInt("endMonth");
            String sDate = sMonth+"/"+sDay+"/"+event.getInt("startYear");
            String eDate= eMonth+"/"+eDay+"/"+event.getInt("endYear");
            startDate.setText(sDate);
            endDate.setText(eDate);



            Save.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){

                    String EventName = eventName.getText().toString();
                    String StartTime = startTime.getText().toString();
                    String EndTime = endTime.getText().toString();
                    String EndDate = endDate.getText().toString();
                    String permissions;
                    String StartDate = startDate.getText().toString();
                    Integer StartMonth = Integer.parseInt(StartDate.substring(0, 2));
                    Integer StartDay = Integer.parseInt(StartDate.substring(3, 5));
                    Integer StartYear = Integer.parseInt(StartDate.substring(6, StartDate.length()));
                    Integer EndMonth = Integer.parseInt(EndDate.substring(0, 2));
                    Integer EndDay = Integer.parseInt(EndDate.substring(3, 5));
                    Integer EndYear = Integer.parseInt(EndDate.substring(6, EndDate.length()));
                    if(EventName.equals("")){
                        Toast.makeText(getApplicationContext(), "You must enter an event name",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(StartTime.equals("")){
                        StartTime = "00:00";
                    }
                    if(EndTime.equals("")){
                        EndTime = "23:59";
                    }
                    if(EndDate.equals("")){
                        EndDate = startDate.toString();
                    }
                    //if(StartDate.equals("")){
                    //    StartDate = startDate.toString();
                    //}

                    final ParseObject event = new ParseObject("Event");
                    event.put("name", EventName);
                    if(radio == null) radio = "oneTime";
                    event.put("repeat", radio);
                    //event.put("startDate", StartDate);
                    //event.put("endDate", EndDate);
                    event.put("startDay", StartDay);
                    event.put("startMonth", StartMonth);
                    event.put("startYear", StartYear);
                    event.put("endDay", EndDay);
                    event.put("endMonth", EndMonth);
                    event.put("endYear", EndYear);
                    event.put("startTime", StartTime);
                    event.put("endTime", EndTime);
                    event.put("creator", currentUser.getObjectId());
                    event.put("private", privateBox.isChecked());
                    event.put("everyone", everyoneBox.isChecked());
                    event.put("work", workBox.isChecked());
                    event.put("family", familyBox.isChecked());
                    event.put("school", schoolBox.isChecked());
                    event.put("friend", friendBox.isChecked());
                    event.put("personal", personalBox.isChecked());
                    event.put("description", description.getText().toString());

                    try {
                        currObject.delete();
                    } catch (ParseException e) {
                        Toast.makeText(getApplicationContext(), "error removing old copy",
                                Toast.LENGTH_LONG).show();
                    }
                    ParseACL acl = new ParseACL();
                    acl.setPublicReadAccess(true);//should just be for friends, not public
                    acl.setWriteAccess(ParseUser.getCurrentUser(), true);
                    event.setACL(acl);
                    try {
                        event.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ParseRelation relation = currentUser.getRelation("Events");
                    relation.add(event);
                    try {
                        currentUser.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Edit successful",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(EditEventActivity.this, MyCalendarActivity.class);
                    startActivity(intent);
                }
            });

            Cancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick (View v){
                    Intent intent = new Intent(EditEventActivity.this, MyCalendarActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
    /*
    public void onClear(View v){
        radioGroup.clearCheck();
    }

    public void onSubmit(View v){
        RadioButton rb = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        Toast.makeText(getApplicationContext(), rb.getText(),
                Toast.LENGTH_LONG).show();
    }*/

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton1:
                if (checked) {
                    radio = "oneTime";
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                    radioButton4.setChecked(false);
                    radioButton5.setChecked(false);
                    radioButton6.setChecked(false);
                    break;
                }
            case R.id.radioButton2:
                if (checked) {
                    radio="weekdays";
                    radioButton1.setChecked(false);
                    radioButton3.setChecked(false);
                    radioButton4.setChecked(false);
                    radioButton5.setChecked(false);
                    radioButton6.setChecked(false);
                    break;
                }
            case R.id.radioButton3:
                if (checked) {
                    radio="weekends";
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(false);
                    radioButton4.setChecked(false);
                    radioButton5.setChecked(false);
                    radioButton6.setChecked(false);
                    break;
                }
            case R.id.radioButton4:
                if (checked) {
                    radio="weekly";
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                    radioButton5.setChecked(false);
                    radioButton6.setChecked(false);
                    break;
                }
            case R.id.radioButton5:
                if (checked) {
                    radio="biweekly";
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                    radioButton4.setChecked(false);
                    radioButton6.setChecked(false);
                    break;
                }
            case R.id.radioButton6:
                if (checked) {
                    radio="monthly";
                    radioButton1.setChecked(false);
                    radioButton2.setChecked(false);
                    radioButton3.setChecked(false);
                    radioButton4.setChecked(false);
                    radioButton5.setChecked(false);
                    break;
                }
        }
    }
}
