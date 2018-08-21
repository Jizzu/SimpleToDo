package apps.jizzu.simpletodo.activity

import android.animation.Animator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.fragment.DatePickerFragment
import apps.jizzu.simpletodo.fragment.TimePickerFragment
import apps.jizzu.simpletodo.utils.Utils
import kotlinx.android.synthetic.main.activity_add_task.*
import kotterknife.bindView
import java.util.*

/**
 * Activity for adding a new task to RecyclerView.
 */
class AddTaskActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val mTitle: EditText by bindView(R.id.taskTitle)
    private val mDate: EditText by bindView(R.id.taskDate)
    private val mTime: EditText by bindView(R.id.taskTime)
    private val mReminderSwitch: SwitchCompat by bindView(R.id.reminderSwitch)
    private val mReminderLayout: RelativeLayout by bindView(R.id.reminderContainer)

    private lateinit var mCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        title = ""
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = getString(R.string.create_task)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        }

        MainActivity.mActivityIsShown = true

        // Get the resolution of the user's screen
        val displayMetrics = DisplayMetrics()
        (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        Log.d(TAG, "width = $width, height = $height")

        if (width <= 480 || height <= 800) {
            tvSetReminder.setText(R.string.set_reminder_short)
            taskDateLayout.layoutParams.width = 150
            taskTimeLayout.layoutParams.width = 150
        }

        mReminderLayout.visibility = View.INVISIBLE

        mReminderSwitch.setOnClickListener {
            if (mReminderSwitch.isChecked) {
                hideKeyboard(mTitle)
                mReminderLayout.animate().alpha(1.0f).setDuration(500).setListener(
                        object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {
                                mReminderLayout.visibility = View.VISIBLE
                            }

                            override fun onAnimationEnd(animation: Animator) {}

                            override fun onAnimationCancel(animation: Animator) {}

                            override fun onAnimationRepeat(animation: Animator) {}
                        }
                )
            } else {
                mReminderLayout.animate().alpha(0.0f).setDuration(500).setListener(
                        object : Animator.AnimatorListener {
                            override fun onAnimationStart(animation: Animator) {

                            }

                            override fun onAnimationEnd(animation: Animator) {
                                mReminderLayout.visibility = View.INVISIBLE
                            }

                            override fun onAnimationCancel(animation: Animator) {

                            }

                            override fun onAnimationRepeat(animation: Animator) {

                            }
                        }
                )
                mDate.text = null
                mTime.text = null
            }
        }

        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        container.setOnClickListener { hideKeyboard(mTitle) }

        mCalendar = Calendar.getInstance()
        // If the user specified only the date (without time), then the notification of the event will appear in an hour.
        mCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1)

        mDate.setOnClickListener {
            mDate.text = null
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(fragmentManager, "DatePickerFragment")
        }

        mTime.setOnClickListener {
            mTime.text = null
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.show(fragmentManager, "TimePickerFragment")
        }

        addTaskButton.setOnClickListener {
            when {
                mTitle.length() == 0 -> mTitle.error = getString(R.string.error_text_input)
                mTitle.text.toString().trim { it <= ' ' }.isEmpty() -> mTitle.error = getString(R.string.error_spaces)
                else -> {
                    val intent = Intent()
                    intent.putExtra("title", mTitle.text.toString())

                    if (mDate.length() != 0 || mTime.length() != 0) {
                        intent.putExtra("date", mCalendar.timeInMillis)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
            hideKeyboard(mTitle)
        }
    }

    /**
     * Method for hiding the soft keyboard.
     */
    private fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    /**
     * Hides the soft keyboard when the user clicks on the home button.
     */
    override fun onStop() {
        super.onStop()
        hideKeyboard(mTitle)
    }

    /**
     * The handler for clicking the close button in the toolbar.
     */
    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == android.R.id.home) {
            hideKeyboard(mTitle)
            onBackPressed()
            true
        } else false

    /**
     * Sets the date selected in the DatePickerFragment.
     */
    override fun onDateSet(datePicker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, monthOfYear)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        mDate.setText(Utils.getDate(mCalendar.timeInMillis))
    }

    /**
     * Sets the time selected in the TimePickerFragment.
     */
    override fun onTimeSet(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        mCalendar.set(Calendar.MINUTE, minute)
        mCalendar.set(Calendar.SECOND, 0)
        mTime.setText(Utils.getTime(mCalendar.timeInMillis))
    }
}
