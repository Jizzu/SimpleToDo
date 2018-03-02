package apps.jizzu.simpletodo.utils;

import android.app.Application;

/**
 * Class that helps to get the current status of MainActivity.
 */
public class MyApplication extends Application {

    private static boolean mActivityVisible;

    public static boolean isActivityVisible() {
        return mActivityVisible;
    }

    public static void activityResumed() {
        mActivityVisible = true;
    }

    public static void activityPaused() {
        mActivityVisible = false;
    }
}
