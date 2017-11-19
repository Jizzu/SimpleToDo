package apps.jizzu.simpletodo.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.List;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.adapter.ListItemTouchHelper;
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter;
import apps.jizzu.simpletodo.adapter.RecyclerViewEmptySupport;
import apps.jizzu.simpletodo.database.DBHelper;
import apps.jizzu.simpletodo.model.ModelTask;


public class MainActivity extends AppCompatActivity {

    private RecyclerViewEmptySupport mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RelativeLayout mEmptyView;

    // TODO: Find better way to get the MainActivity context.
    public static Context mContext;

    public DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }

        mEmptyView = (RelativeLayout) findViewById(R.id.empty);

        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.tasksList);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setEmptyView(mEmptyView);

        ItemTouchHelper.Callback callback = new ListItemTouchHelper(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        dbHelper = new DBHelper(this);
        addTasksFromDB();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
     * Reads all tasks from the database and adds them to the RecyclerView list.
     */
    public void addTasksFromDB() {
        List<ModelTask> taskList = dbHelper.getAllTasks();

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

        long id = dbHelper.saveTask(task);
        task.setId(id);
        mAdapter.addItem(task);
    }
}
