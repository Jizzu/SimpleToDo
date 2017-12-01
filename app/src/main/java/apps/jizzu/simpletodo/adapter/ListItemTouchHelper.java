package apps.jizzu.simpletodo.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Enables basic drag & drop and swipe-to-dismiss. Drag events are automatically started by an item long-press.
 */
public class ListItemTouchHelper extends ItemTouchHelper.Callback {

    private final RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Constructor for mAdapter initialization.
     */
    public ListItemTouchHelper(RecyclerViewAdapter adapter, RecyclerView recyclerView) {
        mAdapter = adapter;
        mRecyclerView = recyclerView;
    }

    /**
     * Allows you to set motion flags for each item in the RecyclerView.
     * Should return a composite flag which defines the enabled move directions in each state (idle, swiping, dragging).
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN; // Flags for up and down movement
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END; // Flags for left and right movement
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to the new position.
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        mAdapter.moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.removeItem(viewHolder.getAdapterPosition(), mRecyclerView);
    }

    /**
     * Enable the ability to move items.
     * Returns whether ItemTouchHelper should start a drag and drop operation if an item is long pressed.
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * Enable the ability to swipe items.
     * Returns whether ItemTouchHelper should start a swipe operation if a pointer is swiped over the View.
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
}
