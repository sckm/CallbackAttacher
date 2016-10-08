package com.github.scache.callbackattacher.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github.scache.callbackattacher.processor.AttachCallback;

public class Activity2 extends AppCompatActivity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(new InnerFragment(), "inner fragment")
                    .commit();
        }
    }

    public static class InnerFragment extends Fragment {
        @AttachCallback
        Callback callback;

        @Override public void onAttach(Context context) {
            super.onAttach(context);
            Activity2$InnerFragment_CallbackAttacher.attach(this, context);
        }

        @Override public void onDetach() {
            super.onDetach();
            Activity2$InnerFragment_CallbackAttacher.detach(this);
        }
    }
}
