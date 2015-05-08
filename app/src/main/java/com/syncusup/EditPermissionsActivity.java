package com.syncusup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;


public class EditPermissionsActivity extends Activity {
    private TextView friendName;
    private Spinner spinner;
    private Friend friend;
    private String listId;
    private String friendId;
    private List_permissions listPermit;
    private String[] permTypes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_permissions);
        if (getIntent().hasExtra("friendId")){
            friendId = getIntent().getStringExtra("friendId");
        } else {
            Log.i("editpermissAct", "no friend id passed in extras");
        }
        if (getIntent().hasExtra("listId")){
            listId = getIntent().getStringExtra("listId");
        } else {
            Log.i("editpermissAct", "no list id passed in extras");
        }
        spinner = (Spinner) findViewById(R.id.permission_spinner);
        friendName = (TextView) findViewById(R.id.friend_name_txt);
        permTypes = getResources().getStringArray(R.array.permissions_array);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.permissions_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        // get the friend object
        final ParseQuery<Friend> friendQuery = Friend.getQuery();
        friendQuery.whereEqualTo("friendId", friendId);
        friendQuery.getFirstInBackground(new GetCallback<Friend>() {

            @Override
            public void done( Friend object, ParseException e) {
                if (!isFinishing()) {
                    return;
                }
                if (e == null){
                    friend = object;
                    friendName.setText(friend.getName());
                    getPermissions();
                } else {
                    Log.i("ShareListAct", e.getMessage());
                }
            }

        });
    } // end oncreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_permissions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void getPermissions(){
        //ParseRelation<List_permissions> listRel = friend.getRelation("friendPermissions");
        ParseQuery<List_permissions> listQuery = List_permissions.getQuery();
        listQuery.whereEqualTo("friendId", friendId);
        listQuery.whereEqualTo("listId", listId);
        listQuery.getFirstInBackground(new GetCallback<List_permissions>() {

            @Override
            public void done(List_permissions object, ParseException e) {
                if (!isFinishing()) {
                    return;
                }
                if (e == null) {
                    listPermit = object;
                    int p = permissionInt(listPermit.getPermissionType());
                    spinner.setSelection(p);
                } else {
                    Log.i("ShareListAct", e.getMessage());
                }
            }

        });
    }
    public int permissionInt(String permType){
        int ret = -1;
        switch (permType){
            case "none":
                ret = 0;
                break;
            case "master":
                ret= 1;
                break;
            case "editor":
                ret= 2;
                break;
            case "watcher":
                ret= 3;
                break;
        }
        return ret;
    }

}
