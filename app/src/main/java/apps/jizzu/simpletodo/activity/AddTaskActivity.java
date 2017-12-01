package apps.jizzu.simpletodo.activity;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import java.util.Calendar;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.utils.Utils;
import apps.jizzu.simpletodo.fragment.DatePickerFragment;
import apps.jizzu.simpletodo.fragment.TimePickerFragment;

/**
 * Activity for adding a new task to RecyclerView.
 */
public class AddTaskActivity extends AppCompatActivity {

    EditText mTitle;
    TextInputLayout mTaskTitleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        setTitle("NEW TASK");

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }

        RelativeLayout relativeLayout = findViewById(R.id.container);
        mTaskTitleLayout = findViewById(R.id.taskTitleLayout);
        mTitle = findViewById(R.id.taskTitle);
        final EditText date = findViewById(R.id.taskDate);
        final EditText time = findViewById(R.id.taskTime);
        Button addTaskButton = findViewById(R.id.addTaskButton);

        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(mTitle);
            }
        });

        final Calendar calendar = Calendar.getInstance();
        // If the user specified only the date (without time), then the notification of the event will appear in an hour.
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);


        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment datePickerFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        date.setText(Utils.getDate(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        date.setText(null);
                    }
                };
                datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment timePickerFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        time.setText(Utils.getTime(calendar.getTimeInMillis()));
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        time.setText(null);
                    }
                };
                timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTitle.length() == 0) {
                    mTitle.setError("Please, input some text!");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("title", mTitle.getText().toString());

                    if (date.length() != 0 || time.length() != 0) {
                        intent.putExtra("date", calendar.getTimeInMillis());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /**
     * Hides the soft keyboard when the user clicks on the home button.
     */
    @Override
    protected void onStop() {
        super.onStop();
        hideKeyboard(mTitle);
    }

    /**
     * Method for hiding the soft keyboard.
     */
    public void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * The handler for clicking the close button in the toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideKeyboard(mTitle);
            onBackPressed();
            return true;
        }
        return false;
    }
}
