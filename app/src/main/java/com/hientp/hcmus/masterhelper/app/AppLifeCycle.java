package com.hientp.hcmus.masterhelper.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hientp.hcmus.masterhelper.AndroidApplication;
import com.hientp.hcmus.masterhelper.event.AppStateChangeEvent;
import com.hientp.hcmus.masterhelper.internal.di.components.ApplicationComponent;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;


public class AppLifeCycle implements Application.ActivityLifecycleCallbacks {

    public static final Map<String, Integer> activities = new HashMap<>();
    private static String mLastActivity;

    private final EventBus mEventBus;
    private short mLastState;

    public AppLifeCycle() {
        mEventBus = EventBus.getDefault();
    }

    public static boolean isBackGround() {
        for (Map.Entry<String, Integer> s : activities.entrySet()) {
            if (s.getValue() == 1) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLastActivity(@NonNull String simpleName) {
        return !TextUtils.isEmpty(mLastActivity) && mLastActivity.endsWith(simpleName);
    }

    /**
     * Make sure activity still alive
     *
     * @param simpleName activity name
     * @return true false
     */
    public static boolean hasActivity(@NonNull String simpleName) {
        if (activities == null || activities.size() <= 0) {
            return false;
        }
        int activityCount = 0;
        for (Map.Entry<String, Integer> entry : activities.entrySet()) {
            if (entry.getKey().endsWith(simpleName)) {
                activityCount = entry.getValue();
                break;
            }
        }
        return activityCount > 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        checkCreatedIfRootActivity(activity, savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        String className = activity.getLocalClassName();
        activities.put(className, 1);
        mLastActivity = className;
        applicationStatus();
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activities.put(activity.getLocalClassName(), 0);
        applicationStatus();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private void applicationStatus() {
        Timber.i("Is application background %s", isBackGround());
        if (isBackGround()) {
            if (mLastState == 0) {
                return;
            }
            mLastState = 0;
            mEventBus.post(new AppStateChangeEvent(false));
        } else {
            if (mLastState == 1) {
                return;
            }
            mEventBus.post(new AppStateChangeEvent(true));
            mLastState = 1;
        }
    }

    private void checkCreatedIfRootActivity(Activity activity, Bundle savedInstanceState) {
        // Timber.d("Created savedInstanceState %s activities %s getUserComponent() %s", savedInstanceState, activities, getUserComponent());

        if (savedInstanceState == null || !activities.isEmpty()) {
            return;
        }

        /*if (activity.getClass().getSimpleName().equals(HomeActivity.TAG)) {
            return;
        }*/

        createUserComponent();

       /* if (getUserComponent() == null) {
            return;
        }*/

        //getUserComponent().userSession().ensureUserInitialized();
    }

    private void createUserComponent() {

       /* if (getUserComponent() != null) {
            return;
        }

        UserConfig userConfig = getAppComponent().userConfig();
        Timber.d("User is sign in %s", userConfig.isSignIn());
        if (userConfig.isSignIn()) {
            userConfig.loadConfig();
            AndroidApplication.instance().createUserComponent(userConfig.getCurrentUser());
        }*/
    }

    public ApplicationComponent getAppComponent() {
        return AndroidApplication.instance().getAppComponent();
    }

    /*private UserComponent getUserComponent() {
        return AndroidApplication.instance().getUserComponent();
    }
    */
}