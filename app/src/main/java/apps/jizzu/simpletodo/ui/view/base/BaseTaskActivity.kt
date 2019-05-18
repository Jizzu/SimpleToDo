package apps.jizzu.simpletodo.ui.view.base

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.task.TaskNoteActivity
import apps.jizzu.simpletodo.utils.DateAndTimeFormatter
import apps.jizzu.simpletodo.utils.PreferenceHelper
import apps.jizzu.simpletodo.utils.gone
import apps.jizzu.simpletodo.utils.visible
import apps.jizzu.simpletodo.vm.base.BaseViewModel
import daio.io.dresscode.dressCodeStyleId
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_task_details.*
import kotterknife.bindView
import java.util.*

abstract class BaseTaskActivity : BaseActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    val mTitleEditText: EditText by bindView(R.id.tvTaskTitle)
    lateinit var mCalendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        setContentView(R.layout.activity_task_details)
        mCalendar = Calendar.getInstance()
        initListeners()
    }

    private fun initListeners() {
        mTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                if (mTitleEditText.length() != 0) {
                    tilTaskTitle.error = null
                }
            }
        })

        tvTaskNote.setOnClickListener {
            hideKeyboard(mTitleEditText)
            startActivityForResult(Intent(this, TaskNoteActivity::class.java).putExtra("note",
                    tvTaskNote.text.toString()), 1)
        }
        tvTaskReminder.setOnClickListener {
            hideKeyboard(mTitleEditText)
            showDatePickerDialog()
        }
        ivDeleteTaskReminder.setOnClickListener {
            tvTaskReminder.text = null
            ivDeleteTaskReminder.gone()
        }
        initScrollViewListener(svTaskDetails)
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
    }

    override fun onTimeSet(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
        mCalendar.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        tvTaskReminder.text = getString(R.string.date_format_at, DateAndTimeFormatter.getDate(mCalendar.timeInMillis),
                DateAndTimeFormatter.getTime(mCalendar.timeInMillis))
        ivDeleteTaskReminder.visible()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            tvTaskNote.text = data?.getStringExtra("note")
        }
    }
}