package com.syncusup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;


public class ShareListActivity extends Activity {
    private LayoutInflater inflater;
    private ParseQueryAdapter<Friend> friendsAdapter;
    private ListView friendsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_list);
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Set up the views
        friendsListView = (ListView) findViewById(R.id.friend_list_view);
        final ParseRelation relation = currentUser.getRelation("Friends");

        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<Friend> factory = new ParseQueryAdapter.QueryFactory<Friend>() {
            public ParseQuery<Friend> create() {
                ParseQuery<Friend> query = relation.getQuery();
                query.whereEqualTo("status", "friend");
                //query.orderByDescending("createdAt");
                //query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        friendsAdapter = new FriendsAdapter(this, factory);

        // Attach the query adapter to the view
        ListView todoListView = (ListView) findViewById(R.id.todo_list_view);
        friendsListView.setAdapter(friendsAdapter);

        friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Friend friend = friendsAdapter.getItem(position);
            }
        });
    }


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
                view = inflater.inflate(R.layout.list_item_friend, parent, false);
                holder = new ViewHolder();
                holder.friendName = (TextView) view.findViewById(R.id.friend_name);
                holder.permissionType = (TextView) view.findViewById(R.id.friend_permission_type);
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
            friendName.setText(friend.getUserName() );

            return view;
        }
    }
    private static class ViewHolder {
        TextView friendName;
        TextView permissionType;
    }
}
