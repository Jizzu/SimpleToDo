package apps.jizzu.simpletodo.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * TimePickerDialog class.
 */
public class TimePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE) + 1;

        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)
                getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}