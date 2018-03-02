package apps.jizzu.simpletodo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import apps.jizzu.simpletodo.activity.EditTaskActivity;
import apps.jizzu.simpletodo.activity.MainActivity;
import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.alarm.AlarmHelper;
import apps.jizzu.simpletodo.database.TasksOrderUpdate;
import apps.jizzu.simpletodo.utils.Utils;
import apps.jizzu.simpletodo.database.DBHelper;
import apps.jizzu.simpletodo.model.ModelTask;

import static android.content.ContentValues.TAG;

/**
 * Adapters connect the list views (RecyclerView for example) to it's contents (uses the Singleton pattern).
 */
public class RecyclerViewAdapter extends RecyclerViewEmptySupport.Adapter<RecyclerView.ViewHolder> {

    public static List<ModelTask> mItems = new ArrayList<>();
    private DBHelper mHelper = DBHelper.getInstance(MainActivity.mContext);
    private AlarmHelper mAlarmHelper = AlarmHelper.getInstance();
    private Context mContext;
    private static RecyclerViewAdapter mInstance;
    private boolean mCancelButtonIsClicked;

    /**
     * Constructor is private to prevent direct instantiation.
     */
    private RecyclerViewAdapter() {

    }

    /**
     * This static method ensures that only one RecyclerViewAdapter will ever exist at any given time.
     */
    public static RecyclerViewAdapter getInstance() {
        if (mInstance == null) {
            mInstance = new RecyclerViewAdapter();
        }
        return mInstance;
    }

    /**
     * Custom OnClickListener which is needed to pass task id for the Snackbar onClick() method.
     */
    class CustomOnClickListener implements View.OnClickListener {
        long taskID;

        public CustomOnClickListener(long taskID) {
            this.taskID = taskID;
        }

        @Override
        public void onClick(View view) {

        }
    }

    /**
     * Adds a new item to the end of the list.
     */
    public void addItem(ModelTask item) {
        mItems.add(item);
        notifyItemInserted(getItemCount() - 1);

        Log.d(TAG, "Task with title " + item.getTitle() + " and position = " + item.getPosition() + " added from db to RecyclerView!");
    }

    /**
     * Adds a new item to the specific position of the list.
     */
    public void addItem(ModelTask item, int position) {
        mItems.add(position, item);
        notifyItemInserted(position);

        Log.d(TAG, "Task with title " + mItems.get(position).getTitle() + " and position = " + position + " added from db to RecyclerView!");
    }

    /**
     * Updates the data of the specific item in the list.
     */
    public void updateItem(ModelTask updatedTask, int position) {
        mItems.set(position, updatedTask);
        notifyItemChanged(position);

        Log.d(TAG, "Task with title " + mItems.get(position).getTitle() + " and position = " + position + " updated in RecyclerView!");
    }

    /**
     * Removes an item from the list (with Snackbar).
     */
    public void removeItem(int position, RecyclerView recyclerView) {
        final long taskID = mItems.get(position).getId();
        final boolean[] isRemoved = {true};
        final long timeStamp = mItems.get(position).getTimeStamp();
        mCancelButtonIsClicked = false;

        mItems.remove(position);
        notifyItemRemoved(position);

        Snackbar snackbar = Snackbar.make(recyclerView, R.string.snackbar_remove_task, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.action_cancel, new CustomOnClickListener(taskID) {

            public void onClick(View view) {
                if (!mCancelButtonIsClicked) {
                    mCancelButtonIsClicked = true;
                    ModelTask task = mHelper.getTask(taskID);
                    addItem(task, task.getPosition());

                    isRemoved[0] = false;
                }
            }
        });

        snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            // Called when Snackbar appears on the screen.
            @Override
            public void onViewAttachedToWindow(View view) {
                MainActivity.mFab.show();
            }

            // Called when Snackbar disappears from the screen.
            @Override
            public void onViewDetachedFromWindow(View view) {
                if (isRemoved[0]) {
                    // Removes a notification
                    mAlarmHelper.removeNotification(timeStamp, MainActivity.mContext);

                    // Removes a task
                    mHelper.deleteTask(taskID);
                    saveTasksOrderFromDB();
                }
            }
        });
        snackbar.show();
    }

    /**
     * Removes an item from the list (without Snackbar).
     */
    public void removeItem(int position) {
        Log.d(TAG, "RV ADAPTER: task position = " + mItems.get(position).getPosition());
        long taskID = mItems.get(position).getId();
        long timeStamp = mItems.get(position).getTimeStamp();

        mItems.remove(position);
        notifyItemRemoved(position);

        // Removes a notification
        mAlarmHelper.removeNotification(timeStamp, MainActivity.mContext);

        // Removes a task
        mHelper.deleteTask(taskID);
        saveTasksOrderFromDB();
    }

    /**
     * Removes all items from the list.
     */
    public void removeAllItems() {
        if (getItemCount() != 0) {
            mItems = new ArrayList<>();
            notifyDataSetChanged();

            Log.d(TAG, "All items is removed!");
        }
    }

    /**
     * Moves an item in the list.
     */
    public void moveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "fromPosition: " + fromPosition + " toPosition: " + toPosition);

        if (fromPosition < toPosition) {
            // Move down
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mItems, i, i + 1);
                mItems.get(i).setPosition(i);
                mItems.get(i + 1).setPosition(i + 1);

                Log.d(TAG, "Task with title " + mItems.get(i).getTitle() + " new position = " + mItems.get(i).getPosition());
                Log.d(TAG, "Task with title " + mItems.get(i + 1).getTitle() + " new position = " + mItems.get(i + 1).getPosition());
            }
        } else {
            // Move up
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mItems, i, i - 1);
                mItems.get(i).setPosition(i);
                mItems.get(i - 1).setPosition(i - 1);

                Log.d(TAG, "Task with title " + mItems.get(i).getTitle() + " new position = " + mItems.get(i).getPosition());
                Log.d(TAG, "Task with title " + mItems.get(i - 1).getTitle() + " new position = " + mItems.get(i - 1).getPosition());
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        saveTasksOrderFromRV();
    }

    /**
     * Saves the new tasks order from RecyclerView list to the database.
     */
    public void saveTasksOrderFromRV() {
        for (ModelTask task : mItems) {
            task.setPosition(mItems.indexOf(task));

            TasksOrderUpdate order = new TasksOrderUpdate();
            order.execute(task);
        }
    }

    /**
     * Saves the new tasks order to the database.
     */
    public void saveTasksOrderFromDB() {
        List<ModelTask> taskList = mHelper.getAllTasks();

        for (ModelTask task : taskList) {
            task.setPosition(taskList.indexOf(task));

            TasksOrderUpdate order = new TasksOrderUpdate();
            order.execute(task);
        }
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item
     * parent: The ViewGroup into which the new View will be added after it is bound to an adapter position
     * viewType: The view type of the new View.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_task, parent, false);
        TextView title = v.findViewById(R.id.tvTaskTitle);
        TextView date = v.findViewById(R.id.tvTaskDate);

        mContext = parent.getContext();

        return new TaskViewHolder(v, title, date);
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * holder: The ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * position: The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ModelTask task = mItems.get(position);
        final int currentTaskPosition = position;

        TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
        View itemView = taskViewHolder.itemView;
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, EditTaskActivity.class);

                intent.putExtra("id", task.getId());
                intent.putExtra("title", task.getTitle());
                intent.putExtra("position", currentTaskPosition);
                intent.putExtra("time_stamp", task.getTimeStamp());

                if (task.getDate() != 0) {
                    intent.putExtra("date", task.getDate());
                }
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setEnabled(true);

        taskViewHolder.title.setText(task.getTitle());

        if (task.getDate() != 0) {
            Log.d(TAG, "TASK WITH DATE");
            taskViewHolder.title.setPadding(0, 0, 0, 0);
            taskViewHolder.title.setGravity(Gravity.CENTER_VERTICAL);
            taskViewHolder.date.setVisibility(View.VISIBLE);
            if (DateUtils.isToday(task.getDate())) {
                taskViewHolder.date.setText(mContext.getString(R.string.reminder_today) + " " + Utils.getTime(task.getDate()));
            } else if (DateUtils.isToday(task.getDate() + DateUtils.DAY_IN_MILLIS)) {
                taskViewHolder.date.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                taskViewHolder.date.setText(mContext.getString(R.string.reminder_yesterday) + " " + Utils.getTime(task.getDate()));
            } else if (DateUtils.isToday(task.getDate() - DateUtils.DAY_IN_MILLIS)) {
                taskViewHolder.date.setText(mContext.getString(R.string.reminder_tomorrow) + " " + Utils.getTime(task.getDate()));
            } else if (task.getDate() < Calendar.getInstance().getTimeInMillis()) {
                taskViewHolder.date.setTextColor(ContextCompat.getColor(mContext, R.color.red));
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            } else {
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            }
        } else {
            Log.d(TAG, "TASK WITHOUT DATE");

            // Get the resolution of the user's screen
            Display d = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int width = d.getWidth();
            int height = d.getHeight();
            Log.d(TAG, "width = " + width + ", height = " + height);

            taskViewHolder.date.setVisibility(View.GONE);
            if (width >= 1080 || height >= 1776) {
                taskViewHolder.title.setPadding(0, 27, 0, 27);
            } else if (width >= 720 || height >= 1184) {
                taskViewHolder.title.setPadding(0, 20, 0, 20);
            } else if (width >= 480 || height >= 800) {
                taskViewHolder.title.setPadding(0, 15, 0, 15);
            }
            taskViewHolder.title.setGravity(Gravity.CENTER_VERTICAL);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * This class helps to get a reference to each element of a particular list item.
     */
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;

        public TaskViewHolder(View itemView, TextView title, TextView date) {
            super(itemView);

            this.title = title;
            this.date = date;
        }
    }
}
