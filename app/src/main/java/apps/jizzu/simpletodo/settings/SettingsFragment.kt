package apps.jizzu.simpletodo.settings

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import apps.jizzu.simpletodo.BuildConfig
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter
import apps.jizzu.simpletodo.utils.BackupHelper
import apps.jizzu.simpletodo.utils.DeviceInfo
import apps.jizzu.simpletodo.utils.PreferenceHelper

/**
 * Fragment which contains the settings information.
 */
class SettingsFragment : PreferenceFragment() {

    private lateinit var mPreferenceHelper: PreferenceHelper
    private lateinit var mAnimation: CheckBoxPreference
    private lateinit var mGeneralNotification: CheckBoxPreference
    private lateinit var mBackupHelper: BackupHelper

    private var mIsCreatingProcess: Boolean = false
    private var mSelectedItemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBackupHelper = BackupHelper(activity)

        addPreferencesFromResource(R.xml.preferences)

        mPreferenceHelper = PreferenceHelper.getInstance()

        mAnimation = findPreference("animation") as CheckBoxPreference
        mAnimation.isChecked = mPreferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)

        mAnimation.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mPreferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, mAnimation.isChecked)

            true
        }

        mGeneralNotification = findPreference("general_notification") as CheckBoxPreference
        mGeneralNotification.isChecked = mPreferenceHelper.getBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON)

        mGeneralNotification.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mPreferenceHelper.putBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON, mGeneralNotification.isChecked)

            true
        }

        val dateFormat = findPreference("date_format")
        dateFormat.onPreferenceClickListener = Preference.OnPreferenceClickListener {

            val listItems = resources.getStringArray(R.array.date_format_list)
            mSelectedItemPosition = mPreferenceHelper.getInt(PreferenceHelper.DATE_FORMAT_KEY)

            val mBuilder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
            mBuilder.setTitle(getString(R.string.date_format_dialog_title))
            mBuilder.setSingleChoiceItems(listItems, mSelectedItemPosition) { dialogInterface, i ->
                mSelectedItemPosition = i
                mPreferenceHelper.putInt(PreferenceHelper.DATE_FORMAT_KEY, i)
                dialogInterface.dismiss()
            }
            val mDialog = mBuilder.create()
            mDialog.show()

            val adapter = RecyclerViewAdapter.getInstance()
            adapter.reloadTasks()
            true
        }

        val createBackup = findPreference("backup")
        createBackup.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mIsCreatingProcess = true

            if (hasPermissions()) {
                mBackupHelper.showCreateDialog()
            } else {
                requestPermissionWithRationale()
            }
            true
        }

        val restoreBackup = findPreference("restore")
        restoreBackup.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (hasPermissions()) {
                mBackupHelper.showRestoreDialog()
            } else {
                requestPermissionWithRationale()
            }
            true
        }

        findPreference("about_version").onPreferenceClickListener = createPreferenceClickListener("https://github.com/Jizzu/SimpleToDo")

        val rateThisApp = findPreference("rate_app")
        rateThisApp.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val appPackageName = activity.packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }

            true
        }

        val sendFeedback = findPreference("send_feedback")
        sendFeedback.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val email = Intent(Intent.ACTION_SENDTO)
            email.data = Uri.Builder().scheme("mailto").build()
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf("ilya.ponomarenko.dev@gmail.com"))
            email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title))
            email.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_device_info) + "\n" + DeviceInfo.deviceInfo
                    + "\n" + getString(R.string.feedback_app_version) + BuildConfig.VERSION_NAME
                    + "\n" + getString(R.string.feedback) + "\n")
            try {
                startActivity(Intent.createChooser(email, "Send feedback"))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
            }

            true
        }

        val otherApps = findPreference("other_apps")
        otherApps.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Ilya+Ponomarenko")))
            true
        }

        val licences = findPreference("about_licenses")
        licences.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val intent = Intent(activity, LicensesActivity::class.java)
            startActivity(intent)
            true
        }
    }

    private fun createPreferenceClickListener(uriString: String): Preference.OnPreferenceClickListener {
        return Preference.OnPreferenceClickListener {
            val uri = Uri.parse(uriString)
            val intent = Intent(ACTION_VIEW, uri)
            startActivity(intent)
            true
        }
    }

    /**
     * Method that verifies the existence of a permission.
     */
    private fun hasPermissions(): Boolean {
        var result: Int
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        for (currentPermission in permissions) {
            result = activity.checkCallingOrSelfPermission(currentPermission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * Method for requesting permission.
     */
    private fun requestPerms() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // After calling this method, the user is presented with a dialog asking for permission
            // The user's response comes in the onRequestPermissionsResult method.
            requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var isAllowed = true

        when (requestCode) {
            PERMISSION_REQUEST_CODE ->

                for (res in grantResults) {
                    // If user granted all permissions.
                    isAllowed = isAllowed && res == PackageManager.PERMISSION_GRANTED
                }

            else ->
                // If user not granted permissions.
                isAllowed = false
        }

        if (isAllowed) {
            if (mIsCreatingProcess) {
                mBackupHelper.showCreateDialog()
            } else {
                mBackupHelper.showRestoreDialog()
            }
        } else {
            // We will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(activity, R.string.settings_permission_denied_toast, Toast.LENGTH_SHORT).show()
                } else {
                    showNoStoragePermissionSnackbar()
                }
            }
        }
        mIsCreatingProcess = false
    }

    /**
     * If the user choose "Don't ask again" Snackbar appears with a link to the settings.
     */
    private fun showNoStoragePermissionSnackbar() {
        Snackbar.make(activity.findViewById(R.id.activity_view), R.string.settings_permission_snackbar_no_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.settings_permission_snackbar_button_settings) {
                    openApplicationSettings()

                    Toast.makeText(activity.applicationContext,
                            R.string.settings_permission_settings_toast,
                            Toast.LENGTH_LONG)
                            .show()
                }
                .show()
    }

    /**
     * Method for opening system settings page.
     */
    private fun openApplicationSettings() {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.packageName))
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE)
    }

    /**
     * If the user clicks "Deny" button, Snackbar appears with a rationale message.
     */
    private fun requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val message = getString(R.string.settings_permission_snackbar_with_rationale)
            Snackbar.make(activity.findViewById(R.id.activity_view), message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.settings_permission_snackbar_button_grant) { requestPerms() }
                    .show()
        } else {
            requestPerms()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}
