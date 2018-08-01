package apps.jizzu.simpletodo.utils

import android.app.Application

/**
 * Class that helps to get the current status of MainActivity.
 */
class MyApplication : Application() {
    companion object {

        var isActivityVisible: Boolean = false
            private set

        fun activityResumed() {
            isActivityVisible = true
        }

        fun activityPaused() {
            isActivityVisible = false
        }
    }
}
