package com.syncusup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseAnonymousUtils;
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
    private Button deleteButton;
    private Friend myFriend;
    private List_permissions lp = new List_permissions();
    private Spinner spinner;
    private TextView creator;
    private TextView userPermis;
    private List_permissions userLp;

    private LayoutInflater inflater;
    private ParseQueryAdapter<List_permissions> permissionsAdapter;
    ParseUser currentUser = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit List");
        setContentView(R.layout.activity_new_list);
        // Fetch the todoId from the Extra data
        if (getIntent().hasExtra("parentListId")) {
            synclistId = getIntent().getExtras().getString("parentListId");
            Log.i("newlistact", "extra sync list id: "+synclistId);
        }
        // set up views
        permissionListView = (ListView) findViewById(R.id.shared_with);
        listName = (EditText) findViewById(R.id.list_name);
        saveButton = (Button) findViewById(R.id.saveButton);
        shareButton = (Button) findViewById(R.id.share_list_button);
        //deleteButton = (Button) findViewById(R.id.deleteButton);
        listInfoView = (TextView) findViewById(R.id.synclist_id);
        creator = (TextView) findViewById(R.id.list_creator);
        userPermis= (TextView) findViewById(R.id.your_permissions);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<List_permissions> factory = new ParseQueryAdapter.QueryFactory<List_permissions>() {
            public ParseQuery<List_permissions> create() {
                ParseQuery<List_permissions> query = List_permissions.getQuery();
                query.orderByDescending("createdAt");
                //query.fromLocalDatastore();
                query.whereEqualTo("list_id", synclistId);
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        permissionsAdapter = new PermissionsAdapter(this, factory);
        // Attach the query adapter to the view
        permissionListView.setAdapter(permissionsAdapter);

        permissionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // only allow editing sharing for creator and master
                if (synclist.getCreator() == currentUser ||userLp.getPermissionType().contentEquals("master")) {
                    List_permissions permissions = permissionsAdapter.getItem(position);
                    Intent edit = new Intent(NewListActivity.this, EditPermissionsActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("friendId", permissions.getUserId());
                    extras.putString("listId", synclistId);
                    edit.putExtras(extras);
                    startActivity(edit);
                }



            }
        });
        if (synclistId == null) {
            synclist = new SyncList();
            synclist.setUuidString();
        } else {
            listInfoView.setText(synclistId);
            ParseQuery<SyncList> query = SyncList.getQuery();
            query.whereEqualTo("objectId", synclistId);
            query.include("creator");
            query.getFirstInBackground(new GetCallback<SyncList>() {

                @Override
                public void done(SyncList object, ParseException e) {
                    if (!isFinishing()) {
                        synclist = object;
                        if (synclist.isPrivate()){
                            //don't let user change name of private todos
                            listName.setFocusable(false);
                            listName.setFocusableInTouchMode(false);
                        }
                        Log.i("newlistact", "synlistnamt: "+synclist.getName());
                        Log.i("newlistAct", "creator name: "+synclist.getCreator().getUsername());
                        listName.setText(synclist.getName());
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

    } // end on create

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
        //List_permissions lp = new List_permissions();
        //Friend friend;
        //testing to add a new list permission////////////////////////////////
        /*ParseQuery<Friend> query = Friend.getQuery();
        query.whereEqualTo("objectId", "pS3BeskLHY");
        query.getFirstInBackground(new GetCallback<Friend>() {
            public void done(Friend friend, ParseException e) {
                if (e == null) {
                    if (!isFinishing()) {
                        myFriend = friend;
                        final List_permissions lp = new List_permissions();
                        Log.i("newlistAct", "getfriendCallback friendname: "+friend.getUserName());
                        //lp.setListPointer(synclist);
                        if (synclist == null) {
                            Log.i("newlistAct", "synclist nULL?: ");
                        } else {
                            Log.i("newlistact", "synclist id:"+synclist.getObjectId());
                        }
                        //ParseRelation rel = lp.getRelation("listPointer");
                        //lp.setListPointer(synclist);
                        lp.setListId(synclist.getObjectId());
                        lp.setUserName(myFriend.getUserName());
                        lp.setPermissionType("editor");
                        lp.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {

                                if (isFinishing()) {
                                    return;
                                }
                                if (e == null) {
                                    ParseRelation rel = synclist.getRelation("permissions");
                                    rel.add(lp);
                                    synclist.saveInBackground( new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                          if (isFinishing()){
                                                return;
                                           }
                                           if (e == null){
                                               ParseRelation r = myFriend.getRelation("friendPermissions");
                                               r.add(lp);
                                               myFriend.saveInBackground();
                                           } else {
                                               Log.i("newlistact", e.getMessage());
                                           }
                                        }
                                    });
                                    rel = myFriend.getRelation("friendPermissions");
                                    rel.add(lp);
                                    myFriend.saveInBackground();


                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Error saving: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    Log.i("newlistact", e.getMessage());
                                }
                            }
                        });

                    }
                } else {
                    Log.i("NewListActivity",
                            "loadFromParse: Error finding  friend: "
                                    + e.getMessage());
                }
            }
        }); */

        Intent i = new Intent(this, ShareListActivity.class);
        i.putExtra("listId", synclistId);
        startActivity(i);
    }
    private void getPermissions(){
        ParseQuery<List_permissions> query = List_permissions.getQuery();
        //query.include("parentList");
        query.whereEqualTo("list_id", synclistId);
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
                            "loadFromParse: Error finding  permissions: "
                                    + e.getMessage());
                }
            }
        });
    }

    private void setListProperties(){
        if (currentUser == synclist.getCreator()){
            // current user is creator don't query for permissions
            userPermis.setText(getString(R.string.your_list_permissions, "creator"));
            creator.setText(getString(R.string.list_creator, currentUser.getUsername()));
            deleteButton.setVisibility(View.VISIBLE);

        } else {
            ParseQuery<List_permissions> userPermisQuery = List_permissions.getQuery();
            userPermisQuery.whereEqualTo("list_id", synclistId);
            userPermisQuery.whereEqualTo("user_id", currentUser.getObjectId());

            userPermisQuery.getFirstInBackground(new GetCallback<List_permissions>() {
                @Override
                public void done(List_permissions permissions, ParseException e) {
                    if (e == null) {
                        if (!isFinishing()) {
                            userLp = permissions;
                            listName.setText(synclist.getName());
                            creator.setText(getString(R.string.list_creator, synclist.getCreator().getUsername()));
                            userPermis.setText(getString(R.string.your_list_permissions, userLp.getPermissionType() ));
                            if (userLp.getPermissionType().contentEquals("watcher")){
                                listName.setFocusable(false);
                                listName.setFocusableInTouchMode(false);
                                saveButton.setVisibility(View.INVISIBLE);
                                shareButton.setVisibility(View.INVISIBLE);

                            }


                        }
                    } else {
                        Log.i("TodoListActivity",
                                "setListProperties: Error finding  user permission: "
                                        + e.getMessage());
                    }
                }
            });
        }
    } // end setListProperties

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
    protected void onResume() {
        super.onResume();
        permissionsAdapter.notifyDataSetChanged();
        // Check if we have a real user

    }
   @Override
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
        if (item.getItemId() == R.id.goto_main_menu){
            Intent i = new Intent(this, MenuActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}
