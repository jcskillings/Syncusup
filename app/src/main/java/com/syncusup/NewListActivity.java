package com.syncusup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class NewListActivity extends Activity {
    private Button saveButton;
    //private Button deleteButton;
    private EditText listName;
    private SyncList synclist;
    private String synclistId = null;
    private TextView listInfoView;
    private ListView permissionListView;
    private Button shareButton;
    private Friend myFriend;

    private LayoutInflater inflater;
    private ParseQueryAdapter<List_permissions> permissionsAdapter;
    ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_list);
        // Fetch the todoId from the Extra data
        if (getIntent().hasExtra("ID")) {
            synclistId = getIntent().getExtras().getString("ID");
        }

        permissionListView = (ListView) findViewById(R.id.shared_with);
        listName = (EditText) findViewById(R.id.list_name);
        saveButton = (Button) findViewById(R.id.saveButton);
        shareButton = (Button) findViewById(R.id.share_list_button);
        //deleteButton = (Button) findViewById(R.id.deleteButton);
        listInfoView = (TextView) findViewById(R.id.synclist_id);


        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<List_permissions> factory = new ParseQueryAdapter.QueryFactory<List_permissions>() {
            public ParseQuery<List_permissions> create() {
                ParseQuery<List_permissions> query = List_permissions.getQuery();
                //query.orderByDescending("createdAt");
                //query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        permissionsAdapter = new PermissionsAdapter(this, factory);
        if (synclistId == null) {
            synclist = new SyncList();
            synclist.setUuidString();
        } else {
            listInfoView.setText(synclistId);
            ParseQuery<SyncList> query = SyncList.getQuery();
            query.fromLocalDatastore();
            query.whereEqualTo("uuid", synclistId);
            query.getFirstInBackground(new GetCallback<SyncList>() {

                @Override
                public void done(SyncList object, ParseException e) {
                    if (!isFinishing()) {
                        synclist = object;
                        setListProperties();

                        //deleteButton.setVisibility(View.VISIBLE);
                    }
                }

            });

        }

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                synclist.setName(listName.getText().toString());
                synclist.setDraft(true);
                synclist.setCreator(ParseUser.getCurrentUser());
                synclist.pinInBackground(ParseApplication.SYNC_LIST_GROUP_NAME,
                        new SaveCallback() {

                            @Override
                            public void done(ParseException e) {
                                if (isFinishing()) {
                                    return;
                                }
                                if (e == null) {
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }

                        });
            }

        });
        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                shareList();
            }
        });
        // temporarily disable delteing lists
        /*deleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // The todo will be deleted eventually but will
                // immediately be excluded from query results.
                todo.deleteEventually();
                setResult(Activity.RESULT_OK);
                finish();
            }

        }); */


    }

    private void shareList(){

        /*ParseQuery query = Friend.getQuery();
        query.whereEqualTo("objectId", "F7aD9iG79H");
        query.getFirstInBackground(new GetCallback<Friend>() {

            @Override
            public void done(Friend friend, ParseException e) {
                if (!isFinishing()) {
                    myFriend = friend;
                    ParseRelation relation = currentUser.getRelation("Friends");
                    relation.add(friend);
                    currentUser.saveInBackground();

                }
            }

        }); */

        // create some friends
        /*final Friend friend = new Friend();
        friend.setFriendId("wOhxE3IBGv");
        friend.setUsername("kyle");
        friend.setName("kyle schenk");
        friend.setNickname("krazy kyle");
        friend.setAll(true);
        friend.setMessage("faked myself a friend");
        friend.setStatus("friend");
        friend.saveInBackground(
                new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (isFinishing()) {
                            ParseRelation relation = currentUser.getRelation("Friends");
                            relation.add(friend);
                            currentUser.saveInBackground();
                            return;
                        }
                        if (e == null) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Error saving: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                }); */
        Intent i = new Intent(this, ShareListActivity.class);
        startActivity(i);
    }
    private void getPermissions(){
        ParseQuery<List_permissions> query = List_permissions.getQuery();
        //query.include("parentList");
        //query.whereEqualTo("parentList", synclist);
        query.findInBackground(new FindCallback<List_permissions>() {
            public void done(List<List_permissions> permissions, ParseException e) {
                if (e == null) {
                    if (!isFinishing()) {
                        //todoListAdapter.notifyDataSetChanged();
                        permissionsAdapter.loadObjects();
                        //todoListAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.i("TodoListActivity",
                            "loadFromParse: Error finding  todos: "
                                    + e.getMessage());
                }
            }
        });
    }

    private void setListProperties(){
        listName.setText(synclist.getName());


    }

    private class PermissionsAdapter extends ParseQueryAdapter<List_permissions> {

        public PermissionsAdapter(Context context,
                               ParseQueryAdapter.QueryFactory<List_permissions> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(List_permissions permission, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_permissions, parent, false);
                holder = new ViewHolder();
                holder.friendName = (TextView) view.findViewById(R.id.friend_name);
                holder.permissionType = (TextView) view.findViewById(R.id.permission_type);
                //holder.editButton = (Button) view.findViewById(R.id.todo_edit_button);
                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }
            TextView friendName = holder.friendName;
            TextView permissionType = holder.permissionType;
            //ParseObject parentlist = new ParseObject("list");
            //parentlist = to-do.getParentList();
            //String parentName = parentlist.getName();
            //ParseUser user = f.getWhoCreated();
            friendName.setText(permission.getUserName() );
            permissionType.setText(permission.getPermissionType());
            return view;
        }
    }
    private static class ViewHolder {
        TextView friendName;
        TextView permissionType;
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_list, menu);
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
    } */
}
