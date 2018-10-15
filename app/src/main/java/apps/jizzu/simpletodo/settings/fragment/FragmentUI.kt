package apps.jizzu.simpletodo.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_user_interface.*


class FragmentUI : BaseSettingsFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_interface, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(getString(R.string.settings_page_title_user_interface))
        initAnimationsSwitch()
    }

    private fun initAnimationsSwitch() {
        val mPreferenceHelper = PreferenceHelper.getInstance()

        switchAnimations.isChecked = mPreferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)

        switchAnimations.setOnClickListener {
            mPreferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, switchAnimations.isChecked)
        }

        buttonAnimations.setOnClickListener {
            switchAnimations.isChecked = !switchAnimations.isChecked
            mPreferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, switchAnimations.isChecked)
        }
    }
}