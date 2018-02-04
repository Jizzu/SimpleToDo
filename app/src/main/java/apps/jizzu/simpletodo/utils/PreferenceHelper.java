package apps.jizzu.simpletodo.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Class that helps to manage SharedPreferences data (uses the Singleton pattern).
 */
public class PreferenceHelper {

    public static final String ANIMATION_IS_ON = "animation_is_on";

    private static PreferenceHelper mInstance;
    private Context context;
    private SharedPreferences preferences;

    private PreferenceHelper() {

    }

    public static PreferenceHelper getInstance() {
        if (mInstance == null) {
            mInstance = new PreferenceHelper();
        }
        return mInstance;
    }

    public void init(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, true);
    }
}
