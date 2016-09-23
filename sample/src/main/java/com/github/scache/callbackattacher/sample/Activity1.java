package com.github.scache.callbackattacher.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.scache.callbackattacher.processor.AttachCallback;

public class Activity1 extends AppCompatActivity implements Callback {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(new InnerFragment(), "inner fragment")
                    .commit();
        }
    }

    @Override public void onSuccess() {
        Toast.makeText(this, getClass().getSimpleName() + ": called callback", Toast.LENGTH_SHORT).show();
    }

    public static class InnerFragment extends Fragment {
        @AttachCallback
        Callback callback;

        @Override public void onAttach(Context context) {
            super.onAttach(context);
            Activity1$InnerFragment_CallbackAttacher.attach(this, context);
            callback.onSuccess();
        }

        @Override public void onDetach() {
            super.onDetach();
            Activity1$InnerFragment_CallbackAttacher.detach(this);
        }
    }
}
