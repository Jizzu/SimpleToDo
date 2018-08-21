package apps.jizzu.simpletodo.widget

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter
import apps.jizzu.simpletodo.model.ModelTask
import apps.jizzu.simpletodo.utils.Utils
import java.util.*

/**
 * Class that will fill the list with values.
 * It's methods are very similar to the standard adapter methods.
 */
class ViewFactory internal constructor(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var mListData: ArrayList<ModelTask>

    /**
     * Called when factory is first constructed.
     */
    override fun onCreate() {
        mListData = ArrayList()
    }

    /**
     * Called when notifyDataSetChanged() is triggered on the remote adapter.
     */
    override fun onDataSetChanged() {
        mListData.clear()
        mListData.addAll(RecyclerViewAdapter.mTaskList)
    }

    /**
     * Called when the last RemoteViewsAdapter that is associated with this factory is unbound.
     */
    override fun onDestroy() {

    }

    override fun getCount() = mListData.size

    /**
     * Get a View that displays the data at the specified position in the data set.
     */
    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(mContext.packageName, R.layout.widget_item)
        remoteViews.setTextViewText(R.id.item_title, mListData[position].title)

        if (mListData[position].date != 0L) {
            remoteViews.setViewPadding(R.id.item_title, 0, 0, 0, 0)
            remoteViews.setViewVisibility(R.id.item_date, View.VISIBLE)

            when {
                DateUtils.isToday(mListData[position].date) -> {
                    remoteViews.setTextColor(R.id.item_date, ContextCompat.getColor(mContext, R.color.colorPrimary))
                    remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_today) + " " + Utils.getTime(mListData[position].date))
                }
                DateUtils.isToday(mListData[position].date + DateUtils.DAY_IN_MILLIS) -> {
                    remoteViews.setTextColor(R.id.item_date, ContextCompat.getColor(mContext, R.color.red))
                    remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_yesterday) + " " + Utils.getTime(mListData[position].date))
                }
                DateUtils.isToday(mListData[position].date - DateUtils.DAY_IN_MILLIS) -> remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_tomorrow) + " " + Utils.getTime(mListData[position].date))
                mListData[position].date < Calendar.getInstance().timeInMillis -> {
                    remoteViews.setTextColor(R.id.item_date, ContextCompat.getColor(mContext, R.color.red))
                    remoteViews.setTextViewText(R.id.item_date, Utils.getFullDate(mListData[position].date))
                }
                else -> remoteViews.setTextViewText(R.id.item_date, Utils.getFullDate(mListData[position].date))
            }
        } else {
            val displayMetrics = DisplayMetrics()
            (mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            Log.d(TAG, "width = $width, height = $height")

            remoteViews.setViewVisibility(R.id.item_date, View.GONE)
            if (width >= 1080 || height >= 1776) {
                remoteViews.setViewPadding(R.id.item_title, 0, 27, 0, 27)
            } else if (width >= 720 || height >= 1184) {
                remoteViews.setViewPadding(R.id.item_title, 0, 20, 0, 20)
            } else if (width >= 480 || height >= 800) {
                remoteViews.setViewPadding(R.id.item_title, 0, 15, 0, 15)
            }
        }

        val fillInIntent = Intent()
        fillInIntent.putExtra(WidgetProvider.ITEM_ID, mListData[position].id)
        fillInIntent.putExtra(WidgetProvider.ITEM_TITLE, mListData[position].title)
        fillInIntent.putExtra(WidgetProvider.ITEM_POSITION, position)
        fillInIntent.putExtra(WidgetProvider.ITEM_TIME_STAMP, mListData[position].timeStamp)

        if (mListData[position].date != 0L) {
            fillInIntent.putExtra(WidgetProvider.ITEM_DATE, mListData[position].date)
        }
        remoteViews.setOnClickFillInIntent(R.id.item, fillInIntent)

        return remoteViews
    }

    /**
     * This allows for the use of a custom loading view which appears between the time that getViewAt(int) is called and returns.
     */
    override fun getLoadingView() = null

    /**
     * Returns the number of types of Views that will be created by getView().
     * This adapter always returns the same type of View for all items.
     */
    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    /**
     * Indicates whether the item ids are stable across changes to the underlying data.
     * Returns true if the same id always refers to the same object.
     */
    override fun hasStableIds() = true
}
