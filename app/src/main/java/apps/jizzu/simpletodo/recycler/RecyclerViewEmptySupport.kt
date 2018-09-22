package apps.jizzu.simpletodo.recycler

import android.content.ContentValues.TAG
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.activity.MainActivity

/**
 * Adds the setEmptyView method for the RecyclerView.
 */
class RecyclerViewEmptySupport : androidx.recyclerview.widget.RecyclerView {
    private var mEmptyView: View? = null

    private val observer: androidx.recyclerview.widget.RecyclerView.AdapterDataObserver = object : androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    fun checkIfEmpty() {
        if (mEmptyView != null && adapter != null && !isAnimationCanceled && !MainActivity.mSearchViewIsOpen) {
            val emptyViewVisible = adapter!!.itemCount == 0
            mEmptyView!!.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            visibility = if (emptyViewVisible) View.GONE else View.VISIBLE

            Log.d(TAG, "checkIfEmpty")

            if (emptyViewVisible) {
                val anim = AnimationUtils.loadAnimation(context, R.anim.empty_view_animation)
                anim.startOffset = 300
                anim.duration = 300
                mEmptyView!!.startAnimation(anim)
                Log.d(TAG, "checkIfEmpty: Start animation")
            }
        }
    }

    override fun setAdapter(adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)

        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)
        checkIfEmpty()
    }

    fun setEmptyView(emptyView: View?) {
        mEmptyView = emptyView
        checkIfEmpty()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    abstract class EmptyAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>()

    companion object {
        var isAnimationCanceled: Boolean = false
    }
}