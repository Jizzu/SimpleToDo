package apps.jizzu.simpletodo.utils

import android.os.Build
import android.text.TextUtils

/**
 * Class for getting information about the device.
 */
object DeviceInfo {

    val deviceInfo: String?
        get() {

            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            val version = Integer.toString(Build.VERSION.SDK_INT)
            val versionRelease = Build.VERSION.RELEASE

            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else capitalize(manufacturer) + " " + model + ", API " + version + ", Android " + versionRelease
        }

    private fun capitalize(str: String): String? {

        if (TextUtils.isEmpty(str)) {
            return str
        }

        val arr = str.toCharArray()
        var capitalizeNext = true
        var phrase = ""

        for (c in arr) {

            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c)
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase += c
        }
        return phrase
    }
}
