package com.example.shweta.referralcodedemo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Shweta on 26-04-2018.
 */

public class DeepLinkActivity extends AppCompatActivity {
    private static final String TAG = DeepLinkActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deep_link_activity);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for link in intent
        if (getIntent() != null && getIntent().getData() != null) {
            Uri data = getIntent().getData();

            Log.d(TAG, "data:" + data);
            ((TextView) findViewById(R.id.deep_link_text))
                    .setText(getString(R.string.deep_link_fmt, data.toString()));
        }
    }

    public void onOkBtn(){
        finish();
    }

}
