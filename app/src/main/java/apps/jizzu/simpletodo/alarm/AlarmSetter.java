package apps.jizzu.simpletodo.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import apps.jizzu.simpletodo.database.DBHelper;
import apps.jizzu.simpletodo.model.ModelTask;

/**
 * Class for restoring all notifications after device reboot.
 */
public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        DBHelper helper = DBHelper.getInstance(context);

        AlarmHelper.getInstance().init(context);
        AlarmHelper alarmHelper = AlarmHelper.getInstance();

        List<ModelTask> tasks = new ArrayList<>();
        tasks.addAll(helper.getAllTasks());

        for (ModelTask task : tasks) {
            if (task.getDate() != 0) {
                alarmHelper.setAlarm(task);
            }
        }
    }
}
