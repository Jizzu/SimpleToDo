package apps.jizzu.simpletodo.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.activity.EditTaskActivity
import apps.jizzu.simpletodo.activity.MainActivity

/**
 * Class for implementing lifecycle methods of widget.
 */
class WidgetProvider : AppWidgetProvider() {

    /**
     * Called in response to the ACTION_APPWIDGET_UPDATE and ACTION_APPWIDGET_RESTORED broadcasts
     * when this AppWidget provider is being asked to provide RemoteViews for a set of AppWidgets.
     */
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        val clickPI = PendingIntent.getActivity(context, 0,
                Intent(context, MainActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT)

        for (appWidgetId in appWidgetIds) {

            // Intent that contains the data for calling the WidgetService class
            // When the system wants to update the data in the list, it takes out this intent, binds to the specified service
            // and takes the adapter. This adapter is used for filling and forming list items.
            val adapter = Intent(context, WidgetService::class.java)
            adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            // In this case, we will have extra data in the data and Intents will be different.
            adapter.data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME))

            val widget = RemoteViews(context.packageName, R.layout.widget_list)
            widget.setRemoteAdapter(R.id.widget_list, adapter)
            widget.setOnClickPendingIntent(R.id.toolbar_textView, clickPI)

            val listClickIntent = Intent(context, WidgetProvider::class.java)
            val listClickPIntent = PendingIntent.getBroadcast(context, 0,
                    listClickIntent, 0)
            widget.setPendingIntentTemplate(R.id.widget_list, listClickPIntent)

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)
            appWidgetManager.updateAppWidget(appWidgetId, widget)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val itemID = intent.getLongExtra(ITEM_ID, 0)
        val itemTitle = intent.getStringExtra(ITEM_TITLE)
        val itemPosition = intent.getIntExtra(ITEM_POSITION, -1)
        val itemTimeStamp = intent.getLongExtra(ITEM_TIME_STAMP, 0)
        val itemDate = intent.getLongExtra(ITEM_DATE, 0)

        if (itemPosition != -1) {
            val mainActivity = Intent(context, MainActivity::class.java)
            mainActivity.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            context.startActivity(mainActivity)

            val editTaskActivity = Intent(context, EditTaskActivity::class.java)
            editTaskActivity.putExtra(ITEM_ID, itemID)
            editTaskActivity.putExtra(ITEM_TITLE, itemTitle)
            editTaskActivity.putExtra(ITEM_POSITION, itemPosition)
            editTaskActivity.putExtra(ITEM_TIME_STAMP, itemTimeStamp)
            editTaskActivity.putExtra(ITEM_DATE, itemDate)

            context.startActivity(editTaskActivity)
        }
    }

    companion object {
        internal const val ITEM_ID = "id"
        internal const val ITEM_TITLE = "title"
        internal const val ITEM_POSITION = "position"
        internal const val ITEM_TIME_STAMP = "time_stamp"
        internal const val ITEM_DATE = "date"
    }
}
