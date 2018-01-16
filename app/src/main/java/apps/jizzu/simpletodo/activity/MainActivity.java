package apps.jizzu.simpletodo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.adapter.ListItemTouchHelper;
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter;
import apps.jizzu.simpletodo.adapter.RecyclerViewEmptySupport;
import apps.jizzu.simpletodo.database.DBHelper;
import apps.jizzu.simpletodo.model.ModelTask;
import apps.jizzu.simpletodo.alarm.AlarmHelper;
import apps.jizzu.simpletodo.utils.MyApplication;


public class MainActivity extends AppCompatActivity {

    private RecyclerViewEmptySupport mRecyclerView;
    public static RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RelativeLayout mEmptyView;
    private DBHelper mHelper;

    // TODO: Find better way to get the MainActivity context.
    public static Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        // Initialize ALARM_SERVICE
        AlarmHelper.getInstance().init(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }

        mEmptyView = findViewById(R.id.empty);

        mRecyclerView = findViewById(R.id.tasksList);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEmptyView(mEmptyView);

        ItemTouchHelper.Callback callback = new ListItemTouchHelper(mAdapter, mRecyclerView);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mHelper = DBHelper.getInstance(mContext);
        addTasksFromDB();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);

                // Method startActivityForResult(Intent, int) allows to get the right data (Title for RecyclerView item for example) from another activity.
                // To obtain data from the activity used onActivityResult(int, int, Intent) method that is called when the AddTaskActivity completes it's work.
                startActivityForResult(intent, 1);
            }
        });
    }

    /**
     * Reads all tasks from the database and compares them with mItems in RecyclerView.
     * If the tasks count in the database doesn't coincide with the number of tasks in RecyclerView,
     * all the tasks in the database are replaced with tasks from the mItems.
     * For example, this happens when the user removes the task from the RecyclerView list and hide/close app until the snackbar has disappeared.
     */
    @Override
    protected void onStop() {
        super.onStop();

        List<ModelTask> taskList = mHelper.getAllTasks();

        if (taskList.size() != mAdapter.mItems.size()) {

            mHelper.deleteAllTasks();

            for (ModelTask task : mAdapter.mItems) {
                mHelper.saveTask(task);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    /**
     * Reads all tasks from the database and adds them to the RecyclerView list.
     */
    public void addTasksFromDB() {
        List<ModelTask> taskList = mHelper.getAllTasks();

        for (ModelTask task : taskList) {
            mAdapter.addItem(task, task.getPosition());
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode you started it with, the resultCode it returned, and any additional data from it.
     * requestCode: The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * resultCode: The integer result code returned by the child activity through its setResult().
     * data: An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;

        String taskTitle = data.getStringExtra("title");
        long taskDate = data.getLongExtra("date", 0);

        ModelTask task = new ModelTask();
        task.setTitle(taskTitle);
        task.setDate(taskDate);
        task.setPosition(mAdapter.getItemCount());

        // Set notification to the current task
        if (task.getDate() != 0 && task.getDate() <= Calendar.getInstance().getTimeInMillis()) {
            Toast.makeText(this, "Error! You have selected an incorrect time!", Toast.LENGTH_SHORT).show();
            task.setDate(0);
        } else if (task.getDate() != 0) {
            AlarmHelper alarmHelper = AlarmHelper.getInstance();
            alarmHelper.setAlarm(task);
        }

        long id = mHelper.saveTask(task);
        task.setId(id);
        mAdapter.addItem(task);
    }
}
