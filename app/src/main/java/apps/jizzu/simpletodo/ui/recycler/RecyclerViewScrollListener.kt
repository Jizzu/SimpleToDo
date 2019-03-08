package apps.jizzu.simpletodo.ui.recycler

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewScrollListener : RecyclerView.OnScrollListener() {
    private var scrolledDistance = 0
    private var isToolbarShown = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        val firstCompletelyVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

        // Change toolbar's shadow visibility
        if (dy > 0 && !isShadowShown) {
            onShadowShow()
            isShadowShown = true
        } else if (dy < 0 && isShadowShown && firstCompletelyVisibleItem == 0) {
            onShadowHide()
            isShadowShown = false
        }

        // Change toolbar visibility
        if (firstVisibleItem == 0 && !isToolbarShown) {
            onToolbarShow()
            isToolbarShown = true
        } else if (scrolledDistance > HIDE_THRESHOLD && isToolbarShown) {
            onToolbarHide()
            isToolbarShown = false
            scrolledDistance = 0
        } else if (scrolledDistance < -HIDE_THRESHOLD && !isToolbarShown && firstCompletelyVisibleItem == 0) {
            onToolbarShow()
            isToolbarShown = true
            scrolledDistance = 0
        }

        if (isToolbarShown && dy > 0 || !isToolbarShown && dy < 0) {
            scrolledDistance += dy
        }
    }

    abstract fun onToolbarShow()
    abstract fun onToolbarHide()
    abstract fun onShadowShow()
    abstract fun onShadowHide()

    companion object {
        private const val HIDE_THRESHOLD = 20
        var isShadowShown = false
    }
}
