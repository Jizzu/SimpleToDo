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
import apps.jizzu.simpletodo.utils.DateAndTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class ViewFactory internal constructor(private val mContext: Context) : RemoteViewsService.RemoteViewsFactory {
    private lateinit var mWidgetData: ArrayList<Task>

    override fun onCreate() {
        mWidgetData = arrayListOf()
    }

    override fun onDataSetChanged() {
        mWidgetData = arrayListOf()
        mWidgetData.addAll(MainActivity.mTaskList)
    }

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(mContext.packageName, R.layout.widget_task_item)
        remoteViews.setTextViewText(R.id.tvItemTitle, mWidgetData[position].title)

        if (!mWidgetData[position].note.isEmpty()) {
            remoteViews.setViewVisibility(R.id.ivItemNoteIcon, View.VISIBLE)
        } else remoteViews.setViewVisibility(R.id.ivItemNoteIcon, View.GONE)


        if (mWidgetData[position].date != 0L) {
            remoteViews.apply {
                setViewPadding(R.id.tvItemTitle, 0, 0, 0, 0)
                setViewVisibility(R.id.tvItemDate, View.VISIBLE)
            }

            when {
                // Today
                DateUtils.isToday(mWidgetData[position].date) -> {
                    remoteViews.apply {
                        setTextColor(R.id.tvItemDate, ContextCompat.getColor(mContext, R.color.blue))
                        setTextViewText(R.id.tvItemDate, mContext.getString(R.string.reminder_today, DateAndTimeFormatter.getTime(mWidgetData[position].date)))
                    }
                }

                // Yesterday
                DateUtils.isToday(mWidgetData[position].date + DateUtils.DAY_IN_MILLIS) -> {
                    remoteViews.apply {
                        setTextColor(R.id.tvItemDate, ContextCompat.getColor(mContext, R.color.red))
                        setTextViewText(R.id.tvItemDate, mContext.getString(R.string.reminder_yesterday, DateAndTimeFormatter.getTime(mWidgetData[position].date)))
                    }
                }

                // Tomorrow
                DateUtils.isToday(mWidgetData[position].date - DateUtils.DAY_IN_MILLIS) -> {
                    remoteViews.apply {
                        setTextColor(R.id.tvItemDate, ContextCompat.getColor(mContext, R.color.blue))
                        setTextViewText(R.id.tvItemDate, mContext.getString(R.string.reminder_tomorrow, DateAndTimeFormatter.getTime(mWidgetData[position].date)))
                    }
                }

                // Far past
                mWidgetData[position].date < Calendar.getInstance().timeInMillis -> {
                    remoteViews.apply {
                        setTextColor(R.id.tvItemDate, ContextCompat.getColor(mContext, R.color.red))
                        setTextViewText(R.id.tvItemDate, DateAndTimeFormatter.getFullDate(mWidgetData[position].date))
                    }
                }

                // Far future
                else -> {
                    remoteViews.apply {
                        setTextColor(R.id.tvItemDate, ContextCompat.getColor(mContext, R.color.blue))
                        setTextViewText(R.id.tvItemDate, mContext.getString(R.string.date_format_at, DateAndTimeFormatter.getFullDate(mWidgetData[position].date), DateAndTimeFormatter.getTime(mWidgetData[position].date)))
                    }
                }
            }
        } else {
            val displayMetrics = DisplayMetrics()
            (mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = displayMetrics.heightPixels
            Log.d(TAG, "width = $width, height = $height")

            remoteViews.setViewVisibility(R.id.tvItemDate, View.GONE)
            if (width >= 1080 || height >= 1776) {
                remoteViews.setViewPadding(R.id.tvItemTitle, 0, 27, 0, 27)
            } else if (width >= 720 || height >= 1184) {
                remoteViews.setViewPadding(R.id.tvItemTitle, 0, 20, 0, 20)
            } else if (width >= 480 || height >= 800) {
                remoteViews.setViewPadding(R.id.tvItemTitle, 0, 15, 0, 15)
            }
        }

        val fillInIntent = Intent()
            .putExtra(WidgetProvider.ITEM_ID, mWidgetData[position].id)
            .putExtra(WidgetProvider.ITEM_TITLE, mWidgetData[position].title)
            .putExtra(WidgetProvider.ITEM_NOTE, mWidgetData[position].note)
            .putExtra(WidgetProvider.ITEM_POSITION, position)
            .putExtra(WidgetProvider.ITEM_TIME_STAMP, mWidgetData[position].timeStamp)

        if (mWidgetData[position].date != 0L) {
            fillInIntent.putExtra(WidgetProvider.ITEM_DATE, mWidgetData[position].date)
        }
        remoteViews.setOnClickFillInIntent(R.id.rvWidgetItem, fillInIntent)

        return remoteViews
    }

    override fun getCount() = mWidgetData.size

    override fun getLoadingView() = null

    override fun getViewTypeCount() = 1

    override fun getItemId(position: Int) = position.toLong()

    override fun hasStableIds() = true

    override fun onDestroy() {

    }
}
