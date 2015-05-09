package com.syncusup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewTodoActivity extends Activity {

	private Button saveButton;
	private Button deleteButton;
    private Button editButton;
    private Button completeButton;
	private EditText todoText;
    private EditText descriptionText;
    private TextView parentText;
	private Todo todo;
    private SyncList parentList;
	private String todoId = null;
    private String parentListId = null;
    private TextView whoCompleted;
    private TextView dateCompleted;
    private ParseUser currentUser;
    private List_permissions userLp;
    //private SyncList parentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit todo");
        setContentView(R.layout.activity_new_todo);
        currentUser = ParseUser.getCurrentUser();
        //Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        // Fetch the todoId from the Extra data
        todoId = extras.getString("ID");
        if (todoId == null) {
            Log.i("NewTodoActivity", "todo ID extra was null");
        }
        parentListId = extras.getString("parentListId");
        if (parentListId == null) {
            Log.i("NewTodoActivity", "parentListId extra was null");
        }
        todoText = (EditText) findViewById(R.id.todo_text);
        descriptionText = (EditText) findViewById(R.id.description_text);
        saveButton = (Button) findViewById(R.id.saveButton);
        editButton = (Button) findViewById(R.id.editButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        parentText = (TextView) findViewById(R.id.parentList);
        completeButton = (Button) findViewById(R.id.completeButton);

        whoCompleted = (TextView) findViewById(R.id.whoCompleted);
        dateCompleted = (TextView) findViewById(R.id.dateCompleted);
		/*if (getIntent().hasExtra("ID")) {
			todoId = getIntent().getExtras().getString("ID");

		}
        if (getIntent().hasExtra("parentListId")){
            parentListId = getIntent().getExtras().getString("parentListId");
        } else {
            Log.i("NewTodoActivity", "No parentListId was passed in extras");
        } */
        ParseQuery<SyncList> query = SyncList.getQuery();
        query.whereEqualTo("objectId", parentListId);
        query.getFirstInBackground(new GetCallback<SyncList>() {
            @Override
            public void done(SyncList syncList, ParseException e) {
                if (!isFinishing()) {
                    parentList = syncList;
                    parentText.setText(parentList.getName());
                    getTodo();
                }
            }
        });
        if (parentList == null) {
            Log.i("newtodo", "on create, parentList is null");
        }
    } // end onCreate

    private void getTodo() {

        if (todoId == null) {
            Log.i("NewTodo", "adding new, todoId was null");
            todo = new Todo();
            todo.setUuidString();
        } else {
            ParseQuery<Todo> listquery = Todo.getQuery();
            //listquery.fromLocalDatastore();
            listquery.include("whoCompleted");
            listquery.whereEqualTo("objectId", todoId);

            Log.i("newTodoACtivity", "get objectId:" + todoId);
            listquery.getFirstInBackground(new GetCallback<Todo>() {

                @Override
                public void done(Todo object, ParseException e) {
                    if (!isFinishing()) {
                        todo = object;
                        if (todo == null) {
                            Log.i("newtodoact", "todo in callback null");
                        } else {
                            todoText.setText(todo.getTitle());
                            todoText.setFocusable(false);
                            descriptionText.setText(todo.getDescription());
                            descriptionText.setFocusable(false);
                            parentText.setText(parentList.getName());
                            deleteButton.setVisibility(View.VISIBLE);
                            if (todo.isCompleted()==true){
                                whoCompleted.setVisibility(View.VISIBLE);
                                whoCompleted.setText("Completed By: " +todo.getWhoCompleted().getUsername());
                                dateCompleted.setVisibility(View.VISIBLE);
                                dateCompleted.setText("Date Completed: "+todo.getDate("dateCompleted"));
                            } else {
                                whoCompleted.setVisibility(View.INVISIBLE);
                                dateCompleted.setVisibility(View.INVISIBLE);
                            }
                            setListProperties();
                        }
                    }
                }

            });

        }

        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO check if user has permissions to edit
                //Log.i("newtodo", "edit button was clicked");
                if (editButton.getText().toString() == "Edit") {
                    editButton.setText("Cancel");
                    saveButton.setVisibility(View.VISIBLE);
                    todoText.setFocusableInTouchMode(true);
                    todoText.setFocusable(true);
                    descriptionText.setFocusableInTouchMode(true);
                    descriptionText.setFocusable(true);
                } else {
                    todoText.setFocusable(false);
                    descriptionText.setFocusable(false);
                    editButton.setText("Edit");
                    saveButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        saveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                todo.setTitle(todoText.getText().toString());
                //todo.setDraft(true);
                todo.setDraft(false);
                if (todo.getWhoCreated() == null) {
                    todo.setWhoCreated(ParseUser.getCurrentUser());
                }
                todo.setDescription(descriptionText.getText().toString());
                //todo.setParentList(parentList);
                todo.put("parentList", parentList);
                //todo.saveEventually();
                //todo.pinInBackground(ParseApplication.TODO_GROUP_NAME, // tyring saveinbackgrund instead
                todo.saveInBackground(
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

        deleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // The todo will be deleted eventually but will
                // immediately be excluded from query results.
                todo.deleteEventually();
                setResult(Activity.RESULT_OK);
                finish();
            }

        });

        completeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                todo.setCompleted(true);
                todo.setWhoCompleted(ParseUser.getCurrentUser());
                finish();
            }
        });


    }
    private void setListProperties(){
        if (currentUser == parentList.getCreator()){
            // current user is creator don't query for permissions
            completeButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);

        } else {
            ParseQuery<List_permissions> userPermisQuery = List_permissions.getQuery();
            userPermisQuery.whereEqualTo("list_id", parentListId);
            userPermisQuery.whereEqualTo("user_id", currentUser.getObjectId());
            todoText.setFocusable(true);
            todoText.setFocusableInTouchMode(true);

            userPermisQuery.getFirstInBackground(new GetCallback<List_permissions>() {
                @Override
                public void done(List_permissions permissions, ParseException e) {
                    if (e == null) {
                        if (!isFinishing()) {
                            userLp = permissions;

                            if (userLp.getPermissionType().contentEquals("watcher")){
                                saveButton.setVisibility(View.INVISIBLE);
                                editButton.setVisibility(View.INVISIBLE);
                                completeButton.setVisibility(View.INVISIBLE);
                                todoText.setFocusable(false);
                                todoText.setFocusableInTouchMode(false);

                            }
                            if (userLp.getPermissionType().contentEquals("master")){
                                deleteButton.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.VISIBLE);
                                saveButton.setVisibility(View.VISIBLE);
                                completeButton.setVisibility(View.VISIBLE);
                                todoText.setFocusable(true);
                                todoText.setFocusableInTouchMode(true);
                            }
                            if (userLp.getPermissionType().contentEquals("editor")){
                                completeButton.setVisibility(View.VISIBLE);
                                deleteButton.setVisibility(View.INVISIBLE);
                                todoText.setFocusable(true);
                                todoText.setFocusableInTouchMode(true);

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
}


