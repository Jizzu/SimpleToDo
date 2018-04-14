package apps.jizzu.simpletodo.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.activity.MainActivity;
import apps.jizzu.simpletodo.utils.MyApplication;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.v4.media.AudioAttributesCompat.USAGE_NOTIFICATION;

/**
 * Class for setting notifications.
 */
public class AlarmReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "1";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        int timeStamp = (int) intent.getLongExtra("time_stamp", 0);

        // Intent to launch the application when you click on notification
        Intent resultIntent = new Intent(context, MainActivity.class);

        if (MyApplication.isActivityVisible()) {
            resultIntent = intent;
        }

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, timeStamp, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ringtonePath = preferences.getString("notification_sound", "");

        // Set NotificationChannel for Android Oreo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "SimpleToDo Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Customize and create notifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        if (!ringtonePath.equals("")) {
            if (SDK_INT >= LOLLIPOP) {
                builder.setSound(Uri.parse(ringtonePath), USAGE_NOTIFICATION);
            } else {
                builder.setSound(Uri.parse(ringtonePath));
            }
        }
        builder.setContentTitle(context.getString(R.string.reminder_text));
        builder.setContentText(title);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(title));
        builder.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        builder.setSmallIcon(R.drawable.ic_check_circle_white_24dp);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        notificationManager.notify(timeStamp, notification);
    }
}
