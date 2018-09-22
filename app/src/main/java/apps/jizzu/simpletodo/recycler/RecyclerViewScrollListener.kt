package apps.jizzu.simpletodo.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewScrollListener : RecyclerView.OnScrollListener() {
    private var scrolledDistance = 0
    private var shadowHidden = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

        if (scrolledDistance > HIDE_THRESHOLD && firstVisibleItem != 0) {
            onShow()
            shadowHidden = false
            scrolledDistance = 0
        } else if (scrolledDistance < -HIDE_THRESHOLD && firstVisibleItem == 0) {
            onHide()
            shadowHidden = true
            scrolledDistance = 0
        }

        if (shadowHidden && dy > 0 || !shadowHidden && dy < 0) {
            scrolledDistance += dy
        }
    }

    abstract fun onShow()
    abstract fun onHide()

    companion object {
        private const val HIDE_THRESHOLD = 20
    }
}