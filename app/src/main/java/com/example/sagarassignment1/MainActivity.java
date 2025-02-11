package com.example.sagarassignment1;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         // Linking our XML file (activity_main) to this activity
        setContentView(R.layout.activity_main);
    }
}