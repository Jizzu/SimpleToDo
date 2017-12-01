package apps.jizzu.simpletodo.adapter;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import apps.jizzu.simpletodo.activity.MainActivity;
import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.database.TasksOrderUpdate;
import apps.jizzu.simpletodo.utils.Utils;
import apps.jizzu.simpletodo.database.DBHelper;
import apps.jizzu.simpletodo.model.ModelTask;

import static android.content.ContentValues.TAG;

/**
 * Adapters connect the list views (RecyclerView for example) to it's contents.
 */
public class RecyclerViewAdapter extends RecyclerViewEmptySupport.Adapter<RecyclerView.ViewHolder> {

    public List<ModelTask> mItems = new ArrayList<>();
    private DBHelper mHelper = DBHelper.getInstance(MainActivity.mContext);

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
     * Removes an item from the list.
     */
    public void removeItem(int position, RecyclerView recyclerView) {
        final long taskID = mItems.get(position).getId();
        final boolean[] isRemoved = {true};

        mItems.remove(position);
        notifyItemRemoved(position);

        Snackbar snackbar = Snackbar.make(recyclerView, "Task is removed!", Snackbar.LENGTH_LONG);
        snackbar.setAction("Cancel", new CustomOnClickListener(taskID) {

            public void onClick(View view) {
                ModelTask task = mHelper.getTask(taskID);
                addItem(task, task.getPosition());

                isRemoved[0] = false;
            }
        });

        snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            // Called when Snackbar appears on the screen.
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            // Called when Snackbar disappears from the screen.
            @Override
            public void onViewDetachedFromWindow(View view) {
                if (isRemoved[0]) {
                    mHelper.deleteTask(taskID);
                    saveTasksOrderFromDB();
                }
            }
        });
        snackbar.show();
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

        return new TaskViewHolder(v, title, date);
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * holder: The ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * position: The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ModelTask task = mItems.get(position);

        holder.itemView.setEnabled(true);
        TaskViewHolder taskViewHolder = (TaskViewHolder) holder;

        taskViewHolder.title.setText(task.getTitle());

        if (task.getDate() != 0) {
            taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
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
