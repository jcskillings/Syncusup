package com.syncusup;
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
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.List;
/*
public class Notifications extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);



    }
}
*/

public class Notifications extends Activity {
    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EDIT_ACTIVITY_CODE = 200;

    // Adapter for the Todos Parse Query
    private ParseQueryAdapter<Notif> aNotifAdapter;

    private LayoutInflater inflater;

    // For showing empty and non-empty todo views
    private ListView NotifView;
    private LinearLayout noNotifView;

    private TextView loggedInInfoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Set up the views
        NotifView = (ListView) findViewById(R.id.notif_view);
        noNotifView = (LinearLayout) findViewById(R.id.no_notif_view);
        NotifView.setEmptyView(noNotifView);
        loggedInInfoView = (TextView) findViewById(R.id.loggedin_info);

        // Set up the Parse query to use in the adapter

        ParseQueryAdapter.QueryFactory<Notif> factory = new ParseQueryAdapter.QueryFactory<Notif>() {
            public ParseQuery<Notif> create() {
                ParseQuery<Notif> query = Notif.getQuery();
                query.orderByDescending("createdAt");
                query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        aNotifAdapter = new NotifAdapter(this, factory);

        // Attach the query adapter to the view
        ListView NotifView = (ListView) findViewById(R.id.notif_view);
        NotifView.setAdapter(aNotifAdapter);

        NotifView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Notif notif = aNotifAdapter.getItem(position);
                openEditView(notif);
            }
        });

    }
    protected void onResume() {
        super.onResume();
        // Check if we have a real user
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // Sync data to Parse
            syncNotifsToParse();
            // Update the logged in label info
            updateLoggedInInfo();
        }
    }

    private void updateLoggedInInfo() {
        if(ParseUser.getCurrentUser() != null) {
            if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                //Toast.makeText(this, currentUser.getString("username"), Toast.LENGTH_SHORT);

                loggedInInfoView.setText(getString(R.string.logged_in, currentUser.getString("username")));
            } else {
                loggedInInfoView.setText(getString(R.string.not_logged_in));
            }
        }else{
            loggedInInfoView.setText(getString(R.string.not_logged_in));
        }
    }

    private void openEditView(Notif notif) {
        Intent i = new Intent(this, ViewNotification.class);
        i.putExtra("ID", notif.getUuidString());
        startActivityForResult(i, EDIT_ACTIVITY_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // An OK result means the pinned dataset changed or
        // log in was successful
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_ACTIVITY_CODE) {
                // Coming back from the edit view, update the view
                aNotifAdapter.loadObjects();
            } else if (requestCode == LOGIN_ACTIVITY_CODE) {
                // If the user is new, sync data to Parse,
                // else get the current list from Parse
                if (ParseUser.getCurrentUser().isNew()) {
                    syncNotifsToParse();
                } else {
                    loadFromParse();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_notification, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_sync) {
            syncNotifsToParse();
        }

        if (item.getItemId() == R.id.action_logout) {
            // Log out the current user
            ParseUser.logOut();
            // Create a new anonymous user
            //ParseAnonymousUtils.logIn(null);
            // Update the logged in label info
            updateLoggedInInfo();
            // Clear the view
            aNotifAdapter.clear();
            // Unpin all the current objects
            startActivity(new Intent(this, WelcomeActivity.class));
            //ParseObject
            // .unpinAllInBackground(ParseApplication.TODO_GROUP_NAME);
        }


        return super.onOptionsItemSelected(item);
    }






    private void syncNotifsToParse() {
        // We could use saveEventually here, but we want to have some UI
        // around whether or not the draft has been saved to Parse
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                // If we have a network connection and a current logged in user,
                // sync the
                // todos

                // In this app, local changes should overwrite content on the
                // server.

                ParseQuery<Notif> query = Notif.getQuery();
                query.fromPin(ParseApplication.NOTIF_GROUP_NAME);
                query.whereEqualTo("isDraft", true);
                query.findInBackground(new FindCallback<Notif>() {
                    public void done(List<Notif> notifs, ParseException e) {
                        if (e == null) {
                            for (final Notif notif : notifs) {
                                // Set is draft flag to false before
                                // syncing to Parse
                                notif.setDraft(false);
                                notif.saveInBackground(new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        ParseRelation relation = currentUser.getRelation("Notif");
                                        relation.add(notif);
                                        currentUser.saveInBackground();
                                        if (e == null) {
                                            // Let adapter know to update view
                                            if (!isFinishing()) {
                                                aNotifAdapter
                                                        .notifyDataSetChanged();
                                            }
                                        } else {
                                            // Reset the is draft flag locally
                                            // to true

                                            notif.setDraft(true);
                                        }
                                    }

                                });

                            }
                        } else {
                            Log.i("Notifications",
                                    "syncTodosToParse: Error finding pinned todos: "
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
                Intent loginIntent = new Intent(Notifications.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);

            }
        } else {
            // If there is no connection, let the user know the sync didn't
            // happen
            Toast.makeText(
                    getApplicationContext(),
                    "Your device appears to be offline. Some todos may not have been synced to Parse.",
                    Toast.LENGTH_LONG).show();
        }
    }
    private void loadFromParse() {
        Toast.makeText(this, "Loading From Parse", Toast.LENGTH_SHORT);
        ParseQuery<Notif> query = Notif.getQuery();
        query.whereEqualTo("whoCreated", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Notif>() {
            public void done(List<Notif> notifs, ParseException e) {
                if (e == null) {
                    ParseObject.pinAllInBackground((List<Notif>) notifs,
                            new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        if (!isFinishing()) {
                                            aNotifAdapter.loadObjects();
                                        }
                                    } else {
                                        Log.i("Notifications",
                                                "Error pinning notifs: "
                                                        + e.getMessage());
                                    }
                                }
                            });
                } else {
                    Log.i("Notifications",
                            "loadFromParse: Error finding pinned todos: "
                                    + e.getMessage());
                }
            }
        });
    }


    private class NotifAdapter extends ParseQueryAdapter<Notif> {

        public NotifAdapter(Context context,
                            QueryFactory<Notif> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(Notif notif, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_todo, parent, false);
                holder = new ViewHolder();
                holder.notifTitle = (TextView) view
                        .findViewById(R.id.todo_title);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            TextView notifTitle = holder.notifTitle;
            notifTitle.setText(notif.getTitle());
            if (notif.isDraft()) {
                notifTitle.setTypeface(null, Typeface.ITALIC);
            } else {
                notifTitle.setTypeface(null, Typeface.NORMAL);
            }
            return view;
        }
    }

    private static class ViewHolder {
        TextView notifTitle;
    }
}





