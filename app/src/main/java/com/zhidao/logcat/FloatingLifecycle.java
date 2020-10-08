package com.zhidao.logcat;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

final class FloatingLifecycle implements Application.ActivityLifecycleCallbacks {

    Application application;

    static void with(Application application) {
        application.registerActivityLifecycleCallbacks(new FloatingLifecycle());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        if (activity instanceof MainActivity) {
            return;
        }
        new FloatingWindow(application).show();
    }

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityResumed(Activity activity) {}

    @Override
    public void onActivityPaused(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
}