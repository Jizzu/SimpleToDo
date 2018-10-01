package apps.jizzu.simpletodo.settings.fragment.base

import androidx.fragment.app.Fragment
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.settings.activity.SettingsActivity

abstract class BaseSettingsFragment : Fragment() {

    fun setTitle(title: String) {
        (activity as SettingsActivity).setToolbarTitle(title)
    }

    override fun onDetach() {
        super.onDetach()
        setTitle(getString(R.string.settings))
    }
}