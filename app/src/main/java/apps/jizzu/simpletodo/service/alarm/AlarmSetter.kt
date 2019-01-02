package apps.jizzu.simpletodo.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.ArrayList
import apps.jizzu.simpletodo.data.database.TasksDatabase
import apps.jizzu.simpletodo.data.models.Task

/**
 * Class for restoring all notifications after device reboot.
 */
class AlarmSetter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val database = TasksDatabase.getInstance(context)

        AlarmHelper.getInstance().init(context)
        val alarmHelper = AlarmHelper.getInstance()

        val tasks = ArrayList<Task>()
        //tasks.addAll(database.taskDAO().getTasksLiveData())

        for (task in tasks) {
            if (task.date != 0L) {
                alarmHelper.setAlarm(task)
            }
        }
    }
}
