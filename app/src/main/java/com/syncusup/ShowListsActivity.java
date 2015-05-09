package com.syncusup;


/**
 * Created by Justin on 4/30/2015.
 */
        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Typeface;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.parse.FindCallback;
        import com.parse.ParseAnonymousUtils;
        import com.parse.ParseException;
        import com.parse.ParseObject;
        import com.parse.ParseQuery;
        import com.parse.ParseQueryAdapter;
        import com.parse.ParseUser;
        import com.parse.SaveCallback;


        import java.util.List;

public class ShowListsActivity extends Activity {
    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EDIT_ACTIVITY_CODE = 200;

    // Adapter for the Todos Parse Query
    private ParseQueryAdapter<SyncList> syncListAdapter;

    private LayoutInflater inflater;

    // For showing empty and non-empty todo views
    private ListView showListsView;
    private LinearLayout noListsView;

    private TextView loggedInInfoView;
    private TextView listInfoView;
    private boolean editMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_lists);
        setTitle("Shared Lists");
        // Set up the views
        showListsView = (ListView) findViewById(R.id.show_list_view);
        noListsView = (LinearLayout) findViewById(R.id.no_lists_view);
        showListsView.setEmptyView(noListsView);
        loggedInInfoView = (TextView) findViewById(R.id.loggedin_info);
        //listInfoView = (TextView) findViewById(R.id.list_id);

        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<SyncList> factory = new ParseQueryAdapter.QueryFactory<SyncList>() {
            public ParseQuery<SyncList> create() {
                ParseQuery<SyncList> query = SyncList.getQuery();
                query.whereEqualTo("privateTodos", false);
                query.orderByDescending("createdAt");
                //query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        //inflater = (LayoutInflater) this
        //        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //syncListAdapter = new SyncListAdapter(this, factory);
        inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        syncListAdapter = new SyncListAdapter(this, factory);

        // Attach the query adapter to the view
        ListView syncListView = (ListView) findViewById(R.id.show_list_view);
        syncListView.setAdapter(syncListAdapter);

        syncListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SyncList synclist = syncListAdapter.getItem(position);
                Log.i("showlistsact","onclick, editmode = "+editMode);
                if (editMode == false) {
                    openTodoListView(synclist);
                } else {
                    openEditListView(synclist);
                }
            }
        });
    }
    protected void onResume() {
        super.onResume();
        syncListAdapter.notifyDataSetChanged();
        editMode = false;
        // Check if we have a real user
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // Sync data to Parse
            syncListsToParse();
            loadFromParse();
            // Update the logged in label info
            updateLoggedInInfo();
        }
    }

    private void updateLoggedInInfo() {
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            //Toast.makeText(this, currentUser.getString("username"), Toast.LENGTH_SHORT);

            loggedInInfoView.setText(getString(R.string.logged_in, currentUser.getString("username")));
        } else {
            loggedInInfoView.setText(getString(R.string.not_logged_in));
        }
    }
    // launching a todolist view
    private void openTodoListView(SyncList syncList) {
        Intent i = new Intent(this, TodoListActivity.class);
        i.putExtra("parentListId", syncList.getObjectId());
        //startActivityForResult(i, EDIT_ACTIVITY_CODE);
        startActivity(i);
    }
    private void openEditListView(SyncList syncList){
        Intent i = new Intent(this, NewListActivity.class);
        i.putExtra("parentListId", syncList.getObjectId());
        //startActivityForResult(i, EDIT_ACTIVITY_CODE);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // An OK result means the pinned dataset changed or
        // log in was successful
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_ACTIVITY_CODE) {
                // Coming back from the edit view, update the view
                syncListAdapter.loadObjects();
            } else if (requestCode == LOGIN_ACTIVITY_CODE) {
                // If the user is new, sync data to Parse,
                // else get the current list from Parse
                if (ParseUser.getCurrentUser().isNew()) {
                    syncListsToParse();
                } else {
                    loadFromParse();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_lists, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new) {
            // Make sure there's a valid user, anonymous
            // or regular
            if (ParseUser.getCurrentUser() != null) {
                //Toast.makeText(this, ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT);

                startActivityForResult(new Intent(this, NewListActivity.class),
                        EDIT_ACTIVITY_CODE);
            }
        }
        if (item.getItemId() == R.id.goto_main_menu){
            Intent i = new Intent(this, MenuActivity.class);
            startActivity(i);
        }

        if (item.getItemId() == R.id.action_sync) {
            //syncListsToParse();
            loadFromParse();
        }
        if (item.getItemId() == R.id.action_edit){
            Log.i("showlistsact", "coming in editmode="+editMode);
            if (!editMode) {

                   //TextView editHide = (TextView) findViewById(R.id.edit_hide);
                    //editHide.setVisibility(View.VISIBLE);
                    editMode = true;
                syncListAdapter.notifyDataSetChanged();

            } else {
                //TextView editHide = (TextView) findViewById(R.id.edit_hide);
                //editHide.setVisibility(View.INVISIBLE);
                editMode = false;
                syncListAdapter.notifyDataSetChanged();
            }
        }
        /*if (item.getItemId() == R.id.action_logout) {
            // Log out the current user
            ParseUser.logOut();
            // Create a new anonymous user
            //ParseAnonymousUtils.logIn(null);
            // Update the logged in label info
            updateLoggedInInfo();
            // Clear the view
            todoListAdapter.clear();
            // Unpin all the current objects
            ParseObject
                    .unpinAllInBackground(ParseApplication.TODO_GROUP_NAME);
            startActivity(new Intent(this, WelcomeActivity.class));
        } */


        return super.onOptionsItemSelected(item);
    }






    private void syncListsToParse() {
        // We could use saveEventually here, but we want to have some UI
        // around whether or not the draft has been saved to Parse
        //Toast.makeText(this, "Called SyncListsToParse", Toast.LENGTH_SHORT).show();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                // If we have a network connection and a current logged in user,
                // sync the
                // todos

                // In this app, local changes should overwrite content on the
                // server.

                ParseQuery<SyncList> query = SyncList.getQuery();
                query.fromPin(ParseApplication.SYNC_LIST_GROUP_NAME);
                query.whereEqualTo("isDraft", true);
                query.findInBackground(new FindCallback<SyncList>() {
                    public void done(List<SyncList> syncLists, ParseException e) {
                        if (e == null) {
                            for (final SyncList synclist : syncLists) {
                                // Set is draft flag to false before
                                // syncing to Parse
                                synclist.setDraft(false);
                                synclist.saveInBackground(new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            // Let adapter know to update view
                                            if (!isFinishing()) {
                                                syncListAdapter
                                                        .notifyDataSetChanged();
                                            }
                                        } else {
                                            // Reset the is draft flag locally
                                            // to true
                                            synclist.setDraft(true);
                                        }
                                    }

                                });

                            }
                        } else {
                            Log.i("ShowListActivity",
                                    "syncListsToParse: Error finding pinned syncLists: "
                                            + e.getMessage());
                        }
                    }
                });
            } else {
                // If we have a network connection but no logged in user, direct
                // the person to log in or sign up.

                //ParseLoginBuilder builder = new ParseLoginBuilder(this);
                //startActivityForResult(builder.build(), LOGIN_ACTIVITY_CODE);

                // try replacing with
                // Start an intent for the dispatch activity
                Intent loginIntent = new Intent(ShowListsActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);

            }
        } else {
            // If there is no connection, let the user know the sync didn't
            // happen
            Toast.makeText(
                    getApplicationContext(),
                    "Your device appears to be offline. Some lists may not have been synced to Parse.",
                    Toast.LENGTH_LONG).show();
        }
    }
    private void loadFromParse() {
        //Toast.makeText(this, "Loading From Parse", Toast.LENGTH_SHORT).show();
        ParseQuery<SyncList> query = SyncList.getQuery();
        query.whereEqualTo("creator", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<SyncList>() {
            public void done(List<SyncList> synclists, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground((List<SyncList>) synclists,
                            new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        if (!isFinishing()) {
                                            syncListAdapter.loadObjects();
                                        }
                                    } else {
                                        Log.i("ShowListsActivity",
                                                "Error pinning syncLists: "
                                                        + e.getMessage());
                                    }
                                }
                            });
                } else {
                    Log.i("ShowListsActivity",
                            "loadFromParse: Error finding pinned syncLists: "
                                    + e.getMessage());
                }
            }
        });
    }

    private class SyncListAdapter extends ParseQueryAdapter<SyncList> {

        public SyncListAdapter(Context context,
                               ParseQueryAdapter.QueryFactory<SyncList> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(SyncList synclist, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.sync_list, parent, false);
                holder = new ViewHolder();
                holder.listName = (TextView) view
                        .findViewById(R.id.list_name);
                holder.editHide = (TextView) view.findViewById(R.id.edit_hide);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            TextView listNameView = holder.listName;
            TextView editHide = holder.editHide;
            listNameView.setText(synclist.getName());
            if (synclist.isDraft()) {
                listNameView.setTypeface(null, Typeface.BOLD_ITALIC);
            } else {
                listNameView.setTypeface(null, Typeface.BOLD);
            }
            if (editMode){
                editHide.setVisibility(View.VISIBLE);
            } else {
                editHide.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }

    private static class ViewHolder {
        TextView listName;
        TextView editHide;
    }
}
