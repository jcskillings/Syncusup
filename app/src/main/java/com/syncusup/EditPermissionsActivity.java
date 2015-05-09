package com.syncusup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;


public class EditPermissionsActivity extends Activity {
    private TextView friendName;
    private Spinner spinner;
    private Friend friend;
    private String listId;
    private String friendId;
    private List_permissions listPermit;
    private String[] permTypes;
    private String setPermit;
    private Button saveBtn;
    private SyncList list;
    private int originalPermissionInt=0;
    private boolean makingNewList=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_permissions);
        setTitle("Edit Permissions");
        Button saveBtn = (Button) findViewById(R.id.edit_permit_save);
        if (getIntent().hasExtra("friendId")){
            friendId = getIntent().getStringExtra("friendId");
            Log.i("editpermissAct", "friend id passed : "+friendId);
        } else {
            Log.i("editpermissAct", "no friend id passed in extras");
        }
        if (getIntent().hasExtra("listId")){
            listId = getIntent().getStringExtra("listId");
            Log.i("editpermissAct", "list id passed : "+listId);
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
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (makingNewList){
                    // add new relation for list
                    ParseRelation newPermissRel = list.getRelation("permissions");
                    newPermissRel.add(listPermit);
                    // set acl for List_permissions
                    ParseACL permisAcl = new ParseACL();
                    permisAcl.setPublicReadAccess(true);
                    permisAcl.setPublicWriteAccess(true);
                    listPermit.setACL(permisAcl);
                    makingNewList = false;
                }
                if (setPermit.contentEquals("master") || setPermit.contentEquals("editor")){
                    ParseACL acl = list.getACL();
                    acl.setReadAccess(friendId, true);
                    acl.setWriteAccess(friendId, true);
                    list.setACL(acl);
                    list.saveInBackground();
                    listPermit.setPermissionType(setPermit);
                    listPermit.saveInBackground();

                } else if (setPermit.contentEquals("watcher")){
                    ParseACL acl = list.getACL();
                    acl.setReadAccess(friendId, true);
                    acl.setWriteAccess(friendId, false);
                    list.setACL(acl);
                    list.saveInBackground();
                    listPermit.setPermissionType(setPermit);
                    listPermit.saveInBackground();
                } else { // if setPermit is none or some other value
                    // launch confirm to remove user from list
                    new AlertDialog.Builder(EditPermissionsActivity.this)
                            .setTitle("Remove Permission")
                            .setTitle("Remove friend's access to this shared list?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ParseACL acl = list.getACL();
                                    acl.setReadAccess(friendId, false);
                                    acl.setWriteAccess(friendId, false);
                                    list.setACL(acl);
                                    list.saveInBackground();
                                    listPermit.setPermissionType(setPermit);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // reset chosen permission on spinner
                                    spinner.setSelection(originalPermissionInt);
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
                finish();
            }

        }); // end save button
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                //String setPermit = parent.getItemAtPosition(pos);
                Log.i("editpermisact", "pos of selected: "+pos);
                //Log.i("editpermact", "permtypes[pos] "+ permTypes[pos]);
                setPermit = permTypes[pos];

            }
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        // get the friend object
        final ParseQuery<Friend> friendQuery = Friend.getQuery();
        friendQuery.whereEqualTo("friend_id", friendId);
        friendQuery.getFirstInBackground(new GetCallback<Friend>() {

            @Override
            public void done( Friend object, ParseException e) {
                if (!isFinishing()) {
                    friend = object;
                    friendName.setText(friend.getName());
                    getPermissions();
                }
                if (e == null){

                } else {
                    Log.i("eidtPermisAct", e.getMessage());
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
        if (item.getItemId() == R.id.goto_main_menu){
            Intent i = new Intent(this, MenuActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    public void getPermissions(){
        //ParseRelation<List_permissions> listRel = friend.getRelation("friendPermissions");
        ParseQuery<List_permissions> listQuery = List_permissions.getQuery();
        listQuery.whereEqualTo("user_id", friendId);
        listQuery.whereEqualTo("list_id", listId);
        listQuery.getFirstInBackground(new GetCallback<List_permissions>() {

            @Override
            public void done(List_permissions object, ParseException e) {
                if (!isFinishing()) {
                    if (object == null){
                        // friend has no existing permission so create one
                        makingNewList = true;
                        listPermit = new List_permissions();
                        listPermit.setUserName(friend.getUserName());
                        listPermit.setPermissionType("watcher");
                        listPermit.setListId(listId);

                    } else {
                        listPermit = object;
                    }
                        Log.i("editpermistAct", "permis type: " + listPermit.getPermissionType());
                        int p = permissionInt(listPermit.getPermissionType());
                        originalPermissionInt = p;
                        if (p == -1) {
                            Log.i("editpermitAct", "invalid permission type");
                            spinner.setSelection(0);
                        } else {
                            spinner.setSelection(p);
                        }
                        Log.i("editpermistAct", "permission changed to : " + listPermit.getPermissionType());
                        getListObject();

                }
                if (e == null) {


                } else {
                    Log.i("ShareListAct", e.getMessage());
                }
            }

        });
    }
    public void getListObject(){
        ParseQuery<SyncList> syncListParseQuery = SyncList.getQuery();
        syncListParseQuery.whereEqualTo("objectId", listId);
        syncListParseQuery.getFirstInBackground(new GetCallback<SyncList>() {
            @Override
            public void done(SyncList syncList, ParseException e) {
                if (!isFinishing()) {
                    list = syncList;

                }
                if (e == null) {


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
            default:
                ret=-1;
                break;
        }
        return ret;
    }

}
