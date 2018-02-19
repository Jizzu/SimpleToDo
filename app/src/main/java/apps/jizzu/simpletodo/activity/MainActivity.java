package apps.jizzu.simpletodo.activity;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.adapter.ListItemTouchHelper;
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter;
import apps.jizzu.simpletodo.adapter.RecyclerViewEmptySupport;
import apps.jizzu.simpletodo.alarm.AlarmHelper;
import apps.jizzu.simpletodo.database.DBHelper;
import apps.jizzu.simpletodo.model.ModelTask;
import apps.jizzu.simpletodo.settings.SettingsActivity;
import apps.jizzu.simpletodo.utils.Interpolator;
import apps.jizzu.simpletodo.utils.MyApplication;
import apps.jizzu.simpletodo.utils.PreferenceHelper;
import apps.jizzu.simpletodo.widget.WidgetProvider;
import top.wefor.circularanim.CircularAnim;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity {

    private RecyclerViewEmptySupport mRecyclerView;
    public RecyclerViewAdapter mAdapter;
    private RelativeLayout mEmptyView;
    private DBHelper mHelper;
    public static FloatingActionButton mFab;
    private PreferenceHelper preferenceHelper;
    private RecyclerView.LayoutManager mLayoutManager;

    // TODO: Find better way to get the MainActivity context.
    public static Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        setTitle("");

        // Initialize ALARM_SERVICE
        AlarmHelper.getInstance().init(getApplicationContext());

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }

        PreferenceHelper.getInstance().init(getApplicationContext());
        preferenceHelper = PreferenceHelper.getInstance();

        mEmptyView = findViewById(R.id.empty);

        mRecyclerView = findViewById(R.id.tasksList);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = RecyclerViewAdapter.getInstance();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEmptyView(mEmptyView);
        mFab = findViewById(R.id.fab);

        ItemTouchHelper.Callback callback = new ListItemTouchHelper(mAdapter, mRecyclerView);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mHelper = DBHelper.getInstance(mContext);
        addTasksFromDB();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)) {
                    CircularAnim.fullActivity(MainActivity.this, view)
                            .colorOrImageRes(R.color.colorPrimary)
                            .duration(300)
                            .go(new CircularAnim.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);

                                    // Method startActivityForResult(Intent, int) allows to get the right data (Title for RecyclerView item for example) from another activity.
                                    // To obtain data from the activity used onActivityResult(int, int, Intent) method that is called when the AddTaskActivity completes it's work.
                                    startActivityForResult(intent, 1);
                                }
                            });
                } else {
                    Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mFab.getVisibility() == View.VISIBLE) {
                    mFab.hide();
                } else if (dy < 0 && mFab.getVisibility() != View.VISIBLE) {
                    mFab.show();
                }
            }
        });

        if (preferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)) {
            mFab.setVisibility(View.GONE);

            // Starts the RecyclerView items animation
            int resId = R.anim.layout_animation;
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this, resId);
            mRecyclerView.setLayoutAnimation(animation);
        }
    }

    /**
     * Reads all tasks from the database and adds them to the RecyclerView list.
     */
    public void addTasksFromDB() {
        mAdapter.removeAllItems();
        List<ModelTask> taskList = mHelper.getAllTasks();

        for (ModelTask task : taskList) {
            mAdapter.addItem(task, task.getPosition());
        }
    }

    /**
     * Updates widget data.
     */
    public void updateWidget() {
        Log.d(TAG, "WIDGET IS UPDATED!");
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(MainActivity.this)
                .getAppWidgetIds(new ComponentName(MainActivity.this, WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
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

        Log.d(TAG, "dbSize = " + taskList.size() + ", adapterSize = " + mAdapter.mItems.size());
        if (taskList.size() != mAdapter.mItems.size()) {

            mHelper.deleteAllTasks();

            for (ModelTask task : mAdapter.mItems) {
                mHelper.saveTask(task);
            }
        }
        updateWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFab.setVisibility(View.GONE);

        if (preferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)) {
            // Starts the FAB animation
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFab.setVisibility(View.VISIBLE);
                    final Animation myAnim = AnimationUtils.loadAnimation(mContext, R.anim.fab_animation);
                    Interpolator interpolator = new Interpolator(0.2, 20);
                    myAnim.setInterpolator(interpolator);
                    mFab.startAnimation(myAnim);
                }
            }, 300);
        }
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
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
            Toast.makeText(this, getString(R.string.toast_incorrect_time), Toast.LENGTH_SHORT).show();
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
