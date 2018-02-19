package apps.jizzu.simpletodo.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.activity.MainActivity;

/**
 * Class for implementing lifecycle methods of widget.
 */
public class WidgetProvider extends AppWidgetProvider {

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

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}
