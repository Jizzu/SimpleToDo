package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.ui.view.base.BaseTaskActivity
import apps.jizzu.simpletodo.utils.DateAndTimeFormatter
import apps.jizzu.simpletodo.utils.invisible
import apps.jizzu.simpletodo.utils.toast
import apps.jizzu.simpletodo.utils.visible
import apps.jizzu.simpletodo.vm.EditTaskViewModel
import kotlinx.android.synthetic.main.activity_task_details.*
import java.util.*

class EditTaskActivity : BaseTaskActivity() {
    private var mId: Long = 0
    private var mDate: Long = 0
    private var mPosition: Int = 0
    private var mTimeStamp: Long = 0
    private lateinit var mViewModel: EditTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar(getString(R.string.edit_task))
        mViewModel = createViewModel()

        // Get Intent data
        mId = intent.getLongExtra("id", 0)
        val title = intent.getStringExtra("title")
        mDate = intent.getLongExtra("date", 0)
        mPosition = intent.getIntExtra("position", 0)
        mTimeStamp = intent.getLongExtra("time_stamp", 0)

        mTitleEditText.setText(title)
        mTitleEditText.setSelection(mTitleEditText.text!!.length)
        if (mDate != 0L) {
            mDateEditText.setText(DateAndTimeFormatter.getDate(mDate))
            mTimeEditText.setText(DateAndTimeFormatter.getTime(mDate))
        }

        if (mDate == 0L) {
            mReminderLayout.invisible()
            mReminderSwitch.isChecked = false
            mDateEditText.text = null
            mTimeEditText.text = null
        } else {
            mReminderLayout.visible()
            mReminderSwitch.isChecked = true
        }

        if (mDateEditText.length() != 0 || mTimeEditText.length() != 0) {
            mCalendar.timeInMillis = mDate
        }
        // If the user specified only the date (without time), then the notification of the event will appear in an hour.
        if (mTimeEditText.length() == 0) {
            mCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1)
        }

        addTaskButton.text = getString(R.string.update_task)
        addTaskButton.setOnClickListener {
            when {
                mTitleEditText.length() == 0 -> taskTitleLayout.error = getString(R.string.error_text_input)
                mTitleEditText.text.toString().trim { it <= ' ' }.isEmpty() -> taskTitleLayout.error = getString(R.string.error_spaces)
                else -> {
                    val task = Task(mId, mTitleEditText.text.toString(), mDate, mPosition, mTimeStamp)

                    if (mDateEditText.length() != 0 || mTimeEditText.length() != 0) {
                        task.date = mCalendar.timeInMillis
                    }

                    if (!mReminderSwitch.isChecked || mDateEditText.length() == 0 && mTimeEditText.length() == 0) {
                        task.date = 0
                    }

                    if (task.date != 0L && task.date <= Calendar.getInstance().timeInMillis) {
                        task.date = 0
                        toast(getString(R.string.toast_incorrect_time))
                    } else if (task.date != 0L) {
                        val alarmHelper = AlarmHelper.getInstance()
                        alarmHelper.setAlarm(task)
                    } else if (task.date == 0L) {
                        val alarmHelper = AlarmHelper.getInstance()
                        alarmHelper.removeAlarm(task.timeStamp)
                        alarmHelper.removeNotification(task.timeStamp, this)
                    }
                    mViewModel.updateTask(task)
                    finish()
                }
            }
            hideKeyboard(mTitleEditText)
        }
    }

    override fun createViewModel() = ViewModelProviders.of(this).get(EditTaskViewModel(application)::class.java)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_task_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            android.R.id.home -> {
                hideKeyboard(mTitleEditText)
                onBackPressed()
            }

            R.id.action_delete -> {
                val alertDialog = AlertDialog.Builder(this, R.style.DialogTheme)
                alertDialog.setTitle(R.string.dialog_title)
                alertDialog.setMessage(R.string.dialog_message)
                alertDialog.setPositiveButton(R.string.action_delete) { _, _ ->
                    hideKeyboard(mTitleEditText)
                    val task = Task(mId, mTitleEditText.text.toString(), mDate, mPosition, mTimeStamp)
                    if (task.date != 0L) {
                        val alarmHelper = AlarmHelper.getInstance()
                        alarmHelper.removeAlarm(task.timeStamp)
                        alarmHelper.removeNotification(task.timeStamp, this)
                    }
                    mViewModel.deleteTask(task)
                    finish()
                }
                alertDialog.setNegativeButton(R.string.action_cancel) { _, _ -> }
                alertDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
