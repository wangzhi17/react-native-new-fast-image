package com.dylanvann.fastimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.BaseReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.module.model.ReactModuleInfo;
import com.facebook.react.module.model.ReactModuleInfoProvider;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastImageViewPackage extends BaseReactPackage {

    @Nullable
    @Override
    public NativeModule getModule(@NonNull String name, @NonNull ReactApplicationContext reactApplicationContext) {
        if (name.equals(FastImageViewModule.NAME)) {
            return new FastImageViewModule(reactApplicationContext);
        }
        return null;
    }

    @NonNull
    @Override
    public ReactModuleInfoProvider getReactModuleInfoProvider() {
        return new ReactModuleInfoProvider() {
            @NonNull
            @Override
            public Map<String, ReactModuleInfo> getReactModuleInfos() {
                final Map<String, ReactModuleInfo> moduleInfo = new HashMap<>();
                boolean isTurboModule = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
                moduleInfo.put(
                        FastImageViewModule.NAME,
                        new ReactModuleInfo(
                                FastImageViewModule.NAME,
                                FastImageViewModule.NAME,
                                false,
                                false,
                                false,
                                isTurboModule
                        )
                );
                return moduleInfo;
            }
        };
    }

    @NonNull
    @Override
    public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
        List<ViewManager> list = new ArrayList<>();
        list.add(new FastImageViewManager());
        return list;
    }
}
