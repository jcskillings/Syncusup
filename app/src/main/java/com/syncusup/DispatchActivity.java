package com.syncusup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Activity which starts an intent for either the logged in (MainActivity) or logged out
 * (SignUpOrLoginActivity) activity.
 */
public class DispatchActivity extends Activity {

  public DispatchActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Check if there is current user info
    if (ParseUser.getCurrentUser() != null ) {
        Context context = getApplicationContext();
        CharSequence text = ParseUser.getCurrentUser().getString("username");
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        if (ParseUser.getCurrentUser() == null){
            startActivity(new Intent(this, WelcomeActivity.class));
        } else {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, ShowListsActivity.class));
        }
    } else {
      // Start and intent for the logged out activity
      if (ParseUser.getCurrentUser() == null){
          startActivity(new Intent(this, WelcomeActivity.class));
      }
      startActivity(new Intent(this, WelcomeActivity.class));
    }
  }

}
