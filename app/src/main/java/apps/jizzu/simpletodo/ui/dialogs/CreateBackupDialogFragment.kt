package apps.jizzu.simpletodo.ui.dialogs

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.dialogs.base.BaseDialogFragment
import apps.jizzu.simpletodo.utils.toast
import apps.jizzu.simpletodo.vm.CreateBackupViewModel
import kotlinx.android.synthetic.main.dialog_default.*

class CreateBackupDialogFragment : BaseDialogFragment() {
    private lateinit var mViewModel: CreateBackupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dialog_default, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { mViewModel = createViewModel(it.application) }
        initDialog()
    }

    private fun initDialog() {
        tvDialogMessage.setText(R.string.backup_create_dialog_message)
        tvConfirm.setText(R.string.backup_create_dialog_button)
        tvConfirm.setOnClickListener { createBackup() }
        tvCancel.setOnClickListener { dismiss() }
    }

    private fun createBackup() {
        mViewModel.createBackup()

        if (mViewModel.isBackupCreatedSuccessfully()) {
            dismiss()
            toast(getString(R.string.backup_create_message_success))
        } else {
            dismiss()
            toast(getString(R.string.backup_create_message_failure))
        }
    }

    private fun createViewModel(application: Application) = ViewModelProviders.of(this).get(CreateBackupViewModel(application)::class.java)
}