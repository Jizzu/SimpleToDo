package apps.jizzu.simpletodo.ui.settings.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import apps.jizzu.simpletodo.BuildConfig
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.DeviceInfo
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
        buttonLicenses.setOnClickListener { openFragment(FragmentLicenses()) }
    }

    private fun openFragment(fragment: BaseSettingsFragment) =
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, fragment)?.addToBackStack(null)?.commit()

    private fun rateThisApp() {
        val appPackageName = activity?.packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (exception: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    private fun sendFeedback() {
        val email = Intent(Intent.ACTION_SENDTO)
        email.data = Uri.Builder().scheme("mailto").build()
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf("ilya.ponomarenko.dev@gmail.com"))
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_title))
        email.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_device_info) + "\n" + DeviceInfo.deviceInfo
                + "\n" + getString(R.string.feedback_app_version) + BuildConfig.VERSION_NAME
                + "\n" + getString(R.string.feedback) + "\n")
        try {
            startActivity(Intent.createChooser(email, "Send feedback"))
        } catch (ex: android.content.ActivityNotFoundException) {
            Toast.makeText(activity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUri(uri: String) = startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))

    private companion object {
        const val GOOGLE_PLAY_PAGE = "https://play.google.com/store/apps/developer?id=Ilya+Ponomarenko"
        const val GIT_HUB_PAGE = "https://github.com/Jizzu/SimpleToDo"
    }
}