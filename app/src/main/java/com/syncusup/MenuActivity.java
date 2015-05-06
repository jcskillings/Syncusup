package com.syncusup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MenuActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    } public void onClick(View v){
        switch (v.getId()){
            case R.id.todo_button:
                Intent Todo = new Intent(this, TodoListActivity.class);
                startActivity(Todo);
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
            //Add other buttons here
        }
    }

}
