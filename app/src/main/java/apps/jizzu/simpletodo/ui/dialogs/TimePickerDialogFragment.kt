package apps.jizzu.simpletodo.ui.dialogs

import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.os.Bundle
import apps.jizzu.simpletodo.utils.PreferenceHelper
import java.util.*

class TimePickerDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val preferenceHelper = PreferenceHelper.getInstance()
        val timeFormatKey = preferenceHelper.getInt(PreferenceHelper.TIME_FORMAT_KEY)
        lateinit var timePickerDialog: TimePickerDialog

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE) + 1

        when(timeFormatKey) {
            0 -> timePickerDialog = TimePickerDialog(activity, activity as TimePickerDialog.OnTimeSetListener, hour, minute, true)
            1 -> timePickerDialog = TimePickerDialog(activity, activity as TimePickerDialog.OnTimeSetListener, hour, minute, false)
        }
        return timePickerDialog
    }
}