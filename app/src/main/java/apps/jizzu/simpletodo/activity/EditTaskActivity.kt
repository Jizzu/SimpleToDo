package apps.jizzu.simpletodo.activity

import android.animation.Animator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter
import apps.jizzu.simpletodo.alarm.AlarmHelper
import apps.jizzu.simpletodo.database.DBHelper
import apps.jizzu.simpletodo.fragment.DatePickerFragment
import apps.jizzu.simpletodo.fragment.TimePickerFragment
import apps.jizzu.simpletodo.model.ModelTask
import apps.jizzu.simpletodo.utils.Utils
import kotlinx.android.synthetic.main.activity_add_task.*
import kotterknife.bindView
import java.util.*

/**
 * Activity for editing a chosen task in the RecyclerView.
 */
class EditTaskActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val mTitle: EditText by bindView(R.id.taskTitle)
    private val mReminderLayout: RelativeLayout by bindView(R.id.reminderContainer)
    private val mDateEditText: EditText by bindView(R.id.taskDate)
    private val mTimeEditText: EditText by bindView(R.id.taskTime)
    private val mReminderSwitch: SwitchCompat by bindView(R.id.reminderSwitch)

    private lateinit var mContext: Context
    private lateinit var mCalendar: Calendar
    private lateinit var mAdapter: RecyclerViewAdapter

    private var mId: Long = 0
    private var mDate: Long = 0
    private var mPosition: Int = 0
    private var mTimeStamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        title = ""
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = getString(R.string.edit_task)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        }

        val relativeLayout = findViewById<RelativeLayout>(R.id.container)
        val addTaskButton = findViewById<Button>(R.id.addTaskButton)
        val reminderText = findViewById<TextView>(R.id.tvSetReminder)

        MainActivity.mActivityIsShown = true
        mContext = applicationContext

        // Get the resolution of the user's screen
        val displayMetrics = DisplayMetrics()
        (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        Log.d(TAG, "width = $width, height = $height")

        if (width <= 480 || height <= 800) {
            reminderText.setText(R.string.set_reminder_short)
            taskDateLayout.layoutParams.width = 150
            taskTimeLayout.layoutParams.width = 150
        }

        // Get Intent data
        mId = intent.getLongExtra("id", 0)
        val title = intent.getStringExtra("title")
        mDate = intent.getLongExtra("date", 0)
        mPosition = intent.getIntExtra("position", 0)
        mTimeStamp = intent.getLongExtra("time_stamp", 0)

        mAdapter = RecyclerViewAdapter.getInstance()

        Log.d(TAG, "TASK DATE = $mDate")

        mTitle.setText(title)
        mTitle.setSelection(mTitle.text.length)
        if (mDate != 0L) {
            mDateEditText.setText(Utils.getDate(mDate))
            mTimeEditText.setText(Utils.getTime(mDate))
        }
        addTaskButton.text = getString(R.string.update_task)

        if (mDate == 0L) {
            mReminderLayout.visibility = View.INVISIBLE
            mReminderSwitch.isChecked = false
            mDateEditText.text = null
            mTimeEditText.text = null
        } else {
            mReminderLayout.visibility = View.VISIBLE
            mReminderSwitch.isChecked = true
        }

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
                mDateEditText.text = null
                mTimeEditText.setText(null)
            }
        }

        val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        relativeLayout.setOnClickListener { hideKeyboard(mTitle) }

        mCalendar = Calendar.getInstance()
        if (mDateEditText.length() != 0 || mTimeEditText.length() != 0) {
            mCalendar.timeInMillis = mDate
        }
        // If the user specified only the date (without time), then the notification of the event will appear in an hour.
        if (mTimeEditText.length() == 0) {
            mCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1)
        }

        mDateEditText.setOnClickListener {
            mDateEditText.text = null
            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(fragmentManager, "DatePickerFragment")
        }

        mTimeEditText.setOnClickListener {
            mTimeEditText.text = null
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.show(fragmentManager, "TimePickerFragment")
        }

        addTaskButton.setOnClickListener {
            when {
                mTitle.length() == 0 -> mTitle.error = getString(R.string.error_text_input)
                mTitle.text.toString().trim { it <= ' ' }.isEmpty() -> mTitle.error = getString(R.string.error_spaces)
                else -> {
                    val dbHelper = DBHelper.getInstance(mContext)

                    val task = ModelTask(mId, mTitle.text.toString(), mDate, mPosition, mTimeStamp)

                    if (mDateEditText.length() != 0 || mTimeEditText.length() != 0) {
                        task.date = mCalendar.timeInMillis
                    }

                    if (!mReminderSwitch.isChecked || mDateEditText.length() == 0 && mTimeEditText.length() == 0) {
                        task.date = 0
                    }
                    Log.d(TAG, "Title = ${task.title}, date = ${task.date}, position = ${task.position}")

                    dbHelper.updateTask(task)
                    mAdapter.updateTask(task, task.position)

                    if (task.date != 0L && task.date <= Calendar.getInstance().timeInMillis) {
                        task.date = 0
                        Toast.makeText(mContext, getString(R.string.toast_incorrect_time), Toast.LENGTH_SHORT).show()
                    } else if (task.date != 0L) {
                        val alarmHelper = AlarmHelper.getInstance()
                        alarmHelper.setAlarm(task)
                    } else if (task.date == 0L) {
                        val mAlarmHelper = AlarmHelper.getInstance()
                        mAlarmHelper.removeAlarm(task.timeStamp)
                    }
                    finish()
                }
            }
            hideKeyboard(mTitle)
        }
    }

    /**
     * Hides the soft keyboard when the user clicks on the home button.
     */
    override fun onStop() {
        super.onStop()
        hideKeyboard(mTitle)
    }

    /**
     * Method for hiding the soft keyboard.
     */
    private fun hideKeyboard(editText: EditText?) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText!!.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_task_menu, menu)
        return true
    }

    /**
     * The handler for clicking the close button in the toolbar.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            android.R.id.home -> {
                hideKeyboard(mTitle)
                onBackPressed()
            }

            R.id.action_delete -> {
                val alertDialog = AlertDialog.Builder(this, R.style.DialogTheme)
                alertDialog.setTitle(R.string.dialog_title)
                alertDialog.setMessage(R.string.dialog_message)
                alertDialog.setPositiveButton(R.string.action_delete) { _, _ ->
                    hideKeyboard(mTitle)
                    val task = ModelTask(mId, mTitle.text.toString(), mDate, mPosition, mTimeStamp)
                    Log.d(TAG, "EDIT TASK ACTIVITY: task position = ${task.position}")
                    mAdapter.removeTask(task.position)
                    if (mAdapter.itemCount == 0 && MainActivity.mSearchViewIsOpen) {
                        MainActivity.mShowAnimation = true
                    }
                    finish()
                }
                alertDialog.setNegativeButton(R.string.action_cancel) { _, _ -> }
                alertDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Sets the date selected in the DatePickerFragment.
     */
    override fun onDateSet(datePicker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        mCalendar.set(Calendar.YEAR, year)
        mCalendar.set(Calendar.MONTH, monthOfYear)
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        mDateEditText.setText(Utils.getDate(mCalendar.timeInMillis))
    }

    /**
     * Sets the time selected in the TimePickerFragment.
     */
    override fun onTimeSet(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        mCalendar.set(Calendar.MINUTE, minute)
        mCalendar.set(Calendar.SECOND, 0)
        mTimeEditText.setText(Utils.getTime(mCalendar.timeInMillis))
    }
}
