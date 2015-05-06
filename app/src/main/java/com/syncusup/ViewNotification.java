package com.syncusup;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class ViewNotification extends Activity {


    private Button deleteButton;
    private Notif anotif;
    private String notifId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);

        // Fetch the todoId from the Extra data
        if (getIntent().hasExtra("ID")) {
            notifId = getIntent().getExtras().getString("ID");
        }

        deleteButton = (Button) findViewById(R.id.deleteButton);


            ParseQuery<Notif> query = Notif.getQuery();
            query.fromLocalDatastore();
            query.whereEqualTo("uuid", notifId);
            query.getFirstInBackground(new GetCallback<Notif>() {

                @Override
                public void done(Notif object, ParseException e) {
                    if (!isFinishing()) {
                        anotif = object;
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                }

            });




        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // The todo will be deleted eventually but will
                // immediately be excluded from query results.
                anotif.deleteEventually();
                setResult(Activity.RESULT_OK);
                finish();
            }

        });

    }
}
