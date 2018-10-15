package apps.jizzu.simpletodo.settings.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.BackupHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_backup_and_restore.*


class FragmentBackupAndRestore : BaseSettingsFragment() {
    private lateinit var mBackupHelper: BackupHelper
    private var mIsCreatingProcess = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_backup_and_restore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBackupHelper = BackupHelper(activity as Context)
        setTitle(getString(R.string.settings_page_title_backup_and_restore))
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        buttonCreateBackup.setOnClickListener {
            if (hasPermissions()) {
                mBackupHelper.showCreateDialog()
            } else {
                requestPermissionWithRationale()
            }
        }

        buttonRestoreBackup.setOnClickListener {
            if (hasPermissions()) {
                mBackupHelper.showRestoreDialog()
            } else {
                requestPermissionWithRationale()
            }
        }
    }

    private fun hasPermissions(): Boolean {
        var result: Int
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        for (currentPermission in permissions) {
            result = activity!!.checkCallingOrSelfPermission(currentPermission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPerms() {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE)
        }
    }

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

    private fun showNoStoragePermissionSnackbar() {
        Snackbar.make(rootLayout, R.string.settings_permission_snackbar_no_permission, Snackbar.LENGTH_LONG)
                .setAction(R.string.settings_permission_snackbar_button_settings) {
                    openApplicationSettings()

                    Toast.makeText(activity!!.applicationContext,
                            R.string.settings_permission_settings_toast,
                            Toast.LENGTH_LONG)
                            .show()
                }
                .show()
    }

    private fun openApplicationSettings() {
        val appSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity!!.packageName))
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE)
    }

    private fun requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val message = getString(R.string.settings_permission_snackbar_with_rationale)
            Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.settings_permission_snackbar_button_grant) { requestPerms() }
                    .show()
        } else {
            requestPerms()
        }
    }

    private companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }
}