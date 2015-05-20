package com.example.ntnu15.android_class;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by NTNU15 on 5/12/15.
 */
public class SimpleUIApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bJDGykyTKBVUTLhxJYDAYFozFHnVtHySdC7lb0YN", "dt61u5zFUDWUK21epwMFDBrYFQjszPwKDOEngRal");  //my account
        //Parse.initialize(this, "fgKEQGJ5j5hQRbC3Mwytop2zyR70MWyoSYUlpM9S", "cpaz9soGN8tfBG9VpeqjGBn4Oe3xza7DNepqIbbO");  // teacher for hw2

    }

}
