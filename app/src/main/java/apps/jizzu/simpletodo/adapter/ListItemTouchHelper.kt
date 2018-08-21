package apps.jizzu.simpletodo.adapter

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

import apps.jizzu.simpletodo.activity.MainActivity

/**
 * Enables basic drag & drop and swipe-to-dismiss. Drag events are automatically started by an item long-press.
 */
open class ListItemTouchHelper protected constructor(private val mAdapter: RecyclerViewAdapter, private val mRecyclerView: RecyclerView) : ItemTouchHelper.Callback() {

    /**
     * Allows you to set motion flags for each item in the RecyclerView.
     * Should return a composite flag which defines the enabled move directions in each state (idle, swiping, dragging).
     */
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (MainActivity.mSearchViewIsOpen) return 0
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN // Flags for up and down movement
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END // Flags for left and right movement
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }

    /**
     * Called when ItemTouchHelper wants to move the dragged item from its old position to the new position.
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        mAdapter.moveTask(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) =
        mAdapter.removeTask(viewHolder.adapterPosition, mRecyclerView)

    /**
     * Enable the ability to move items.
     * Returns whether ItemTouchHelper should start a drag and drop operation if an item is long pressed.
     */
    override fun isLongPressDragEnabled() = true

    /**
     * Enable the ability to swipe items.
     * Returns whether ItemTouchHelper should start a swipe operation if a pointer is swiped over the View.
     */
    override fun isItemViewSwipeEnabled() = true
}
