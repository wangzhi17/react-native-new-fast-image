package com.dylanvann.fastimage;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public class FastImageViewModule extends NativeFastImageSpec {

    private final String REACT_CLASS = "FastImageViewModule";

    FastImageViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    public void clearMemoryCache(Promise promise) {
        final Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.resolve(true);
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.get(activity.getApplicationContext()).clearMemory();
                promise.resolve(true);
            }
        });
    }

    public void clearDiskCache(Promise promise) {
        final Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.resolve(true);
            return;
        }

        Glide.get(activity.getApplicationContext()).clearDiskCache();
        promise.resolve(true);
    }

    @Override
    public void preload(ReadableArray sources, Promise promise) {
        final Activity activity = getCurrentActivity();
        if (activity == null) {
            promise.resolve(true);
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < sources.size(); i++) {
                    final ReadableMap source = sources.getMap(i);
                    final FastImageSource imageSource = FastImageViewConverter.getImageSource(activity, source);

                    // This will make this work for remote and local images. e.g.
                    //    - file:///
                    //    - content://
                    //    - res:/
                    //    - android.resource://
                    //    - data:image/png;base64
                    Glide.with(activity.getApplicationContext())
                            .load(
                                    imageSource.isBase64Resource() ? imageSource.getSource() :
                                            imageSource.isResource() ? imageSource.getUri() : imageSource.getGlideUrl()
                            )
                            .apply(FastImageViewConverter.getOptions(activity, imageSource, source))
                            .preload();
                    promise.resolve(true);
                }
            }
        });
    }
}
