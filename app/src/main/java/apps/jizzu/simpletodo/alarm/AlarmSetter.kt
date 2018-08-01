package apps.jizzu.simpletodo.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import java.util.ArrayList

import apps.jizzu.simpletodo.database.DBHelper
import apps.jizzu.simpletodo.model.ModelTask

/**
 * Class for restoring all notifications after device reboot.
 */
class AlarmSetter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val helper = DBHelper.getInstance(context)

        AlarmHelper.getInstance().init(context)
        val alarmHelper = AlarmHelper.getInstance()

        val tasks = ArrayList<ModelTask>()
        tasks.addAll(helper.getAllTasks())

        for (task in tasks) {
            if (task.date != 0L) {
                alarmHelper.setAlarm(task)
            }
        }
    }
}
