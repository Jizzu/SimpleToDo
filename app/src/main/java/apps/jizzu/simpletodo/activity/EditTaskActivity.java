package apps.jizzu.simpletodo.activity;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter;
import apps.jizzu.simpletodo.alarm.AlarmHelper;
import apps.jizzu.simpletodo.database.DBHelper;
import apps.jizzu.simpletodo.fragment.DatePickerFragment;
import apps.jizzu.simpletodo.fragment.TimePickerFragment;
import apps.jizzu.simpletodo.model.ModelTask;
import apps.jizzu.simpletodo.utils.Utils;

import static android.content.ContentValues.TAG;

/**
 * Activity for editing a chosen task in the RecyclerView.
 */
public class EditTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    EditText mTitle;
    TextInputLayout mTaskTitleLayout;
    RelativeLayout reminderLayout;
    Calendar mCalendar;
    EditText mDate;
    EditText mTime;
    SwitchCompat mReminderSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        setTitle("EDIT TASK");

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }

        RelativeLayout relativeLayout = findViewById(R.id.container);
        mDate = findViewById(R.id.taskDate);
        mTaskTitleLayout = findViewById(R.id.taskTitleLayout);
        reminderLayout = findViewById(R.id.reminderContainer);
        mTitle = findViewById(R.id.taskTitle);
        mTime = findViewById(R.id.taskTime);
        Button addTaskButton = findViewById(R.id.addTaskButton);
        mReminderSwitch = findViewById(R.id.reminderSwitch);

        // Get Intent data
        Intent intent = getIntent();
        final long id = intent.getLongExtra("id", 0);
        String title = intent.getStringExtra("title");
        final long date = intent.getLongExtra("date", 0);
        final int position = intent.getIntExtra("position", 0);
        final long timeStamp = intent.getLongExtra("time_stamp", 0);

        mTitle.setText(title);
        mTitle.setSelection(mTitle.getText().length());
        mDate.setText(Utils.getDate(date));
        mTime.setText(Utils.getTime(date));
        addTaskButton.setText("UPDATE TASK");

        if (date == 0) {
            reminderLayout.setVisibility(View.INVISIBLE);
            mReminderSwitch.setChecked(false);
            mDate.setText(null);
            mTime.setText(null);
        } else {
            reminderLayout.setVisibility(View.VISIBLE);
            mReminderSwitch.setChecked(true);
        }
        mReminderSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReminderSwitch.isChecked()) {
                    hideKeyboard(mTitle);
                    reminderLayout.animate().alpha(1.0f).setDuration(500).setListener(
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    reminderLayout.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            }
                    );
                } else {
                    reminderLayout.animate().alpha(0.0f).setDuration(500).setListener(
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    reminderLayout.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }
                    );
                    mDate.setText(null);
                    mTime.setText(null);
                }
            }
        });

        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(mTitle);
            }
        });


        mCalendar = Calendar.getInstance();
        // If the user specified only the date (without time), then the notification of the event will appear in an hour.
        mCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1);


        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDate.setText(null);
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
            }
        });

        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTime.setText(null);
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTitle.length() == 0) {
                    mTitle.setError("Please, input some text!");
                } else if (mTitle.getText().toString().trim().length() == 0) {
                    mTitle.setError("Error! The entered text consists only of spaces!");
                } else {
                    DBHelper dbHelper = DBHelper.getInstance(MainActivity.mContext);
                    RecyclerViewAdapter adapter = RecyclerViewAdapter.getInstance();

                    final ModelTask task = new ModelTask(id, mTitle.getText().toString(), date, position, timeStamp);

                    if (mDate.length() != 0 || mTime.length() != 0) {
                        task.setDate(mCalendar.getTimeInMillis());
                    }

                    if (!mReminderSwitch.isChecked() || (mDate.length() == 0 && mTime.length() == 0)) {
                        task.setDate(0);
                    }
                    Log.d(TAG, "Title = " + task.getTitle() + ", date = " + task.getDate() + ", position = " + task.getPosition());

                    if (!MainActivity.mIsSearchOpen) {
                        dbHelper.updateTask(task);
                        adapter.updateItem(task, task.getPosition());
                    } else {
                        dbHelper.updateTask(task);
                        MainActivity.mSearchView.closeSearch();
                    }

                    if (task.getDate() != 0 && task.getDate() <= Calendar.getInstance().getTimeInMillis()) {
                        Toast.makeText(MainActivity.mContext, "Error! You have selected an incorrect time!", Toast.LENGTH_SHORT).show();
                        task.setDate(0);
                    } else if (task.getDate() != 0) {
                        AlarmHelper alarmHelper = AlarmHelper.getInstance();
                        alarmHelper.setAlarm(task);
                    } else if (task.getDate() == 0) {
                        AlarmHelper mAlarmHelper = AlarmHelper.getInstance();
                        mAlarmHelper.removeAlarm(task.getTimeStamp());
                    }
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

    /**
     * Sets the date selected in the DatePickerFragment.
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mDate.setText(Utils.getDate(mCalendar.getTimeInMillis()));
    }

    /**
     * Sets the time selected in the TimePickerFragment.
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
        mTime.setText(Utils.getTime(mCalendar.getTimeInMillis()));
    }
}
