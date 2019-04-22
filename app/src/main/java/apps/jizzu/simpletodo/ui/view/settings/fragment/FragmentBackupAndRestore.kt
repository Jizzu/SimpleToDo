package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.dialogs.CreateBackupDialogFragment
import apps.jizzu.simpletodo.ui.dialogs.RestoreBackupDialogFragment
import apps.jizzu.simpletodo.ui.dialogs.base.BaseDialogFragment
import apps.jizzu.simpletodo.ui.view.base.BaseActivity
import apps.jizzu.simpletodo.ui.view.base.BaseActivity.Companion.PERMISSION_REQUEST_CODE
import apps.jizzu.simpletodo.ui.view.settings.activity.SettingsActivity
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.toast
import kotlinx.android.synthetic.main.fragment_backup_and_restore.*

class FragmentBackupAndRestore : BaseSettingsFragment() {
    private var mIsCreatingProcess = false
    private lateinit var mSettingsActivity: SettingsActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_backup_and_restore, container, false)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_backup_and_restore))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSettingsActivity = activity as SettingsActivity
        setOnClickListeners()
    }

    private fun showDialog(dialog: BaseDialogFragment) = activity?.let { dialog.show(it.supportFragmentManager, null) }

    private fun setOnClickListeners() {
        clCreateBackup.setOnClickListener {
            mIsCreatingProcess = true

            if (mSettingsActivity.isHasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showDialog(CreateBackupDialogFragment())
            } else {
                requestPermissionWithRationale()
            }
        }

        clRestoreBackup.setOnClickListener {
            if (mSettingsActivity.isHasPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showDialog(RestoreBackupDialogFragment())
            } else {
                requestPermissionWithRationale()
            }
        }
    }

    private fun requestPermissionWithRationale() =
        mSettingsActivity.requestPermissionWithRationale(llBackupAndRestore,
                getString(R.string.permission_storage_snackbar_with_rationale),
                Manifest.permission.WRITE_EXTERNAL_STORAGE, object : BaseActivity.PermissionRequestListener {
                    override fun onPermissionRequest() = mSettingsActivity.requestPerms(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            this@FragmentBackupAndRestore)
                })

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
                showDialog(CreateBackupDialogFragment())
            } else {
                showDialog(RestoreBackupDialogFragment())
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                toast(getString(R.string.permission_storage_denied_toast))
            } else {
                mSettingsActivity.showNoPermissionSnackbar(llBackupAndRestore,
                        getString(R.string.permission_storage_snackbar_no_permission),
                        getString(R.string.permission_storage_toast))
            }
        }
        mIsCreatingProcess = false
    }
}