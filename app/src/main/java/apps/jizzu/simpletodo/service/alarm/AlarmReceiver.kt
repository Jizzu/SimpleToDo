package apps.jizzu.simpletodo.service.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.MainActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val timeStamp = intent.getLongExtra("time_stamp", 0).toInt()

        // Intent to launch the application when you click on notification
        val resultIntent = Intent(context, MainActivity::class.java)
        resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, timeStamp, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Set NotificationChannel for Android Oreo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)

            if (notificationChannel == null) {
                val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, context.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_HIGH).apply {
                    enableLights(true)
                    lightColor = Color.BLUE
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setContentTitle(context.getString(R.string.reminder_text))
            setContentText(title)
            setStyle(NotificationCompat.BigTextStyle().bigText(title))
            color = ContextCompat.getColor(context, R.color.blue)
            setSmallIcon(R.drawable.ic_check_circle_white_24dp)
            setDefaults(Notification.DEFAULT_ALL)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }
        notificationManager.notify(timeStamp, notification.build())
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1"
        const val GENERAL_NOTIFICATION_CHANNEL_ID = "2"
    }
}
