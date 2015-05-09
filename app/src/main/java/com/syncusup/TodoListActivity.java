package com.syncusup;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Paint;
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
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.parse.FindCallback;
        import com.parse.GetCallback;
        import com.parse.ParseAnonymousUtils;
        import com.parse.ParseClassName;
        import com.parse.ParseException;
        import com.parse.ParseObject;
        import com.parse.ParseQuery;
        import com.parse.ParseQueryAdapter;
        import com.parse.ParseUser;
        import com.parse.SaveCallback;


        import java.util.List;

public class TodoListActivity extends Activity {
    private static final int LOGIN_ACTIVITY_CODE = 100;
    private static final int EDIT_ACTIVITY_CODE = 200;

    // Adapter for the Todos Parse Query
    private ParseQueryAdapter<Todo> todoListAdapter;

    private LayoutInflater inflater;

    // For showing empty and non-empty todo views
    private ListView todoListView;
    private LinearLayout noTodosView;
    private LinearLayout loadingView;
    private ParseUser currentUser;
    private TextView loggedInInfoView;
    private String syncListId = null; // id of parentList
    private SyncList synclist;
    private List_permissions userLp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Todos");
        setContentView(R.layout.activity_todo_list);
        currentUser = ParseUser.getCurrentUser();
        // Fetch the todoId from the Extra data - need this to know what list todos to load
        if (getIntent().hasExtra("parentListId")) {
            syncListId = getIntent().getStringExtra("parentListId");
    }
        if (syncListId == null) {
            //synclist = new SyncList();
            //synclist.setUuidString();
            // not sure what do do i syncListId is null, maybe return to previous activity
            Log.i("TodoListActivity", ":onCreate synclistId is null from extras");


        } else {

            ParseQuery<SyncList> query = SyncList.getQuery();
            //query.fromLocalDatastore();
            query.whereEqualTo("objectId", syncListId);
            query.getFirstInBackground(new GetCallback<SyncList>() {

                @Override
                public void done( SyncList object, ParseException e) {
                    if (!isFinishing()) {
                        synclist = object;

                        Log.i("TodoListActivity", "synclistname: "+synclist.getName());
                        setListProperties();
                        //todoText.setText(todo.getTitle());
                        //deleteButton.setVisibility(View.VISIBLE);
                    }
                }

            });

        }
        // Set up the views
        todoListView = (ListView) findViewById(R.id.todo_list_view);
        noTodosView = (LinearLayout) findViewById(R.id.no_todos_view);
        loadingView = (LinearLayout) findViewById(R.id.todo_loading_view);
        todoListView.setEmptyView(noTodosView);
        loggedInInfoView = (TextView) findViewById(R.id.loggedin_info);

        // Set up the Parse query to use in the adapter
        ParseQueryAdapter.QueryFactory<Todo> factory = new ParseQueryAdapter.QueryFactory<Todo>() {
            public ParseQuery<Todo> create() {
                ParseQuery<Todo> query = Todo.getQuery(synclist);
                query.orderByDescending("createdAt");
                //query.fromLocalDatastore();
                return query;
            }
        };
        // Set up the adapter
        inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        todoListAdapter = new ToDoListAdapter(this, factory);

        // Attach the query adapter to the view
        ListView todoListView = (ListView) findViewById(R.id.todo_list_view);
        todoListView.setAdapter(todoListAdapter);
        final SwipeDetector swipeDetector = new SwipeDetector();
        todoListView.setOnTouchListener(swipeDetector);

        todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //if (view.getId() == R.id.todo_edit_button){
                 //   Log.i("todolistact", "onitemclick : view: button");
                //}
                TextView todoView = (TextView) view.findViewById(R.id.todo_title);
                Todo todo = todoListAdapter.getItem(position);
                    //todo.setCompleted(!todo.isCompleted());
                    if (todo.isCompleted()) {
                        todoView.setPaintFlags(todoView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        todoView.setPaintFlags(todoView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }
                    openEditView(todo);
                //Button todoEdit = (Button) view.findViewById(R.id.todo_edit_button);
               /* if(swipeDetector.swipeDetected()) {
                    if(swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        //Log.i("todolist", "on RL swipe, uuid? " + todo.getUuidString());
                        todoEdit.setVisibility(View.VISIBLE);
                        //openEditView(todo);
                    }
                    if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                        todoEdit.setVisibility(View.INVISIBLE);
                    }
                } else {
                    // NORMAL CLICK
                    Log.i("todolist", "onclick normal non-swipe");
                    Todo todo = todoListAdapter.getItem(position);
                    if (view.getId() == R.id.edit_todos){
                        Log.i("todolistact", "onitemclick : view: button");
                        openEditView(todo);
                    } else {


                        todo.setCompleted(!todo.isCompleted());
                        if (todo.isCompleted()) {
                            todoView.setPaintFlags(todoView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            todoView.setPaintFlags(todoView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                    }
                } */


                //openEditView(todo);

            }
        });

    } // end onCreate
    protected void onResume() {
        super.onResume();
        // Check if we have a real user
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // Sync data to Parse
            //syncTodosToParse();


            //check if synclist is null
            Log.i("TodoListAct:onResume", "synclistId: "+syncListId);
            ParseQuery<SyncList> query = SyncList.getQuery();
            //query.fromLocalDatastore();
            query.whereEqualTo("objectId", syncListId);
            query.getFirstInBackground(new GetCallback<SyncList>() {

                @Override
                public void done( SyncList object, ParseException e) {
                    if (!isFinishing()) {
                        synclist = object;
                        Log.i("TodoListActivity", "onResume:synclistname: "+synclist.getName());
                        //todoText.setText(todo.getTitle());
                        updateLoggedInInfo();
                        //deleteButton.setVisibility(View.VISIBLE);
                        //todoListAdapter.clear();
                        //todoListAdapter.notifyDataSetChanged();
                        loadFromParse();
                    }
                }

            });



            // Update the logged in label info

        }
    }

    private void updateLoggedInInfo() {
        if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            ParseUser currentUser = ParseUser.getCurrentUser();

            //loggedInInfoView.setText(getString(R.string.logged_in, currentUser.getString("username")));
            loggedInInfoView.setText((getString(R.string.parent_list, synclist.getName())));
        } else {
            loggedInInfoView.setText(getString(R.string.not_logged_in));
        }
    }

    private void openEditView(Todo todo) {
        Intent i = new Intent(this, NewTodoActivity.class);
        Bundle extras = new Bundle();

        extras.putString("ID", todo.getObjectId());
        Log.i("TodoListActivity", "syncListId to newTodo: "+syncListId);
        extras.putString("parentListId", syncListId);
        i.putExtras(extras);
        //i.putExtra("ID", todo.getUuidString());
        //i.putExtra("parentListId", syncListId);
        startActivity(i);
        //startActivityForResult(i, EDIT_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // An OK result means the pinned dataset changed or
        // log in was successful
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_ACTIVITY_CODE) {
                // Coming back from the edit view, update the view
                //todoListAdapter.notifyDataSetChanged();
                //todoListAdapter.loadObjects();
                //todoListAdapter.
            } else if (requestCode == LOGIN_ACTIVITY_CODE) {
                // If the user is new, sync data to Parse,
                // else get the current list from Parse
                if (ParseUser.getCurrentUser().isNew()) {

                    //syncTodosToParse();
                    loadFromParse();
                } else {
                    loadFromParse();
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_new) {
            // Make sure there's a valid user, anonymous
            // or regular
            if (ParseUser.getCurrentUser() != null) {
                // only allow adding new todo items for creator master and editor
                if (synclist.getCreator() == currentUser || userLp.getPermissionType().contentEquals("master") || userLp.getPermissionType().contentEquals("editor")) {
                    Intent i = new Intent(this, NewTodoActivity.class);
                    Bundle extras = new Bundle();
                    extras.putString("ID", null);
                    Log.i("TodoListActivity", "syncListId to newTodo: " + syncListId);
                    extras.putString("parentListId", syncListId);
                    i.putExtras(extras);
                    startActivityForResult(i, EDIT_ACTIVITY_CODE);
                }
            }
        }

        if (item.getItemId() == R.id.action_sync) {
            //syncTodosToParse();
            loadFromParse();

        }
        if (item.getItemId() == R.id.goto_main_menu){
            Intent i = new Intent(this, MenuActivity.class);
            startActivity(i);
        }

        if (item.getItemId() == R.id.action_logout) {
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
        }


        return super.onOptionsItemSelected(item);
    }






    private void syncTodosToParse() {
        // We could use saveEventually here, but we want to have some UI
        // around whether or not the draft has been saved to Parse
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if ((ni != null) && (ni.isConnected())) {
            if (!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                // If we have a network connection and a current logged in user,
                // sync the
                // todos

                // In this app, local changes should overwrite content on the
                // server.

                ParseQuery<Todo> query = Todo.getQuery(synclist);
                query.fromPin(ParseApplication.TODO_GROUP_NAME);
                query.whereEqualTo("isDraft", true);

                //query.whereEqualTo("parentList", syncListId);
                query.findInBackground(new FindCallback<Todo>() {
                    public void done(List<Todo> todos, ParseException e) {
                        if (e == null) {
                            for (final Todo todo : todos) {
                                // Set is draft flag to false before
                                // syncing to Parse
                                todo.setDraft(false);
                                todo.setCompleted(false);

                                todo.saveInBackground(new SaveCallback() {

                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            // Let adapter know to update view
                                            if (!isFinishing()) {
                                                todoListAdapter
                                                        .notifyDataSetChanged();

                                            }
                                        } else {
                                            // Reset the is draft flag locally
                                            // to true
                                            todo.setDraft(true);
                                        }
                                    }

                                });

                            }
                        } else {
                            Log.i("TodoListActivity",
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
                Intent loginIntent = new Intent(TodoListActivity.this, LoginActivity.class);
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
        //todoListAdapter.clear();
        if (todoListAdapter.isEmpty()){
            //Log.i("TodoListActv", "todoListAdapter is emtpy");
        } else {
            //Log.i("TodoListActv", "todoListAdapter is NOT empty");
        }
        //todoListAdapter.notifyDataSetChanged();
        if (synclist != null) {
            //Log.i("TodoList", "LoadFromParse: parent syncList getObjectId: " + synclist.getObjectId());
        } else {
            //Log.i("TodoList", "LoadFrmPrs: synclist object is NULL");
        }
        ParseQuery<Todo> query = Todo.getQuery(synclist);
        //query.include("parentList");
        //query.whereEqualTo("parentList", synclist);
        query.findInBackground(new FindCallback<Todo>() {
            public void done(List<Todo> todos, ParseException e) {
                if (e == null) {
                    if (!isFinishing()) {
                        //todoListAdapter.notifyDataSetChanged();
                        todoListAdapter.loadObjects();
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

    private class ToDoListAdapter extends ParseQueryAdapter<Todo> {

        public ToDoListAdapter(Context context,
                               ParseQueryAdapter.QueryFactory<Todo> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(Todo todo, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_todo, parent, false);
                holder = new ViewHolder();
                holder.todoTitle = (TextView) view.findViewById(R.id.todo_title);
                //holder.editButton = (Button) view.findViewById(R.id.todo_edit_button);
                view.setTag(holder);

            } else {
                holder = (ViewHolder) view.getTag();
            }
            TextView todoTitle = holder.todoTitle;
            Button editButton = holder.editButton;
            //ParseObject parentlist = new ParseObject("list");
            //parentlist = to-do.getParentList();

            //String parentName = parentlist.getName();
            ParseUser user = todo.getWhoCreated();
            //TODO if user only has read permission change button text to view details?
            todoTitle.setText(todo.getTitle() );
            if (todo.isDraft()) {
                todoTitle.setTypeface(null, Typeface.ITALIC);

            } else {
                todoTitle.setTypeface(null, Typeface.NORMAL);
            }
            if(todo.isCompleted()){
                todoTitle.setPaintFlags(todoTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                todoTitle.setPaintFlags(todoTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            return view;
        }
    }

    private static class ViewHolder {
        TextView todoTitle;
        Button editButton;


    }
    // used to get user permissions for the parent list
    private void setListProperties(){
        if (currentUser == synclist.getCreator()){
            // current user is creator don't query for permissions


        } else {
            ParseQuery<List_permissions> userPermisQuery = List_permissions.getQuery();
            userPermisQuery.whereEqualTo("list_id", syncListId);
            userPermisQuery.whereEqualTo("user_id", currentUser.getObjectId());

            userPermisQuery.getFirstInBackground(new GetCallback<List_permissions>() {
                @Override
                public void done(List_permissions permissions, ParseException e) {
                    if (e == null) {
                        if (!isFinishing()) {
                            userLp = permissions;

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

}



