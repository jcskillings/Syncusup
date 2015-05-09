package com.syncusup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.List;


public class ShareListActivity extends Activity {
    private LayoutInflater inflater;
    private ParseQueryAdapter<Friend> friendsAdapter;
    private ListView friendsListView;
    private String listId;
    private List_permissions listPermit;
    private TextView listName;
    private SyncList list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Share List");
        setContentView(R.layout.activity_share_list);
        ParseUser currentUser = ParseUser.getCurrentUser();
        listId = getIntent().getStringExtra("listId");
        // Set up the views
        friendsListView = (ListView) findViewById(R.id.friend_list_view);
        final ParseRelation relation = currentUser.getRelation("Friends");

        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<Friend> factory = new ParseQueryAdapter.QueryFactory<Friend>() {
            public ParseQuery<Friend> create() {
                ParseQuery<Friend> query = relation.getQuery();
                //query.include("friendPermissions");
                query.whereEqualTo("status", "friend");
                //query.orderByDescending("createdAt");
                //query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friendsAdapter = new FriendsAdapter(this, factory);

        // Attach the query adapter to the view
        ListView friendsListView = (ListView) findViewById(R.id.friend_list_view);
        friendsListView.setAdapter(friendsAdapter);
        listName = (TextView) findViewById(R.id.list_info);

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Friend friend = friendsAdapter.getItem(position);
                openEditPermissions(friend);
            }
        });
        ParseQuery<SyncList> listQuery = SyncList.getQuery();
        listQuery.whereEqualTo("objectId", listId);
        listQuery.getFirstInBackground(new GetCallback<SyncList>() {
            @Override
            public void done(SyncList syncList, ParseException e) {
                if (e == null) {
                    if (!isFinishing()) {
                        list = syncList;
                        listName.setText(list.getName());
                    }
                } else {
                    Log.i("TodoListActivity",
                            "setListProperties: Error finding list: "
                                    + e.getMessage());
                }
            }
        });


    } // end oncreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_share_list, menu);
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

    private class FriendsAdapter extends ParseQueryAdapter<Friend> {

        public FriendsAdapter(Context context,
                                  ParseQueryAdapter.QueryFactory<Friend> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(Friend friend, View view, ViewGroup parent) {
            ViewHolder holder;

            if (view == null) {
                //get user and relation to listpermission
                view = inflater.inflate(R.layout.list_item_friend, parent, false);
                holder = new ViewHolder();
                holder.friendName = (TextView) view.findViewById(R.id.friend_name);
                holder.permissionType = (TextView) view.findViewById(R.id.friend_permission_type);
                holder.checkmark = (ImageView) view.findViewById(R.id.img_has_permission);
                //holder.editButton = (Button) view.findViewById(R.id.todo_edit_button);
                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }
            final TextView friendName = holder.friendName;
            final TextView permissionType = holder.permissionType;
            final ImageView checkmark = holder.checkmark;
            //ParseObject parentlist = new ParseObject("list");
            //parentlist = to-do.getParentList();
            //String parentName = parentlist.getName();
            //ParseUser user = f.getWhoCreated();
            ParseRelation relatePermissions = friend.getRelation("friendPermissions");
            ParseQuery<List_permissions> query = relatePermissions.getQuery();
            query.whereEqualTo("list_id", listId);
            query.getFirstInBackground(new GetCallback<List_permissions>() {

                @Override
                public void done( List_permissions object, ParseException e) {
                    if (!isFinishing()) {
                        listPermit = object;
                        if (listPermit == null){
                            Log.i("sharelistact", "no permission found (null)");
                            permissionType.setText("none");
                            checkmark.setVisibility(View.INVISIBLE);
                        } else {
                            //listPermit = (List_permissions) permitList;
                            //listPermit = permitList.get(0);
                            checkmark.setVisibility(View.VISIBLE);
                            Log.i("sharelistact", "list permit info: "+listPermit.getUserName()+" , "+listPermit.getPermissionType());
                            permissionType.setText(listPermit.getPermissionType());

                        }
                    }
                    if (e == null){

                    } else {
                        Log.i("ShareListAct", e.getMessage());
                    }
                }

            });


            friendName.setText(friend.getUserName() );

            return view;
        }
    }
    private static class ViewHolder {
        TextView friendName;
        TextView permissionType;
        ImageView checkmark;
    }
    private void openEditPermissions(Friend friend){
        Bundle extras = new Bundle();
        extras.putString("friendId", friend.getFriendId());
        extras.putString("listId", listId);
        Intent i = new Intent(this, EditPermissionsActivity.class);
        i.putExtras(extras);
        startActivity(i);
    }



}
