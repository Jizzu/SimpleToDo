package apps.jizzu.simpletodo.ui.view.task

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.service.alarm.AlarmHelper
import apps.jizzu.simpletodo.ui.dialogs.DeleteTaskDialogFragment
import apps.jizzu.simpletodo.ui.view.base.BaseTaskActivity
import apps.jizzu.simpletodo.utils.DateAndTimeFormatter
import apps.jizzu.simpletodo.utils.toast
import apps.jizzu.simpletodo.utils.visible
import apps.jizzu.simpletodo.vm.EditTaskViewModel
import kotlinx.android.synthetic.main.activity_task_details.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class EditTaskActivity : BaseTaskActivity() {
    private var mId: Long = 0
    private var mDate: Long = 0
    private var mPosition: Int = 0
    private var mTimeStamp: Long = 0
    private lateinit var mTitle: String
    private lateinit var mNote: String
    private lateinit var mViewModel: EditTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar(getString(R.string.edit_task))
        if (Locale.getDefault().displayLanguage == "français") {
            tvToolbarTitle.textSize = 18F
        }
        mViewModel = createViewModel()

        // Get Intent data
        mId = intent.getLongExtra("id", 0)
        mTitle = intent.getStringExtra("title")
        mNote = intent.getStringExtra("note")
        mDate = intent.getLongExtra("date", 0)
        mPosition = intent.getIntExtra("position", 0)
        mTimeStamp = intent.getLongExtra("time_stamp", 0)

        mTitleEditText.setText(mTitle)

        if (mNote.isNotEmpty()) {
            tvTaskNote.text = mNote
        }

        if (mDate != 0L) {
            tvTaskReminder.text = getString(R.string.date_format_at, DateAndTimeFormatter.getDate(mDate),
                    DateAndTimeFormatter.getTime(mDate))
            ivDeleteTaskReminder.visible()
        }

        if (tvTaskReminder.length() != 0) {
            mCalendar.timeInMillis = mDate
        }

        btnTaskConfirm.text = getString(R.string.update_task)
        btnTaskConfirm.setOnClickListener {
            when {
                mTitleEditText.length() == 0 -> tilTaskTitle.error = getString(R.string.error_text_input)
                mTitleEditText.text.toString().trim { it <= ' ' }.isEmpty() -> tilTaskTitle.error = getString(R.string.error_spaces)
                else -> {
                    val task = Task(mId, mTitleEditText.text.toString(), tvTaskNote.text.toString(), mDate, mPosition, mTimeStamp)

                    if (tvTaskReminder.length() != 0) {
                        task.date = mCalendar.timeInMillis
                    } else task.date = 0L

                    if (task.date != 0L && task.date <= Calendar.getInstance().timeInMillis) {
                        task.date = 0
                        toast(getString(R.string.toast_incorrect_time))
                    } else if (task.date != 0L) {
                        AlarmHelper.getInstance().setAlarm(task)
                    } else if (task.date == 0L) {
                        AlarmHelper.getInstance().apply {
                            removeAlarm(task.timeStamp)
                            removeNotification(task.timeStamp, this@EditTaskActivity)
                        }
                    }
                    mViewModel.updateTask(task)
                    finish()
                }
            }
            hideKeyboard(mTitleEditText)
        }
    }

    private fun showDeleteTaskDialog(task: Task) = DeleteTaskDialogFragment(task).show(supportFragmentManager, null)

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
                hideKeyboard(mTitleEditText)
                showDeleteTaskDialog(Task(mId, mTitle, mNote, mDate, mPosition, mTimeStamp))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}