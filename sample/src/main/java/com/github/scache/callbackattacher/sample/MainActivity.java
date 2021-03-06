package com.github.scache.callbackattacher.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Callback, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.inner_fragment_button).setOnClickListener(this);
        findViewById(R.id.extend_fragment_button).setOnClickListener(this);
        findViewById(R.id.extend2_fragment_button).setOnClickListener(this);
        findViewById(R.id.no_callback_fragment_button).setOnClickListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(new SampleFragment(), "sample")
                    .commit();
        }
    }

    @Override public void onSuccess() {
        Toast.makeText(this, getClass().getSimpleName() + ": called callback ", Toast.LENGTH_SHORT).show();
    }


    @Override public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.inner_fragment_button:
                intent = new Intent(this, Activity1.class);
                break;

            case R.id.extend_fragment_button:
                intent = new Intent(this, ExtendedActivity.class);
                break;
            case R.id.extend2_fragment_button:
                intent = new Intent(this, ExtendedActivity2.class);
                break;
            case R.id.no_callback_fragment_button:
                intent = new Intent(this, NoCallbackActivity.class);
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
