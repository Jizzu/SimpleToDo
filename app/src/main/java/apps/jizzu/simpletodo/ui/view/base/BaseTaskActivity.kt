package apps.jizzu.simpletodo.ui.view.base

import android.animation.Animator
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.TaskDescriptionActivity
import apps.jizzu.simpletodo.utils.DateAndTimeFormatter
import apps.jizzu.simpletodo.utils.PreferenceHelper
import apps.jizzu.simpletodo.utils.invisible
import apps.jizzu.simpletodo.utils.visible
import apps.jizzu.simpletodo.vm.base.BaseViewModel
import daio.io.dresscode.dressCodeStyleId
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_task_details.*
import kotlinx.android.synthetic.main.toolbar.*
import kotterknife.bindView
import java.util.*

abstract class BaseTaskActivity : BaseActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    val mTitleEditText: EditText by bindView(R.id.taskTitle)
//    val mDateEditText: EditText by bindView(R.id.taskDate)
//    val mTimeEditText: EditText by bindView(R.id.taskTime)
    lateinit var mCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        setContentView(R.layout.activity_task_details)
        mCalendar = Calendar.getInstance()
        checkScreenResolution()
        initTheme()
        showKeyboard(mTitleEditText)
        initListeners()
    }

    private fun initTheme() {
        when (this.dressCodeStyleId) {
            R.style.AppTheme_Light -> taskTitle.setHintTextColor(ContextCompat.getColor(this, R.color.black))
            R.style.AppTheme_Dark -> taskTitle.setHintTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun checkScreenResolution() {
        val displayMetrics = DisplayMetrics()
        (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

//        if (width <= 480 || height <= 800) {
//            if (Locale.getDefault().displayLanguage == RU) toolbarTitle.textSize = 18f
//            tvSetReminder.setText(R.string.set_reminder_short)
//            taskDateLayout.layoutParams.width = 150
//            taskTimeLayout.layoutParams.width = 150
//        }
    }

    private fun initListeners() {
//        reminderSwitch.setOnTouchListener { _, event -> event.actionMasked == MotionEvent.ACTION_MOVE }
//        reminderSwitch.setOnClickListener {
//            if (reminderSwitch.isChecked) {
//                hideKeyboard(mTitleEditText)
//                reminderContainer.animate().alpha(1.0f).setDuration(500).setListener(
//                        object : Animator.AnimatorListener {
//                            override fun onAnimationStart(animation: Animator) {
//                                reminderContainer.visible()
//
//                            }
//
//                            override fun onAnimationEnd(animation: Animator) {}
//
//                            override fun onAnimationCancel(animation: Animator) {}
//
//                            override fun onAnimationRepeat(animation: Animator) {}
//                        }
//                )
//            } else {
//                reminderContainer.animate().alpha(0.0f).setDuration(500).setListener(
//                        object : Animator.AnimatorListener {
//                            override fun onAnimationStart(animation: Animator) {
//
//                            }
//
//                            override fun onAnimationEnd(animation: Animator) {
//                                reminderContainer.invisible()
//                            }
//
//                            override fun onAnimationCancel(animation: Animator) {
//
//                            }
//
//                            override fun onAnimationRepeat(animation: Animator) {
//
//                            }
//                        }
//                )
//                mDateEditText.text = null
//                mTimeEditText.text = null
//            }
//        }
//
//        mTitleEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//            }
//
//            override fun afterTextChanged(s: Editable) {
//                if (mTitleEditText.length() != 0) {
//                    taskTitleLayout.error = null
//                }
//            }
//        })
//
//        mDateEditText.setOnClickListener {
//            hideKeyboard(mTitleEditText)
//            mDateEditText.text = null
//            showDatePickerDialog()
//        }
//
//        mTimeEditText.setOnClickListener {
//            hideKeyboard(mTitleEditText)
//            mTimeEditText.text = null
//            showTimePickerDialog()
//        }

        //taskDescription.movementMethod = null
        taskDescription.setOnClickListener {
            hideKeyboard(mTitleEditText)
            startActivityForResult(Intent(this, TaskDescriptionActivity::class.java).putExtra("note",
                    taskDescription.text.toString()), 1)
        }
        container.setOnClickListener { hideKeyboard(mTitleEditText) }
        taskReminder.setOnClickListener { showDatePickerDialog() }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerStyle = when (dressCodeStyleId) {
            R.style.AppTheme_Light -> R.style.DatePicker_Light
            else -> R.style.DatePicker_Dark
        }
        DatePickerDialog(this, datePickerStyle, this, year, month, day).apply {
            window?.attributes?.windowAnimations = R.style.DialogAnimation
            show()
            window?.setLayout(resources.getDimensionPixelSize(R.dimen.dialog_width), ViewGroup.LayoutParams.WRAP_CONTENT)
            getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this@BaseTaskActivity, R.color.blue))
            getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this@BaseTaskActivity, R.color.blue))
            getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT)
            getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun showTimePickerDialog() {
        val timeFormatKey = PreferenceHelper.getInstance().getInt(PreferenceHelper.TIME_FORMAT_KEY)
        lateinit var timePickerDialog: TimePickerDialog

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE) + 1

        when (timeFormatKey) {
            0 -> timePickerDialog = TimePickerDialog(this, this, hour, minute, true)
            1 -> timePickerDialog = TimePickerDialog(this, this, hour, minute, false)
        }
        timePickerDialog.apply {
            window?.attributes?.windowAnimations = R.style.DialogAnimation
            show()
            window?.setLayout(resources.getDimensionPixelSize(R.dimen.dialog_picker_width), ViewGroup.LayoutParams.WRAP_CONTENT)
            getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this@BaseTaskActivity, R.color.blue))
            getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this@BaseTaskActivity, R.color.blue))
            getButton(DatePickerDialog.BUTTON_POSITIVE).setBackgroundColor(Color.TRANSPARENT)
            getButton(DatePickerDialog.BUTTON_NEGATIVE).setBackgroundColor(Color.TRANSPARENT)
        }
    }

    abstract fun createViewModel(): BaseViewModel

    override fun onStop() {
        super.onStop()
        hideKeyboard(mTitleEditText)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                hideKeyboard(mTitleEditText)
                onBackPressed()
                true
            } else false

    override fun onDateSet(datePicker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        mCalendar.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, monthOfYear)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        showTimePickerDialog()
        //mDateEditText.setText(DateAndTimeFormatter.getDate(mCalendar.timeInMillis))
    }

    override fun onTimeSet(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
        mCalendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        //mTimeEditText.setText(DateAndTimeFormatter.getTime(mCalendar.timeInMillis))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            taskDescription.text = data?.getStringExtra("note")
        }
    }

    private companion object {
        private const val RU = "русский"
    }
}