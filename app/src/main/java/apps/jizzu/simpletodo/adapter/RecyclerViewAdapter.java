package apps.jizzu.simpletodo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.Utils;
import apps.jizzu.simpletodo.model.Item;
import apps.jizzu.simpletodo.model.ModelTask;

/**
 * Adapters connect the list views (RecyclerView for example) to it's contents.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Item> mItems = new ArrayList<>();

    private static final int TYPE_TASK = 0;
    private static final int TYPE_SEPARATOR = 1;

    /**
     * Adds a new item to the end of the list.
     */
    public void addItem(Item item) {
        mItems.add(item);
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Adds a new item to specific position of the list.
     */
    public void addItem(int location, Item item) {
        mItems.add(location, item);
        notifyItemInserted(location);
    }

    /**
     * Returns the specific item depending on the position in the list.
     */
    public Item getItem(int position) {
        return mItems.get(position);
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item
     * parent: The ViewGroup into which the new View will be added after it is bound to an adapter position
     * viewType: The view type of the new View.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_TASK:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_task, parent, false);
                TextView title = v.findViewById(R.id.tvTaskTitle);
                TextView date = v.findViewById(R.id.tvTaskDate);

                return new TaskViewHolder(v, title, date);

            default:
                return null;
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position
     * holder: The ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * position: The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item = mItems.get(position);

        if (item.isTask()) {
            holder.itemView.setEnabled(true);
            ModelTask task = (ModelTask) item;
            TaskViewHolder taskViewHolder = (TaskViewHolder) holder;

            taskViewHolder.title.setText(task.getTitle());

            if (task.getDate() != 0) {
                taskViewHolder.date.setText(Utils.getFullDate(task.getDate()));
            }
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
     * Returns the type View (task or separator) depending on the position of the item.
     */
    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) {
            return TYPE_TASK;
        } else {
            return TYPE_SEPARATOR;
        }
    }

    /**
     * This class helps to get a reference to each element of a particular list item.
     */
    private class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;

        public TaskViewHolder(View itemView, TextView title, TextView date) {
            super(itemView);
            this.title = title;
            this.date = date;
        }
    }
}
