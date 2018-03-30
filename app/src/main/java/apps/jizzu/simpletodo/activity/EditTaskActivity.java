package apps.jizzu.simpletodo.activity;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

    private Context mContext;
    private EditText mTitle;
    private TextInputLayout mTaskTitleLayout;
    private RelativeLayout mReminderLayout;
    private Calendar mCalendar;
    private EditText mDateEditText;
    private EditText mTimeEditText;
    private SwitchCompat mReminderSwitch;
    private RecyclerViewAdapter mAdapter;
    private long mId;
    private long mDate;
    private int mPosition;
    private long mTimeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        setTitle("");
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.edit_task));

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }

        RelativeLayout relativeLayout = findViewById(R.id.container);
        mDateEditText = findViewById(R.id.taskDate);
        mTaskTitleLayout = findViewById(R.id.taskTitleLayout);
        mReminderLayout = findViewById(R.id.reminderContainer);
        mTitle = findViewById(R.id.taskTitle);
        mTimeEditText = findViewById(R.id.taskTime);
        Button addTaskButton = findViewById(R.id.addTaskButton);
        mReminderSwitch = findViewById(R.id.reminderSwitch);
        TextView reminderText = findViewById(R.id.tvSetReminder);

        MainActivity.mActivityIsShown = true;
        mContext = getApplicationContext();

        // Get the resolution of the user's screen
        Display d = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        Log.d(TAG, "width = " + width + ", height = " + height);

        if (width <= 480 || height <= 800) {
            reminderText.setText(R.string.set_reminder_short);
        }

        // Get Intent data
        Intent intent = getIntent();
        mId = intent.getLongExtra("id", 0);
        String title = intent.getStringExtra("title");
        mDate = intent.getLongExtra("date", 0);
        mPosition = intent.getIntExtra("position", 0);
        mTimeStamp = intent.getLongExtra("time_stamp", 0);

        mAdapter = RecyclerViewAdapter.getInstance();

        Log.d(TAG, "TASK DATE = " + mDate);

        mTitle.setText(title);
        mTitle.setSelection(mTitle.getText().length());
        if (mDate != 0) {
            mDateEditText.setText(Utils.getDate(mDate));
            mTimeEditText.setText(Utils.getTime(mDate));
        }
        addTaskButton.setText(getString(R.string.update_task));

        if (mDate == 0) {
            mReminderLayout.setVisibility(View.INVISIBLE);
            mReminderSwitch.setChecked(false);
            mDateEditText.setText(null);
            mTimeEditText.setText(null);
        } else {
            mReminderLayout.setVisibility(View.VISIBLE);
            mReminderSwitch.setChecked(true);
        }

        mReminderSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mReminderSwitch.isChecked()) {
                    hideKeyboard(mTitle);
                    mReminderLayout.animate().alpha(1.0f).setDuration(500).setListener(
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    mReminderLayout.setVisibility(View.VISIBLE);
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
                    mReminderLayout.animate().alpha(0.0f).setDuration(500).setListener(
                            new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    mReminderLayout.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            }
                    );
                    mDateEditText.setText(null);
                    mTimeEditText.setText(null);
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
        if (mDateEditText.length() != 0 || mTimeEditText.length() != 0) {
            mCalendar.setTimeInMillis(mDate);
        }
        // If the user specified only the date (without time), then the notification of the event will appear in an hour.
        if (mTimeEditText.length() == 0) {
            mCalendar.set(Calendar.HOUR_OF_DAY, mCalendar.get(Calendar.HOUR_OF_DAY) + 1);
        }

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDateEditText.setText(null);
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getFragmentManager(), "DatePickerFragment");
            }
        });

        mTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTimeEditText.setText(null);
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getFragmentManager(), "TimePickerFragment");
            }
        });

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTitle.length() == 0) {
                    mTitle.setError(getString(R.string.error_text_input));
                } else if (mTitle.getText().toString().trim().length() == 0) {
                    mTitle.setError(getString(R.string.error_spaces));
                } else {
                    DBHelper dbHelper = DBHelper.getInstance(mContext);

                    ModelTask task = new ModelTask(mId, mTitle.getText().toString(), mDate, mPosition, mTimeStamp);

                    if (mDateEditText.length() != 0 || mTimeEditText.length() != 0) {
                        task.setDate(mCalendar.getTimeInMillis());
                    }

                    if (!mReminderSwitch.isChecked() || (mDateEditText.length() == 0 && mTimeEditText.length() == 0)) {
                        task.setDate(0);
                    }
                    Log.d(TAG, "Title = " + task.getTitle() + ", date = " + task.getDate() + ", position = " + task.getPosition());

                    dbHelper.updateTask(task);
                    mAdapter.updateItem(task, task.getPosition());

                    if (task.getDate() != 0 && task.getDate() <= Calendar.getInstance().getTimeInMillis()) {
                        task.setDate(0);
                        Toast.makeText(mContext, getString(R.string.toast_incorrect_time), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);
        return true;
    }

    /**
     * The handler for clicking the close button in the toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                hideKeyboard(mTitle);
                onBackPressed();
                break;

            case R.id.action_delete:
                AlertDialog.Builder alertDialog;
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    alertDialog = new AlertDialog.Builder(this);
                } else {
                    alertDialog = new AlertDialog.Builder(this, R.style.DialogTheme);
                }
                alertDialog.setTitle(R.string.dialog_title);
                alertDialog.setMessage(R.string.dialog_message);
                alertDialog.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideKeyboard(mTitle);
                        ModelTask task = new ModelTask(mId, mTitle.getText().toString(), mDate, mPosition, mTimeStamp);
                        Log.d(TAG, "EDIT TASK ACTIVITY: task position = " + (task.getPosition()));
                        mAdapter.removeItem(task.getPosition());
                        if (mAdapter.getItemCount() == 0 && MainActivity.mSearchViewIsOpen) {
                            MainActivity.mShowAnimation = true;
                        }
                        finish();
                    }
                });
                alertDialog.setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets the date selected in the DatePickerFragment.
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        mDateEditText.setText(Utils.getDate(mCalendar.getTimeInMillis()));
    }

    /**
     * Sets the time selected in the TimePickerFragment.
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
        mTimeEditText.setText(Utils.getTime(mCalendar.getTimeInMillis()));
    }
}
