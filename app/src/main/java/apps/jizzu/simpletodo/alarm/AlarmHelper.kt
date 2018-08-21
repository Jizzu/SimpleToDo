package apps.jizzu.simpletodo.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

import apps.jizzu.simpletodo.model.ModelTask

import android.content.Context.NOTIFICATION_SERVICE

/**
 * Class for initializing alarm service (uses the Singleton pattern).
 */
class AlarmHelper private constructor() {

    private lateinit var mContext: Context
    private lateinit var mAlarmManager: AlarmManager

    /**
     * Alarm service initialization.
     */
    fun init(context: Context) {
        this.mContext = context
        mAlarmManager = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    /**
     * Passes the required data to the AlarmReceiver to create a notification.
     */
    fun setAlarm(task: ModelTask) {
        val intent = Intent(mContext, AlarmReceiver::class.java)
        intent.putExtra("title", task.title)
        intent.putExtra("time_stamp", task.timeStamp)

        val pendingIntent = PendingIntent.getBroadcast(mContext.applicationContext, task.timeStamp.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, task.date, pendingIntent)
    }

    /**
     * Removes notification by id (timeStamp).
     */
    fun removeNotification(taskTimeStamp: Long, context: Context) {
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskTimeStamp.toInt())
    }

    /**
     * Removes alarm by id (timeStamp).
     */
    fun removeAlarm(taskTimeStamp: Long) {
        val intent = Intent(mContext, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(mContext, taskTimeStamp.toInt(),
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        mAlarmManager.cancel(pendingIntent)
    }

    companion object {

        private var mInstance: AlarmHelper? = null

        /**
         * This static method ensures that only one AlarmHelper will ever exist at any given time.
         */
        fun getInstance(): AlarmHelper {
            if (mInstance == null) {
                mInstance = AlarmHelper()
            }
            return mInstance as AlarmHelper
        }
    }
}
