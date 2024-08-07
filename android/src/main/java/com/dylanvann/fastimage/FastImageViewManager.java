
package com.dylanvann.fastimage;

import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_ERROR_EVENT;
import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_LOAD_END_EVENT;
import static com.dylanvann.fastimage.FastImageRequestListener.REACT_ON_LOAD_EVENT;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.PorterDuff;
import android.os.Build;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.dylanvann.fastimage.events.OnErrorEvent;
import com.dylanvann.fastimage.events.OnLoadEndEvent;
import com.dylanvann.fastimage.events.OnLoadEvent;
import com.dylanvann.fastimage.events.OnLoadStartEvent;
import com.dylanvann.fastimage.events.OnProgressEvent;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerHelper;
import com.facebook.react.uimanager.ViewManagerDelegate;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.viewmanagers.FastImageViewManagerDelegate;
import com.facebook.react.viewmanagers.FastImageViewManagerInterface;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

class FastImageViewManager extends SimpleViewManager<FastImageViewWithUrl> implements FastImageProgressListener, FastImageViewManagerInterface<FastImageViewWithUrl> {

    private final String REACT_CLASS = "FastImageView";
    private final String REACT_ON_LOAD_START_EVENT = "onFastImageLoadStart";
    private final String REACT_ON_PROGRESS_EVENT = "onFastImageProgress";
    private final Map<String, List<FastImageViewWithUrl>> VIEWS_FOR_URLS = new WeakHashMap<>();

    @Nullable
    private RequestManager requestManager = null;
    private final ViewManagerDelegate<FastImageViewWithUrl> mDelegate;


    @Nullable
    @Override
    protected ViewManagerDelegate<FastImageViewWithUrl> getDelegate() {
        return mDelegate;
    }

    public FastImageViewManager() {
        mDelegate = new FastImageViewManagerDelegate<>(this);
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected FastImageViewWithUrl createViewInstance(@NonNull ThemedReactContext reactContext) {
        if (isValidContextForGlide(reactContext)) {
            requestManager = Glide.with(reactContext);
        }

        return new FastImageViewWithUrl(reactContext);
    }

    @Override
    @ReactProp(name = "source")
    public void setSource(FastImageViewWithUrl view, @Nullable ReadableMap source) {
        view.setSource(source);
    }

    @Override
    @ReactProp(name = "defaultSource")
    public void setDefaultSource(FastImageViewWithUrl view, @Nullable String source) {
        view.setDefaultSource(
                ResourceDrawableIdHelper.getInstance()
                        .getResourceDrawable(view.getContext(), source));
    }

    @Override
    @ReactProp(name = "tintColor", customType = "Color")
    public void setTintColor(FastImageViewWithUrl view, @Nullable Integer color) {
        if (color == null) {
            view.clearColorFilter();
        } else {
            view.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    @ReactProp(name = "resizeMode")
    public void setResizeMode(FastImageViewWithUrl view, String resizeMode) {
        final FastImageViewWithUrl.ScaleType scaleType = FastImageViewConverter.getScaleType(resizeMode);
        view.setScaleType(scaleType);
    }

    @Override
    public void onDropViewInstance(@NonNull FastImageViewWithUrl view) {
        // This will cancel existing requests.
        view.clearView(requestManager);

        if (view.glideUrl != null) {
            final String key = view.glideUrl.toString();
            FastImageOkHttpProgressGlideModule.forget(key);
            List<FastImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
            if (viewsForKey != null) {
                viewsForKey.remove(view);
                if (viewsForKey.size() == 0) VIEWS_FOR_URLS.remove(key);
            }
        }

        super.onDropViewInstance(view);
    }

    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.<String, Object>builder()
                .put(OnLoadStartEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_LOAD_START_EVENT))
                .put(OnProgressEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_PROGRESS_EVENT))
                .put(OnLoadEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_LOAD_EVENT))
                .put(OnErrorEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_ERROR_EVENT))
                .put(OnLoadEndEvent.EVENT_NAME, MapBuilder.of("registrationName", REACT_ON_LOAD_END_EVENT))
                .build();
    }

    @Override
    public void onProgress(String key, long bytesRead, long expectedLength) {
        List<FastImageViewWithUrl> viewsForKey = VIEWS_FOR_URLS.get(key);
        if (viewsForKey != null) {
            for (FastImageViewWithUrl view : viewsForKey) {
                ThemedReactContext context = (ThemedReactContext) view.getContext();
                int viewId = view.getId();
                EventDispatcher eventDispatcher =
                        UIManagerHelper.getEventDispatcherForReactTag(context, viewId);
                if (eventDispatcher == null) {
                    return;
                }
                eventDispatcher.dispatchEvent(new OnProgressEvent(viewId, bytesRead, expectedLength));
            }
        }
    }

    @Override
    public float getGranularityPercentage() {
        return 0.5f;
    }

    private boolean isValidContextForGlide(final Context context) {
        Activity activity = getActivityFromContext(context);

        if (activity == null) {
            return false;
        }

        return !isActivityDestroyed(activity);
    }

    private static Activity getActivityFromContext(final Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }

        if (context instanceof ThemedReactContext) {
            final Context baseContext = ((ThemedReactContext) context).getBaseContext();
            if (baseContext instanceof Activity) {
                return (Activity) baseContext;
            }

            if (baseContext instanceof ContextWrapper) {
                final ContextWrapper contextWrapper = (ContextWrapper) baseContext;
                final Context wrapperBaseContext = contextWrapper.getBaseContext();
                if (wrapperBaseContext instanceof Activity) {
                    return (Activity) wrapperBaseContext;
                }
            }
        }

        return null;
    }

    private boolean isActivityDestroyed(Activity activity) {
        return activity.isDestroyed() || activity.isFinishing();

    }

    @Override
    protected void onAfterUpdateTransaction(@NonNull FastImageViewWithUrl view) {
        super.onAfterUpdateTransaction(view);
        view.onAfterUpdate(this, requestManager, VIEWS_FOR_URLS);
    }
}
