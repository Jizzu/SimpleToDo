package apps.jizzu.simpletodo.widget

import android.content.Intent
import android.widget.RemoteViewsService

/**
 * The service to be connected to for a remote adapter to request RemoteViews.
 */
class WidgetService : RemoteViewsService() {

    /**
     * Method to be implemented by the derived service to generate appropriate factories for the data.
     */
    override fun onGetViewFactory(intent: Intent) = ViewFactory(applicationContext)
}
