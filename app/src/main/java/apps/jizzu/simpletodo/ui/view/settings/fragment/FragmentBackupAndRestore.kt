package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.Manifest
import android.app.Activity
import android.app.Application
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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.toast
import apps.jizzu.simpletodo.utils.toastLong
import apps.jizzu.simpletodo.vm.BackupViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_backup_and_restore.*

class FragmentBackupAndRestore : BaseSettingsFragment() {
    private lateinit var mViewModel: BackupViewModel
    private var mIsCreatingProcess = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_backup_and_restore, container, false)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_backup_and_restore))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { mViewModel = createViewModel(it.application) }
        setOnClickListeners()
    }

    private fun showCreateDialog() {
        AlertDialog.Builder(activity as Context, R.style.DialogTheme).apply {
            setMessage(R.string.backup_create_dialog_message)
            setPositiveButton(R.string.backup_create_dialog_button) { _, _ -> createBackup() }
            setNegativeButton(R.string.action_cancel) { _, _ -> }
            show()
        }
    }

    private fun showRestoreDialog() {
        AlertDialog.Builder(activity as Context, R.style.DialogTheme).apply {
            setMessage(R.string.backup_restore_dialog_message)
            setPositiveButton(R.string.backup_restore_dialog_button) { _, _ -> restoreBackup() }
            setNegativeButton(R.string.action_cancel) { _, _ -> }
            show()
        }
    }

    private fun createBackup() {
        mViewModel.createBackup()

        if (mViewModel.isBackupCreatedSuccessfully()) {
            toast(getString(R.string.backup_create_message_success))
        } else {
            toast(getString(R.string.backup_create_message_failure))
        }
    }

    private fun restoreBackup() {
        if (mViewModel.isBackupExist()) {
            mViewModel.restoreBackup()

            if (mViewModel.isBackupRestoredSuccessfully()) {
                toast(getString(R.string.backup_restore_message_success))
            } else {
                toast(getString(R.string.backup_restore_message_failure))
            }
        } else {
            toast(getString(R.string.backup_restore_message_nothing))
        }
    }

    private fun setOnClickListeners() {
        buttonCreateBackup.setOnClickListener {
            mIsCreatingProcess = true

            if (isHasPermissions()) {
                if (mViewModel.isBackupExist()) {
                    showCreateDialog()
                } else mViewModel.createBackup()
            } else {
                requestPermissionWithRationale()
            }
        }

        buttonRestoreBackup.setOnClickListener {
            if (isHasPermissions()) {
                showRestoreDialog()
            } else {
                requestPermissionWithRationale()
            }
        }
    }

    private fun isHasPermissions(): Boolean {
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
            PERMISSION_REQUEST_CODE -> {
                for (res in grantResults) {
                    // If user granted all permissions.
                    isAllowed = isAllowed && res == PackageManager.PERMISSION_GRANTED
                }
            }

            else -> {
                // If user not granted permissions.
                isAllowed = false
            }
        }

        if (isAllowed) {
            if (mIsCreatingProcess) {
                showCreateDialog()
            } else {
                showRestoreDialog()
            }
        } else {
            // We will give warning to user that they haven't granted permissions.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    toast(getString(R.string.settings_permission_denied_toast))
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
                    toastLong(getString(R.string.settings_permission_settings_toast))
                }.show()
    }

    private fun openApplicationSettings() =
        startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("$PACKAGE${activity!!.packageName}")), PERMISSION_REQUEST_CODE)

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

    private fun createViewModel(application: Application) = ViewModelProviders.of(this).get(BackupViewModel(application)::class.java)

    private companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val PACKAGE = "package:"
    }
}