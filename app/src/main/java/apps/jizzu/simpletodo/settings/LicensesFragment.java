package apps.jizzu.simpletodo.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import apps.jizzu.simpletodo.R;

import static android.content.Intent.ACTION_VIEW;

/**
 * Fragment which contains information about used open source libraries.
 */
public class LicensesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_licences);
        addLicenses();
    }

    private void addLicenses() {
        findPreference("material_search_view").setOnPreferenceClickListener(
                createPreferenceClickListener("https://github.com/MiguelCatalan/MaterialSearchView")
        );
        findPreference("circular_anim").setOnPreferenceClickListener(
                createPreferenceClickListener("https://github.com/XunMengWinter/CircularAnim")
        );
        findPreference("whats_new").setOnPreferenceClickListener(
                createPreferenceClickListener("https://github.com/TonnyL/WhatsNew")
        );
        findPreference("android_rate").setOnPreferenceClickListener(
                createPreferenceClickListener("https://github.com/hotchemi/Android-Rate")
        );
    }

    private Preference.OnPreferenceClickListener createPreferenceClickListener(final String uriString) {
        return new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            }
        };
    }
}
