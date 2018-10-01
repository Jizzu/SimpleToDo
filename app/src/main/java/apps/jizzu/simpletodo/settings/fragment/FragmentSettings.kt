package apps.jizzu.simpletodo.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import apps.jizzu.simpletodo.R
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
        buttonUI.setOnClickListener {  openUserInterfaceFragment() }
        buttonNotifications.setOnClickListener { openNotifications() }
        buttonDateAndTime.setOnClickListener { Toast.makeText(activity, "buttonDateAndTime", Toast.LENGTH_SHORT).show() }
        buttonBackupAndRestore.setOnClickListener { Toast.makeText(activity, "buttonBackupAndRestore", Toast.LENGTH_SHORT).show() }
        buttonRate.setOnClickListener { Toast.makeText(activity, "buttonRate", Toast.LENGTH_SHORT).show() }
        buttonFeedback.setOnClickListener { Toast.makeText(activity, "buttonFeedback", Toast.LENGTH_SHORT).show() }
        buttonOtherApps.setOnClickListener { Toast.makeText(activity, "buttonOtherApps", Toast.LENGTH_SHORT).show() }
        buttonGitHub.setOnClickListener { Toast.makeText(activity, "buttonGitHub", Toast.LENGTH_SHORT).show() }
        buttonLicenses.setOnClickListener { Toast.makeText(activity, "buttonLicenses", Toast.LENGTH_SHORT).show() }
    }

    private fun openUserInterfaceFragment() =
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, FragmentUI())?.addToBackStack(null)?.commit()

    private fun openNotifications() =
            fragmentManager?.beginTransaction()?.replace(R.id.fragment_container, FragmentNotifications())?.addToBackStack(null)?.commit()
}