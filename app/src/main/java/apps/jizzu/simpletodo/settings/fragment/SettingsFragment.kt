package apps.jizzu.simpletodo.settings.fragment

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import apps.jizzu.simpletodo.BuildConfig
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.recycler.RecyclerViewAdapter
import apps.jizzu.simpletodo.alarm.AlarmReceiver
import apps.jizzu.simpletodo.settings.activity.LicensesActivity
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

    private var mIsCreatingProcess = false
    private var mSelectedItemPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBackupHelper = BackupHelper(activity)
        mPreferenceHelper = PreferenceHelper.getInstance()

        val screen = preferenceManager.createPreferenceScreen(activity)

        val categoryGeneralSettings = PreferenceCategory(activity)
        categoryGeneralSettings.title = getString(R.string.category_general)
        screen.addPreference(categoryGeneralSettings)

        mAnimation = CheckBoxPreference(activity)
        mAnimation.title = getString(R.string.preferences_animation_title)
        mAnimation.summary = getString(R.string.preferences_animation_summary)
        mAnimation.isChecked = mPreferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)
        categoryGeneralSettings.addPreference(mAnimation)

        mAnimation.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mPreferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, mAnimation.isChecked)
            true
        }

        mGeneralNotification = CheckBoxPreference(activity)
        mGeneralNotification.title = getString(R.string.preferences_general_notification_title)
        mGeneralNotification.summary = getString(R.string.preferences_general_notification_summary)
        mGeneralNotification.isChecked = mPreferenceHelper.getBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON)
        categoryGeneralSettings.addPreference(mGeneralNotification)

        mGeneralNotification.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mPreferenceHelper.putBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON, mGeneralNotification.isChecked)
            true
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = notificationManager.getNotificationChannel(AlarmReceiver.NOTIFICATION_CHANNEL_ID)

            if (notificationChannel == null) {
                val channel = NotificationChannel(AlarmReceiver.NOTIFICATION_CHANNEL_ID, context.getString(R.string.notification_channel),
                        NotificationManager.IMPORTANCE_HIGH)

                channel.enableLights(true)
                channel.lightColor = Color.GREEN
                channel.enableVibration(true)
                notificationManager.createNotificationChannel(channel)
            }

            val notificationSound = Preference(activity)
            notificationSound.title = getString(R.string.preferences_notification_channel_title)
            notificationSound.summary = getString(R.string.preferences_notification_channel_summary)
            categoryGeneralSettings.addPreference(notificationSound)

            notificationSound.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        .putExtra(Settings.EXTRA_CHANNEL_ID, AlarmReceiver.NOTIFICATION_CHANNEL_ID)
                startActivity(intent)
                true
            }
        } else {
            val notificationSound = RingtonePreference(activity)
            notificationSound.key = "notification_sound"
            notificationSound.title = getString(R.string.preferences_notification_sound_title)
            notificationSound.summary = getString(R.string.preferences_notification_sound_summary)
            notificationSound.setDefaultValue("content://settings/system/notification_sound")
            notificationSound.ringtoneType = 2
            notificationSound.showDefault = true
            notificationSound.showSilent = true
            categoryGeneralSettings.addPreference(notificationSound)
        }

        val categoryDateAndTime = PreferenceCategory(activity)
        categoryDateAndTime.title = getString(R.string.category_date_and_time)
        screen.addPreference(categoryDateAndTime)

        val dateFormat = Preference(activity)
        dateFormat.title = getString(R.string.preferences_date_format_title)
        dateFormat.summary = getString(R.string.preferences_date_format_summary)
        categoryDateAndTime.addPreference(dateFormat)

        dateFormat.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val listItems = resources.getStringArray(R.array.date_format_list)
            mSelectedItemPosition = mPreferenceHelper.getInt(PreferenceHelper.DATE_FORMAT_KEY)

            val alertDialog = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
            alertDialog.setTitle(getString(R.string.date_format_dialog_title))
            alertDialog.setSingleChoiceItems(listItems, mSelectedItemPosition) { dialogInterface, i ->
                mSelectedItemPosition = i
                mPreferenceHelper.putInt(PreferenceHelper.DATE_FORMAT_KEY, i)
                dialogInterface.dismiss()
            }
            alertDialog.show()

            val adapter = RecyclerViewAdapter.getInstance()
            adapter.reloadTasks()
            true
        }

        val timeFormat = Preference(activity)
        timeFormat.title = getString(R.string.preferences_time_format_title)
        timeFormat.summary = getString(R.string.preferences_time_format_summary)
        categoryDateAndTime.addPreference(timeFormat)

        timeFormat.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val listItems = resources.getStringArray(R.array.time_format_list)
            mSelectedItemPosition = mPreferenceHelper.getInt(PreferenceHelper.TIME_FORMAT_KEY)

            val mBuilder = AlertDialog.Builder(activity, R.style.AlertDialogStyle)
            mBuilder.setTitle(getString(R.string.time_format_dialog_title))
            mBuilder.setSingleChoiceItems(listItems, mSelectedItemPosition) { dialogInterface, i ->
                mSelectedItemPosition = i
                mPreferenceHelper.putInt(PreferenceHelper.TIME_FORMAT_KEY, i)
                dialogInterface.dismiss()
            }
            val mDialog = mBuilder.create()
            mDialog.show()

            val adapter = RecyclerViewAdapter.getInstance()
            adapter.reloadTasks()
            true
        }

        val categoryBackupAndRestore = PreferenceCategory(activity)
        categoryBackupAndRestore.title = getString(R.string.category_backup_and_restore)
        screen.addPreference(categoryBackupAndRestore)

        val createBackup = Preference(activity)
        createBackup.title = getString(R.string.preferences_backup_create_title)
        createBackup.summary = getString(R.string.preferences_backup_create_summary)
        categoryBackupAndRestore.addPreference(createBackup)

        createBackup.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            mIsCreatingProcess = true

            if (hasPermissions()) {
                mBackupHelper.showCreateDialog()
            } else {
                requestPermissionWithRationale()
            }
            true
        }

        val restoreBackup = Preference(activity)
        restoreBackup.title = getString(R.string.preferences_backup_restore_title)
        restoreBackup.summary = getString(R.string.preferences_backup_restore_summary)
        categoryBackupAndRestore.addPreference(restoreBackup)

        restoreBackup.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (hasPermissions()) {
                mBackupHelper.showRestoreDialog()
            } else {
                requestPermissionWithRationale()
            }
            true
        }

        val categoryAdditional = PreferenceCategory(activity)
        categoryAdditional.title = getString(R.string.category_additional)
        screen.addPreference(categoryAdditional)

        val rateThisApp = Preference(activity)
        rateThisApp.title = getString(R.string.preferences_rate_app_title)
        rateThisApp.summary = getString(R.string.preferences_rate_app_summary)
        categoryAdditional.addPreference(rateThisApp)

        rateThisApp.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val appPackageName = activity.packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
            true
        }

        val sendFeedback = Preference(activity)
        sendFeedback.title = getString(R.string.preferences_send_feedback_title)
        sendFeedback.summary = getString(R.string.preferences_send_feedback_summary)
        categoryAdditional.addPreference(sendFeedback)

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

        val otherApps = Preference(activity)
        otherApps.title = getString(R.string.preferences_other_apps_title)
        otherApps.summary = getString(R.string.preferences_other_apps_summary)
        categoryAdditional.addPreference(otherApps)

        otherApps.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Ilya+Ponomarenko")))
            true
        }

        val categoryAbout = PreferenceCategory(activity)
        categoryAbout.title = getString(R.string.category_about)
        screen.addPreference(categoryAbout)

        val aboutVersion = Preference(activity)
        aboutVersion.title = getString(R.string.preferences_about_version_title)
        aboutVersion.summary = "1.3"
        categoryAbout.addPreference(aboutVersion)

        aboutVersion.onPreferenceClickListener = createPreferenceClickListener("https://github.com/Jizzu/SimpleToDo")

        val licences = Preference(activity)
        licences.title = getString(R.string.preferences_about_licenses_title)
        licences.summary = getString(R.string.preferences_about_licenses_summary)
        categoryAbout.addPreference(licences)

        licences.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val intent = Intent(activity, LicensesActivity::class.java)
            startActivity(intent)
            true
        }
        preferenceScreen = screen
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
