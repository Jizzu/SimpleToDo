package apps.jizzu.simpletodo.ui.dialogs

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.ui.dialogs.base.BaseDialogFragment
import apps.jizzu.simpletodo.vm.DeleteTaskViewModel
import kotlinx.android.synthetic.main.dialog_default.*

class DeleteTaskDialogFragment(val task: Task) : BaseDialogFragment() {
    private lateinit var mViewModel: DeleteTaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dialog_default, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let { mViewModel = createViewModel(it.application) }
        initDialog()
    }

    private fun initDialog() {
        tvDialogMessage.setText(R.string.dialog_message)
        tvConfirm.setText(R.string.action_delete)
        tvConfirm.setOnClickListener {
            mViewModel.deleteTask(task)
            if (task.date != 0L) {
                val alarmHelper = AlarmHelper.getInstance()
                alarmHelper.removeAlarm(task.timeStamp)
                alarmHelper.removeNotification(task.timeStamp, activity!!.applicationContext)
            }
            activity?.finish()
        }
        tvCancel.setOnClickListener { dismiss() }
    }

    private fun createViewModel(application: Application) = ViewModelProviders.of(this).get(DeleteTaskViewModel(application)::class.java)
}
