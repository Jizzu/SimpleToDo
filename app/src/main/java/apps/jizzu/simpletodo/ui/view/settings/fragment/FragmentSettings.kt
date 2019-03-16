package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import apps.jizzu.simpletodo.BuildConfig
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.DeviceInfo
import apps.jizzu.simpletodo.utils.toast
import kotlinx.android.synthetic.main.fragment_settings.*

class FragmentSettings : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButtons()
    }

    private fun initButtons() {
        buttonUI.setOnClickListener { openFragment(FragmentUI()) }
        buttonNotifications.setOnClickListener { openFragment(FragmentNotifications()) }
        buttonDateAndTime.setOnClickListener { openFragment(FragmentDateAndTime()) }
        buttonBackupAndRestore.setOnClickListener { openFragment(FragmentBackupAndRestore()) }
        buttonRate.setOnClickListener { rateThisApp() }
        buttonFeedback.setOnClickListener { sendFeedback() }
        buttonOtherApps.setOnClickListener { openUri(GOOGLE_PLAY_PAGE) }
        buttonGitHub.setOnClickListener { openUri(GIT_HUB_PAGE) }
        buttonPrivacyPolicy.setOnClickListener { openUri(PRIVACY_POLICY_PAGE) }
        buttonLicenses.setOnClickListener { openFragment(FragmentLicenses()) }
    }

    private fun openFragment(fragment: BaseSettingsFragment) =
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)?.addToBackStack(null)?.commit()

    private fun rateThisApp() {
        val appPackageName = activity?.packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$APP_PAGE_SHORT_LINK$appPackageName")))
        } catch (exception: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$APP_PAGE_LONG_LINK$appPackageName")))
        }
    }

    private fun sendFeedback() {
        val email = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.Builder().scheme(SCHEME).build()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.author_gmail)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_device_info) + "\n" + DeviceInfo.deviceInfo
                    + "\n" + getString(R.string.feedback_app_version) + BuildConfig.VERSION_NAME
                    + "\n" + getString(R.string.feedback))
        }

        try {
            startActivity(Intent.createChooser(email, getString(R.string.settings_feedback)))
        } catch (exception: android.content.ActivityNotFoundException) {
            toast(getString(R.string.settings_no_email_apps))
        }
    }

    private fun openUri(uri: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        } catch (exception: android.content.ActivityNotFoundException) {
            toast(getString(R.string.settings_no_browser_apps))
        }
    }

    private companion object {
        const val GOOGLE_PLAY_PAGE = "https://play.google.com/store/apps/developer?id=Ilya+Ponomarenko"
        const val GIT_HUB_PAGE = "https://github.com/Jizzu/SimpleToDo"
        const val PRIVACY_POLICY_PAGE = "https://github.com/Jizzu/SimpleToDo/blob/master/PRIVACY_POLICY.md"
        const val APP_PAGE_SHORT_LINK = "market://details?id="
        const val APP_PAGE_LONG_LINK = "https://play.google.com/store/apps/details?id="
        const val SCHEME = "mailto"
    }
}