package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.ui.view.base.BaseTaskActivity
import apps.jizzu.simpletodo.utils.invisible
import apps.jizzu.simpletodo.utils.toast
import apps.jizzu.simpletodo.vm.AddTaskViewModel
import kotlinx.android.synthetic.main.activity_task_details.*
import java.util.*

class AddTaskActivity : BaseTaskActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar(getString(R.string.create_task))
        reminderContainer.invisible()

        // If the user specified only the date (without time), then the notification of the event will appear in an hour
        mCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1)
        val position = intent.getIntExtra("position", 0)

        addTaskButton.setOnClickListener {
            when {
                mTitleEditText.length() == 0 -> taskTitleLayout.error = getString(R.string.error_text_input)
                mTitleEditText.text.toString().trim { it <= ' ' }.isEmpty() -> taskTitleLayout.error = getString(R.string.error_spaces)
                else -> {
                    val task = Task()
                    task.title = mTitleEditText.text.toString()
                    task.position = position

                    if (mDateEditText.length() != 0 || mTimeEditText.length() != 0) {
                        task.date = mCalendar.timeInMillis
                    }

                    if (task.date != 0L && task.date <= Calendar.getInstance().timeInMillis) {
                        task.date = 0
                        toast(getString(R.string.toast_incorrect_time))
                    } else if (task.date != 0L) {
                        AlarmHelper.getInstance().setAlarm(task)
                    }
                    val viewModel = createViewModel()
                    viewModel.saveTask(task)
                    finish()
                }
            }
            hideKeyboard(mTitleEditText)
        }
    }

    override fun createViewModel() = ViewModelProviders.of(this).get(AddTaskViewModel(application)::class.java)
}
