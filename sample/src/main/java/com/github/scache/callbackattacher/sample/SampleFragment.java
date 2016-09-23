package com.github.scache.callbackattacher.sample;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.github.scache.callbackattacher.processor.AttachCallback;
import com.github.scache.callbackattacher.processor.generated.GeneratedClass;

public class SampleFragment extends Fragment {

    @AttachCallback
    Callback callback;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
    }
}
