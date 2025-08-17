package com.dylanvann.fastimage;

import android.app.Activity;

import com.bumptech.glide.Glide;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public class FastImageViewModule extends NativeFastImageSpec {

    private final ReactApplicationContext reactApplicationContext;

    FastImageViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactApplicationContext = reactContext;
    }

    @Override
    public void clearMemoryCache() {
        final Activity activity = reactApplicationContext.getCurrentActivity();
        if (activity == null) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.get(activity.getApplicationContext()).clearMemory();
            }
        });
    }

    @Override
    public void clearDiskCache() {
        final Activity activity = reactApplicationContext.getCurrentActivity();
        if (activity == null) {
            return;
        }

        Glide.get(activity.getApplicationContext()).clearDiskCache();
    }

    @Override
    public void preload(ReadableArray sources) {
        final Activity activity = reactApplicationContext.getCurrentActivity();
        if (activity == null) {
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
                }
            }
        });
    }
}
