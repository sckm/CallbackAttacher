package com.github.scache.callbackattacher.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github.scache.callbackattacher.CallbackAttacher;

public class NoCallbackActivity extends AppCompatActivity {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(new BaseFragment(), "extend fragment")
                    .commit();
        }
    }

    public static class BaseFragment extends Fragment {
        @Override public void onAttach(Context context) {
            super.onAttach(context);
            CallbackAttacher.attach(this, context);
        }

        @Override public void onDetach() {
            CallbackAttacher.detach(this);
            super.onDetach();
        }
    }
}
