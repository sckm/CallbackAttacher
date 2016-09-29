package com.github.scache.callbackattacher.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.scache.callbackattacher.CallbackAttacher;
import com.github.scache.callbackattacher.processor.AttachCallback;

public class ExtendedActivity extends AppCompatActivity implements Callback {
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(new ChildFragment(), "extend fragment")
                    .commit();
        }
    }

    @Override public void onSuccess() {
        Toast.makeText(this, getClass().getSimpleName() + ": called callback", Toast.LENGTH_SHORT).show();
    }

    public static class BaseFragment extends Fragment {
        @AttachCallback
        Callback parentCallback;

        @Override public void onAttach(Context context) {
            super.onAttach(context);
            CallbackAttacher.attach(this, context);
            parentCallback.onSuccess();
        }

        @Override public void onDetach() {
            super.onDetach();
            CallbackAttacher.detach(this);
        }
    }

    public static class ChildFragment extends BaseFragment {
        @AttachCallback
        Callback childCallback;

        @Override public void onAttach(Context context) {
            super.onAttach(context);
            assert parentCallback != null;
            assert childCallback != null;
            childCallback.onSuccess();
        }

        @Override public void onDetach() {
            super.onDetach();
            assert parentCallback == null;
            assert childCallback == null;
        }
    }
}
