package apps.jizzu.simpletodo.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Class that helps to manage SharedPreferences data (uses the Singleton pattern).
 */
class PreferenceHelper private constructor() {
    private lateinit var mPreferences: SharedPreferences

    fun init(context: Context) {
        mPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    fun putBoolean(key: String, value: Boolean) {
        val editor = mPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getBoolean(key: String): Boolean {
        return mPreferences.getBoolean(key, true)
    }

    companion object {

        const val ANIMATION_IS_ON = "animation_is_on"
        const val GENERAL_NOTIFICATION_IS_ON = "general_notification_is_on"

        private var mInstance: PreferenceHelper? = null

        fun getInstance(): PreferenceHelper {
            if (mInstance == null) {
                mInstance = PreferenceHelper()
            }
            return mInstance as PreferenceHelper
        }
    }
}
