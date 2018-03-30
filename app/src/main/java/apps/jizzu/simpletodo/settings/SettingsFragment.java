package apps.jizzu.simpletodo.settings;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import apps.jizzu.simpletodo.BuildConfig;
import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.utils.DeviceInfo;
import apps.jizzu.simpletodo.utils.PreferenceHelper;
import apps.jizzu.simpletodo.utils.BackupHelper;

import static android.content.Intent.ACTION_VIEW;

/**
 * Fragment which contains the settings information.
 */
public class SettingsFragment extends PreferenceFragment {

    private PreferenceHelper mPreferenceHelper;
    private CheckBoxPreference mAnimation;
    private CheckBoxPreference mGeneralNotification;
    private Preference mCreateBackup;
    private Preference mRestoreBackup;
    private Preference mAboutVersion;
    private BackupHelper mBackupHelper;
    private boolean isCreatingProcess;
    private int clicksCounter;

    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBackupHelper = new BackupHelper(getActivity());

        addPreferencesFromResource(R.xml.preferences);

        mPreferenceHelper = PreferenceHelper.getInstance();

        mAnimation = (CheckBoxPreference) findPreference("animation");
        mAnimation.setChecked(mPreferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON));

        mAnimation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                mPreferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, mAnimation.isChecked());

                return true;
            }
        });

        mGeneralNotification = (CheckBoxPreference) findPreference("general_notification");
        mGeneralNotification.setChecked(mPreferenceHelper.getBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON));

        mGeneralNotification.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                mPreferenceHelper.putBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON, mGeneralNotification.isChecked());

                return true;
            }
        });

        mCreateBackup = findPreference("backup");
        mCreateBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                isCreatingProcess = true;

                if (hasPermissions()) {
                    mBackupHelper.showCreateDialog();
                } else {
                    requestPermissionWithRationale();
                }
                return true;
            }
        });

        mRestoreBackup = findPreference("restore");
        mRestoreBackup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                if (hasPermissions()) {
                    mBackupHelper.showRestoreDialog();
                } else {
                    requestPermissionWithRationale();
                }
                return true;
            }
        });

        findPreference("about_version").setOnPreferenceClickListener(
                createPreferenceClickListener("https://github.com/Jizzu/SimpleToDo")
        );

        Preference rateThisApp = findPreference("rate_app");
        rateThisApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String appPackageName = getActivity().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            }
        });

        Preference sendFeedback = findPreference("send_feedback");
        sendFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setData(new Uri.Builder().scheme("mailto").build());
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"ilya.ponomarenko.dev@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title));
                email.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_device_info) + "\n" + DeviceInfo.getDeviceInfo()
                        + "\n" + getString(R.string.feedback_app_version) + BuildConfig.VERSION_NAME
                        + "\n" + getString(R.string.feedback) + "\n");
                try {
                    startActivity(Intent.createChooser(email, "Send feedback"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText((getActivity()), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        Preference licences = findPreference("about_licenses");
        licences.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), LicensesActivity.class);
                startActivity(intent);
                return true;
            }
        });
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

    /**
     * Method that verifies the existence of a permission.
     */
    private boolean hasPermissions() {
        int result;
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        for (String currentPermission : permissions) {
            result = getActivity().checkCallingOrSelfPermission(currentPermission);
            if (!(result == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Method for requesting permission.
     */
    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // After calling this method, the user is presented with a dialog asking for permission
            // The user's response comes in the onRequestPermissionsResult method.
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean isAllowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    // If user granted all permissions.
                    isAllowed = isAllowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;

            default:
                // If user not granted permissions.
                isAllowed = false;
                break;
        }

        if (isAllowed) {
            if (isCreatingProcess) {
                mBackupHelper.showCreateDialog();
            } else {
                mBackupHelper.showRestoreDialog();
            }
        } else {
            // We will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(getActivity(), R.string.settings_permission_denied_toast, Toast.LENGTH_SHORT).show();
                } else {
                    showNoStoragePermissionSnackbar();
                }
            }
        }
        isCreatingProcess = false;
    }

    /**
     * If the user choose "Don't ask again" Snackbar appears with a link to the settings.
     */
    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(getActivity().findViewById(R.id.activity_view), R.string.settings_permission_snackbar_no_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.settings_permission_snackbar_button_settings, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.settings_permission_settings_toast,
                                Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .show();
    }

    /**
     * Method for opening system settings page.
     */
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    /**
     * If the user clicks "Deny" button, Snackbar appears with a rationale message.
     */
    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = getString(R.string.settings_permission_snackbar_with_rationale);
            Snackbar.make(getActivity().findViewById(R.id.activity_view), message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.settings_permission_snackbar_button_grant, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }
    }
}
