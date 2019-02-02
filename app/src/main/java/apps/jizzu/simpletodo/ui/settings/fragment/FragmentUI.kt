package apps.jizzu.simpletodo.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_user_interface.*

class FragmentUI : BaseSettingsFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_interface, container, false)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_user_interface))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAnimationSwitch()
    }

    private fun initAnimationSwitch() {
        val preferenceHelper = PreferenceHelper.getInstance()

        switchAnimation.setOnTouchListener { _, event -> event.actionMasked == MotionEvent.ACTION_MOVE }
        switchAnimation.isChecked = preferenceHelper.getBoolean(PreferenceHelper.ANIMATION_IS_ON)

        switchAnimation.setOnClickListener {
            preferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, switchAnimation.isChecked)
        }

        buttonAnimations.setOnClickListener {
            switchAnimation.isChecked = !switchAnimation.isChecked
            preferenceHelper.putBoolean(PreferenceHelper.ANIMATION_IS_ON, switchAnimation.isChecked)
        }
    }
}