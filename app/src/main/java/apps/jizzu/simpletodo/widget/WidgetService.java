package apps.jizzu.simpletodo.widget;


import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * The service to be connected to for a remote adapter to request RemoteViews.
 */
public class WidgetService extends RemoteViewsService {

    /**
     * Method to be implemented by the derived service to generate appropriate factories for the data.
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ViewFactory(getApplicationContext());
    }
}
