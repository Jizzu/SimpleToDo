package apps.jizzu.simpletodo.database;

import android.content.Context;
import android.os.AsyncTask;

import apps.jizzu.simpletodo.model.ModelTask;

/**
 * Moving the tasks update operation in the background thread, in order to get rid of UI lags.
 */
public class TasksOrderUpdate extends AsyncTask<ModelTask, Void, Void> {

    private Context mContext;
    private DBHelper mHelper = DBHelper.getInstance(mContext);

    public TasksOrderUpdate(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(ModelTask... modelTasks) {
        for (ModelTask task : modelTasks) {
            mHelper.updateTaskPosition(task);
        }
        return null;
    }
}
