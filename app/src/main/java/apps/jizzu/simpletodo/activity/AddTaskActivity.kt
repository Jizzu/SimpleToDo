package apps.jizzu.simpletodo.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.activity.base.BaseTaskActivity
import kotlinx.android.synthetic.main.activity_add_task.*
import java.util.*

/**
 * Activity for adding a new task to RecyclerView.
 */
class AddTaskActivity : BaseTaskActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar(getString(R.string.create_task))
        mReminderLayout.visibility = View.INVISIBLE

        // If the user specified only the date (without time), then the notification of the event will appear in an hour.
        mCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1)

        addTaskButton.setOnClickListener {
            when {
                mTitleEditText.length() == 0 -> taskTitleLayout.error = getString(R.string.error_text_input)
                mTitleEditText.text.toString().trim { it <= ' ' }.isEmpty() -> taskTitleLayout.error = getString(R.string.error_spaces)
                else -> {
                    val intent = Intent()
                    intent.putExtra("title", mTitleEditText.text.toString())

                    if (mDateEditText.length() != 0 || mTimeEditText.length() != 0) {
                        intent.putExtra("date", mCalendar.timeInMillis)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
            hideKeyboard(mTitleEditText)
        }
    }
}
