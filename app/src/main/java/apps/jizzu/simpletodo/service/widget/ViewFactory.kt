package apps.jizzu.simpletodo.service.widget

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.ui.view.MainActivity
import apps.jizzu.simpletodo.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

/**
 * Class that will fill the list with values.
 * It's methods are very similar to the standard adapter methods.
 */
class ViewFactory internal constructor(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var  mWidgetData: ArrayList<Task>

    /**
     * Called when factory is first constructed.
     */
    override fun onCreate() {
        mWidgetData = arrayListOf()
    }

    /**
     * Called when notifyDataSetChanged() is triggered on the remote adapter.
     */
    override fun onDataSetChanged() {
        mWidgetData = arrayListOf()
        mWidgetData.addAll(MainActivity.mTaskList)
    }

    /**
     * Called when the last RemoteViewsAdapter that is associated with this factory is unbound.
     */
    override fun onDestroy() {

    }

    override fun getCount() = mWidgetData.size

    /**
     * Get a View that displays the data at the specified position in the data set.
     */
    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(mContext.packageName, R.layout.widget_task_item)
        remoteViews.setTextViewText(R.id.item_title, mWidgetData[position].title)

        if (mWidgetData[position].date != 0L) {
            remoteViews.setViewPadding(R.id.item_title, 0, 0, 0, 0)
            remoteViews.setViewVisibility(R.id.item_date, View.VISIBLE)

            when {
                DateUtils.isToday(mWidgetData[position].date) -> {
                    remoteViews.setTextColor(R.id.item_date, ContextCompat.getColor(mContext, R.color.colorPrimary))
                    remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_today) + " " + Utils.getTime(mWidgetData[position].date))
                }
                DateUtils.isToday(mWidgetData[position].date + DateUtils.DAY_IN_MILLIS) -> {
                    remoteViews.setTextColor(R.id.item_date, ContextCompat.getColor(mContext, R.color.red))
                    remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_yesterday) + " " + Utils.getTime(mWidgetData[position].date))
                }
                DateUtils.isToday(mWidgetData[position].date - DateUtils.DAY_IN_MILLIS) -> remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_tomorrow) + " " + Utils.getTime(mWidgetData[position].date))
                mWidgetData[position].date < Calendar.getInstance().timeInMillis -> {
                    remoteViews.setTextColor(R.id.item_date, ContextCompat.getColor(mContext, R.color.red))
                    remoteViews.setTextViewText(R.id.item_date, Utils.getFullDate(mWidgetData[position].date))
                }
                else -> remoteViews.setTextViewText(R.id.item_date, Utils.getFullDate(mWidgetData[position].date) + mContext.getString(R.string.date_format_at) + Utils.getTime(mWidgetData[position].date))
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
        fillInIntent.putExtra(WidgetProvider.ITEM_ID, mWidgetData[position].id)
        fillInIntent.putExtra(WidgetProvider.ITEM_TITLE, mWidgetData[position].title)
        fillInIntent.putExtra(WidgetProvider.ITEM_POSITION, position)
        fillInIntent.putExtra(WidgetProvider.ITEM_TIME_STAMP, mWidgetData[position].timeStamp)

        if (mWidgetData[position].date != 0L) {
            fillInIntent.putExtra(WidgetProvider.ITEM_DATE, mWidgetData[position].date)
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
