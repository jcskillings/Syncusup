package com.syncusup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Owner on 5/4/2015.
 */

public class EditFriendActivity extends Activity {

    String value;
    ParseObject editFriend;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("EXTRA_SESSION_ID");
        }
        setContentView(R.layout.activity_edit_friend);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final EditText nicknameEdit = (EditText)findViewById(R.id.nickname);
            final EditText Message = (EditText)findViewById(R.id.message);
            final CheckBox allBox = (CheckBox)findViewById(R.id.all);
            final CheckBox friendBox = (CheckBox)findViewById(R.id.friend);
            final CheckBox familyBox = (CheckBox)findViewById(R.id.family);
            final CheckBox workBox = (CheckBox)findViewById(R.id.work);
            final CheckBox schoolBox = (CheckBox)findViewById(R.id.school);
            final CheckBox personalBox = (CheckBox)findViewById(R.id.personal);
            final TextView ResultText = (TextView)findViewById(R.id.ResultTextView);
            final TextView ResultText2 = (TextView)findViewById(R.id.ResultTextView2);
            final TextView ResultText3= (TextView)findViewById(R.id.ResultTextView3);
            Button Save = (Button)findViewById(R.id.Save);
            Button Cancel = (Button)findViewById(R.id.Cancel);

            ParseRelation relation = currentUser.getRelation("Friends");
            ParseQuery query = relation.getQuery();
            query.whereEqualTo("friend_id", value);
            query.getFirstInBackground(new GetCallback<ParseObject>() {

                @Override
                public void done(ParseObject friend, ParseException e) {
                    editFriend = friend;
                    nicknameEdit.setText(friend.getString("nickname"));
                    Message.setText(friend.getString("message"));
                    ResultText.setText("Editing:");
                    ResultText2.setText("User:"+friend.getString("username"));
                    ResultText3.setText("Name:"+friend.getString("name"));
                    if(friend.getBoolean("all")==true)allBox.setChecked(true);
                    if(friend.getBoolean("friend")==true)friendBox.setChecked(true);
                    if(friend.getBoolean("family")==true)familyBox.setChecked(true);
                    if(friend.getBoolean("work")==true)workBox.setChecked(true);
                    if(friend.getBoolean("school")==true)schoolBox.setChecked(true);
                    if(friend.getBoolean("personal")==true)personalBox.setChecked(true);
                }
            });

            Cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    Intent intent = new Intent(getBaseContext(), ViewFriends.class);
                    startActivity(intent);
                }
            });

            Save.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    editFriend.put("nickname", nicknameEdit.getText().toString());
                    editFriend.put("message", Message.getText().toString());
                    editFriend.put("all", allBox.isChecked());
                    editFriend.put("friend", friendBox.isChecked());
                    editFriend.put("family", familyBox.isChecked());
                    editFriend.put("work", workBox.isChecked());
                    editFriend.put("school", schoolBox.isChecked());
                    editFriend.put("personal", personalBox.isChecked());
                    try {
                        editFriend.save();
                    } catch (ParseException e) {
                        Toast.makeText(getApplicationContext(), "issue saving",
                                Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent(getBaseContext(), ViewFriends.class);
                    startActivity(intent);
                }
            });
        }

    }
}
