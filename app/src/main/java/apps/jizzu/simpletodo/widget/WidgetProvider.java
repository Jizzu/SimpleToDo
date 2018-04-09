package apps.jizzu.simpletodo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.activity.EditTaskActivity;
import apps.jizzu.simpletodo.activity.MainActivity;

/**
 * Class for implementing lifecycle methods of widget.
 */
public class WidgetProvider extends AppWidgetProvider {

    final static String ITEM_ID = "id";
    final static String ITEM_TITLE = "title";
    final static String ITEM_POSITION = "position";
    final static String ITEM_TIME_STAMP = "time_stamp";
    final static String ITEM_DATE = "date";

    /**
     * Called in response to the ACTION_APPWIDGET_UPDATE and ACTION_APPWIDGET_RESTORED broadcasts
     * when this AppWidget provider is being asked to provide RemoteViews for a set of AppWidgets.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        PendingIntent clickPI = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT);

        for (int appWidgetId : appWidgetIds) {

            // Intent that contains the data for calling the WidgetService class
            // When the system wants to update the data in the list, it takes out this intent, binds to the specified service
            // and takes the adapter. This adapter is used for filling and forming list items.
            Intent adapter = new Intent(context, WidgetService.class);
            adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            // In this case, we will have extra data in the data and Intents will be different.
            adapter.setData(Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_list);
            widget.setRemoteAdapter(R.id.widget_list, adapter);
            widget.setOnClickPendingIntent(R.id.toolbar_textView, clickPI);

            Intent listClickIntent = new Intent(context, WidgetProvider.class);
            PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                    listClickIntent, 0);
            widget.setPendingIntentTemplate(R.id.widget_list, listClickPIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        long itemID = intent.getLongExtra(ITEM_ID, 0);
        String itemTitle = intent.getStringExtra(ITEM_TITLE);
        int itemPosition = intent.getIntExtra(ITEM_POSITION, -1);
        long itemTimeStamp = intent.getLongExtra(ITEM_TIME_STAMP, 0);
        long itemDate = intent.getLongExtra(ITEM_DATE, 0);

        if (itemPosition != -1) {
            Intent mainActivity = new Intent(context, MainActivity.class);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            context.startActivity(mainActivity);

            Intent editTaskActivity = new Intent(context, EditTaskActivity.class);
            editTaskActivity.putExtra(ITEM_ID, itemID);
            editTaskActivity.putExtra(ITEM_TITLE, itemTitle);
            editTaskActivity.putExtra(ITEM_POSITION, itemPosition);
            editTaskActivity.putExtra(ITEM_TIME_STAMP, itemTimeStamp);
            editTaskActivity.putExtra(ITEM_DATE, itemDate);

            context.startActivity(editTaskActivity);
        }
    }
}
