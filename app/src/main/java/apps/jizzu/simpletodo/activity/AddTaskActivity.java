package apps.jizzu.simpletodo.activity;

import android.animation.Animator;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
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

import java.util.Calendar;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.fragment.DatePickerFragment;
import apps.jizzu.simpletodo.fragment.TimePickerFragment;
import apps.jizzu.simpletodo.utils.Utils;

import static android.content.ContentValues.TAG;

/**
 * Activity for adding a new task to RecyclerView.
 */
public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText mTitle;
    private TextInputLayout mTaskTitleLayout;
    private RelativeLayout mReminderLayout;
    private Calendar mCalendar;
    private EditText mDate;
    private EditText mTime;
    private SwitchCompat mReminderSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        setTitle("");
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.create_task));

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
        mReminderLayout = findViewById(R.id.reminderContainer);
        mTitle = findViewById(R.id.taskTitle);
        mTime = findViewById(R.id.taskTime);
        Button addTaskButton = findViewById(R.id.addTaskButton);
        mReminderSwitch = findViewById(R.id.reminderSwitch);
        TextView reminderText = findViewById(R.id.tvSetReminder);

        MainActivity.mActivityIsShown = true;

        // Get the resolution of the user's screen
        Display d = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = d.getWidth();
        int height = d.getHeight();
        Log.d(TAG, "width = " + width + ", height = " + height);

        if (width <= 480 || height <= 800) {
            reminderText.setText(R.string.set_reminder_short);
        }

        mReminderLayout.setVisibility(View.INVISIBLE);

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
                    mTitle.setError(getString(R.string.error_text_input));
                } else if (mTitle.getText().toString().trim().length() == 0) {
                    mTitle.setError(getString(R.string.error_spaces));
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("title", mTitle.getText().toString());

                    if (mDate.length() != 0 || mTime.length() != 0) {
                        intent.putExtra("date", mCalendar.getTimeInMillis());
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    /**
     * Method for hiding the soft keyboard.
     */
    private void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
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
