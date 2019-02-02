package apps.jizzu.simpletodo.ui.settings.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.service.alarm.AlarmReceiver
import apps.jizzu.simpletodo.ui.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.PreferenceHelper
import apps.jizzu.simpletodo.utils.gone
import kotlinx.android.synthetic.main.fragment_notifications.*

class FragmentNotifications : BaseSettingsFragment() {
    private lateinit var mPreferenceHelper: PreferenceHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_notifications))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPreferenceHelper = PreferenceHelper.getInstance()
        initGeneralNotificationSwitch()
        initNotificationSoundButton()
    }

    private fun initGeneralNotificationSwitch() {
        switchGeneralNotification.setOnTouchListener { _, event -> event.actionMasked == MotionEvent.ACTION_MOVE }
        switchGeneralNotification.isChecked = mPreferenceHelper.getBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON)

        switchGeneralNotification.setOnClickListener {
            mPreferenceHelper.putBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON, switchGeneralNotification.isChecked)
            callback?.onGeneralNotificationStateChanged()
        }

        buttonGeneralNotification.setOnClickListener {
            switchGeneralNotification.isChecked = !switchGeneralNotification.isChecked
            mPreferenceHelper.putBoolean(PreferenceHelper.GENERAL_NOTIFICATION_IS_ON, switchGeneralNotification.isChecked)
            callback?.onGeneralNotificationStateChanged()
        }
    }

    private fun initNotificationSoundButton() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = notificationManager.getNotificationChannel(AlarmReceiver.NOTIFICATION_CHANNEL_ID)

            if (notificationChannel == null) {
                val channel = NotificationChannel(AlarmReceiver.NOTIFICATION_CHANNEL_ID, context?.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_HIGH).apply {
                    enableLights(true)
                    lightColor = Color.GREEN
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            buttonNotificationSound.setOnClickListener {
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
                        .putExtra(Settings.EXTRA_CHANNEL_ID, AlarmReceiver.NOTIFICATION_CHANNEL_ID)
                startActivity(intent)
            }
        } else {
            buttonNotificationSound.gone()
        }
    }

    interface GeneralNotificationClickListener {
        fun onGeneralNotificationStateChanged()
    }

    companion object {
        var callback: GeneralNotificationClickListener? = null
    }
}