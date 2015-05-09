package com.syncusup;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseObject;

public class ParseApplication extends Application {
  public static final String TODO_GROUP_NAME = "ALL_TODOS";
  public static final String SYNC_LIST_GROUP_NAME = "ALL_SYNC_LISTS";
  public static final String NOTIF_GROUP_NAME = "ALL_NOTIFS";
  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize Crash Reporting.
    ParseCrashReporting.enable(this);

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);
      // add Todo subclass and SyncList subclass
      ParseObject.registerSubclass(Todo.class);
    ParseObject.registerSubclass(Notif.class);
      ParseObject.registerSubclass(SyncList.class);
      ParseObject.registerSubclass(Friend.class);
      ParseObject.registerSubclass(List_permissions.class);
    // Add your initialization code here
    Parse.initialize(this, "S853VeY0ISR12ujFxD6vAN2CL1x1yzRDCSXSOtq1", "g4XHz7D1WubgNX8EUD9EouHdVHqgbGTmi8DKaq75");


    //ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    // defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);

      /* this was test code
      ParseUser user = new ParseUser();
      user.setUsername("my name");
      user.setPassword("my pass");
      user.setEmail("email@example.com");
        */
       // other fields can be set just like with ParseObject
      //user.put("email", "jcskillings@gmail.com");

      /*ParseUser user = new ParseUser();
      user.signUpInBackground(new SignUpCallback() {
          public void done(ParseException e) {
              if (e == null) {
                  // Hooray! Let them use the app now.
              } else {
                  // Sign up didn't succeed. Look at the ParseException
                  // to figure out what went wrong
              }
          }
      }); */


  }
}
