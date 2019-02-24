package apps.jizzu.simpletodo.ui.dialogs

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
import kotlinx.android.synthetic.main.dialog_delete_task.*

class DeleteTaskDialogFragment(val task: Task) : BaseDialogFragment() {
    private lateinit var mViewModel: DeleteTaskViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.dialog_delete_task, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = createViewModel()
        initButtons()
    }

    private fun initButtons() {
        buttonDeleteTask.setOnClickListener {
            mViewModel.deleteTask(task)
            if (task.date != 0L) {
                val alarmHelper = AlarmHelper.getInstance()
                alarmHelper.removeAlarm(task.timeStamp)
                alarmHelper.removeNotification(task.timeStamp, activity!!.applicationContext)
            }
            activity?.finish()
        }
        buttonCancel.setOnClickListener { dismiss() }
    }

    private fun createViewModel() = ViewModelProviders.of(this).get(DeleteTaskViewModel(activity!!.application)::class.java)
}
