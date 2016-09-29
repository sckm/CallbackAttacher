package com.github.scache.callbackattacher.sample;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.scache.callbackattacher.CallbackAttacher;
import com.github.scache.callbackattacher.processor.AttachCallback;

public class SampleFragment extends Fragment {

    @AttachCallback
    Callback callback;

    @AttachCallback
    Callback callback2;

    @AttachCallback
    Callback callback3;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        CallbackAttacher.attach(this, context);
        callback.onSuccess();
        Log.d("SampleFragment", "onAttach: callback=" + callback);
        Log.d("SampleFragment", "onAttach: callback2=" + callback2);
        Log.d("SampleFragment", "onAttach: callback3=" + callback3);
    }

    @Override public void onDetach() {
        super.onDetach();
        CallbackAttacher.detach(this);
        Log.d("SampleFragment", "onDetach: callback=" + callback);
        Log.d("SampleFragment", "onDetach: callback2=" + callback2);
        Log.d("SampleFragment", "onDetach: callback3=" + callback3);
    }
}
