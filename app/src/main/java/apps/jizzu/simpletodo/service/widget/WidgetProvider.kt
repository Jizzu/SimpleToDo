package apps.jizzu.simpletodo.service.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.task.EditTaskActivity
import apps.jizzu.simpletodo.ui.view.MainActivity

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        val onTitleClickPendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_UPDATE_CURRENT)

        for (appWidgetId in appWidgetIds) {
            val adapter = Intent(context, WidgetService::class.java)
            adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            adapter.data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME))

            val widget = RemoteViews(context.packageName, R.layout.widget_list)
            widget.setRemoteAdapter(R.id.lvWidgetTasksList, adapter)
            widget.setOnClickPendingIntent(R.id.tvWidgetTitle, onTitleClickPendingIntent)

            val onTaskClickPendingIntent = PendingIntent.getBroadcast(context, 0, Intent(context, WidgetProvider::class.java), 0)
            widget.setPendingIntentTemplate(R.id.lvWidgetTasksList, onTaskClickPendingIntent)

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lvWidgetTasksList)
            appWidgetManager.updateAppWidget(appWidgetId, widget)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val itemID = intent.getLongExtra(ITEM_ID, 0)
        val itemTitle = intent.getStringExtra(ITEM_TITLE)
        val itemNote = intent.getStringExtra(ITEM_NOTE)
        val itemPosition = intent.getIntExtra(ITEM_POSITION, -1)
        val itemTimeStamp = intent.getLongExtra(ITEM_TIME_STAMP, 0)
        val itemDate = intent.getLongExtra(ITEM_DATE, 0)

        if (itemPosition != -1) {
            val mainActivity = Intent(context, MainActivity::class.java)
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mainActivity)

            val editTaskActivity = Intent(context, EditTaskActivity::class.java)
                    .putExtra(ITEM_ID, itemID)
                    .putExtra(ITEM_TITLE, itemTitle)
                    .putExtra(ITEM_NOTE, itemNote)
                    .putExtra(ITEM_POSITION, itemPosition)
                    .putExtra(ITEM_TIME_STAMP, itemTimeStamp)
                    .putExtra(ITEM_DATE, itemDate)
            editTaskActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(editTaskActivity)
        }
    }

    companion object {
        internal const val ITEM_ID = "id"
        internal const val ITEM_TITLE = "title"
        internal const val ITEM_NOTE = "note"
        internal const val ITEM_POSITION = "position"
        internal const val ITEM_TIME_STAMP = "time_stamp"
        internal const val ITEM_DATE = "date"
    }
}