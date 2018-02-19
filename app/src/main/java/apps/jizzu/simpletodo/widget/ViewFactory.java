package apps.jizzu.simpletodo.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.Calendar;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.adapter.RecyclerViewAdapter;
import apps.jizzu.simpletodo.model.ModelTask;
import apps.jizzu.simpletodo.utils.Utils;

import static android.content.ContentValues.TAG;

/**
 * Class that will fill the list with values.
 * It's methods are very similar to the standard adapter methods.
 */
public class ViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private ArrayList<ModelTask> mListData;
    private Context mContext;

    ViewFactory(Context context) {
        mContext = context;
    }

    /**
     * Called when factory is first constructed.
     */
    @Override
    public void onCreate() {
        mListData = new ArrayList<>();
    }

    /**
     * Called when notifyDataSetChanged() is triggered on the remote adapter.
     */
    @Override
    public void onDataSetChanged() {
        mListData.clear();
        mListData.addAll(RecyclerViewAdapter.mItems);
    }

    /**
     * Called when the last RemoteViewsAdapter that is associated with this factory is unbound.
     */
    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     */
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.item_title, mListData.get(position).getTitle());

        if (mListData.get(position).getDate() != 0) {
            remoteViews.setViewPadding(R.id.item_title, 0, 0, 0, 0);
            remoteViews.setViewVisibility(R.id.item_date, View.VISIBLE);

            if (DateUtils.isToday(mListData.get(position).getDate())) {
                remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_today) + "  " + Utils.getTime(mListData.get(position).getDate()));
            } else if (DateUtils.isToday(mListData.get(position).getDate() + DateUtils.DAY_IN_MILLIS)) {
                remoteViews.setTextColor(R.id.item_date, ContextCompat.getColor(mContext, R.color.red));
                remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_yesterday) + "  " + Utils.getTime(mListData.get(position).getDate()));
            } else if (DateUtils.isToday(mListData.get(position).getDate() - DateUtils.DAY_IN_MILLIS)) {
                remoteViews.setTextViewText(R.id.item_date, mContext.getString(R.string.reminder_tomorrow) + "  " + Utils.getTime(mListData.get(position).getDate()));
            } else if (mListData.get(position).getDate() < Calendar.getInstance().getTimeInMillis()) {
                remoteViews.setTextColor(R.id.item_date, ContextCompat.getColor(mContext, R.color.red));
                remoteViews.setTextViewText(R.id.item_date, Utils.getFullDate(mListData.get(position).getDate()));
            } else {
                remoteViews.setTextViewText(R.id.item_date, Utils.getFullDate(mListData.get(position).getDate()));
            }
        } else {
            Display d = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int width = d.getWidth();
            int height = d.getHeight();
            Log.d(TAG, "width = " + width + ", height = " + height);

            remoteViews.setViewVisibility(R.id.item_date, View.GONE);
            if (width >= 1080 || height >= 1776) {
                remoteViews.setViewPadding(R.id.item_title, 0, 27, 0, 27);
            } else if (width >= 720 || height >= 1184) {
                remoteViews.setViewPadding(R.id.item_title, 0, 20, 0, 20);
            } else if (width >= 480 || height >= 800) {
                remoteViews.setViewPadding(R.id.item_title, 0, 15, 0, 15);
            }
        }
        return remoteViews;
    }

    /**
     * This allows for the use of a custom loading view which appears between the time that getViewAt(int) is called and returns.
     */
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * Returns the number of types of Views that will be created by getView().
     * This adapter always returns the same type of View for all items.
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Indicates whether the item ids are stable across changes to the underlying data..
     * Returns true if the same id always refers to the same object.
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }
}
