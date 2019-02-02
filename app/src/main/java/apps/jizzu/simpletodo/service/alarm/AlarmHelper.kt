package apps.jizzu.simpletodo.service.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import apps.jizzu.simpletodo.data.models.Task
import android.content.Context.NOTIFICATION_SERVICE

class AlarmHelper private constructor() {
    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mContext: Context

    fun init(context: Context) {
        this.mContext = context
        mAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun setAlarm(task: Task) {
        val intent = Intent(mContext, AlarmReceiver::class.java)
            .putExtra("title", task.title)
            .putExtra("time_stamp", task.timeStamp)
        val pendingIntent = PendingIntent.getBroadcast(mContext, task.timeStamp.toInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, task.date, pendingIntent)
    }

    fun removeNotification(taskTimeStamp: Long, context: Context) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskTimeStamp.toInt())
    }

    fun removeAlarm(taskTimeStamp: Long) {
        val intent = Intent(mContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(mContext, taskTimeStamp.toInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mAlarmManager.cancel(pendingIntent)
    }

    companion object {
        private var mInstance: AlarmHelper? = null

        fun getInstance(): AlarmHelper {
            if (mInstance == null) {
                mInstance = AlarmHelper()
            }
            return mInstance as AlarmHelper
        }
    }
}
