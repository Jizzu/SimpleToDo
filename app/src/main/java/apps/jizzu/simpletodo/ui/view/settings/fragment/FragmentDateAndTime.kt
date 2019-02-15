package apps.jizzu.simpletodo.ui.view.settings.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.settings.fragment.base.BaseSettingsFragment
import apps.jizzu.simpletodo.utils.PreferenceHelper
import kotlinx.android.synthetic.main.fragment_date_and_time.*

class FragmentDateAndTime : BaseSettingsFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_date_and_time, container, false)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getString(R.string.settings_page_title_date_and_time))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        val preferenceHelper = PreferenceHelper.getInstance()

        buttonDateFormat.setOnClickListener {
            val listItems = resources.getStringArray(R.array.date_format_list)
            var selectedItemPosition = preferenceHelper.getInt(PreferenceHelper.DATE_FORMAT_KEY)

            AlertDialog.Builder(activity as Context, R.style.AlertDialogStyle).apply {
                setTitle(getString(R.string.date_format_dialog_title))
                setSingleChoiceItems(listItems, selectedItemPosition) { dialogInterface, i ->
                    selectedItemPosition = i
                    preferenceHelper.putInt(PreferenceHelper.DATE_FORMAT_KEY, i)
                    dialogInterface.dismiss()
                }
                show()
            }
        }

        buttonTimeFormat.setOnClickListener {
            val listItems = resources.getStringArray(R.array.time_format_list)
            var selectedItemPosition = preferenceHelper.getInt(PreferenceHelper.TIME_FORMAT_KEY)

            AlertDialog.Builder(activity as Context, R.style.AlertDialogStyle).apply {
                setTitle(getString(R.string.time_format_dialog_title))
                setSingleChoiceItems(listItems, selectedItemPosition) { dialogInterface, i ->
                    selectedItemPosition = i
                    preferenceHelper.putInt(PreferenceHelper.TIME_FORMAT_KEY, i)
                    dialogInterface.dismiss()
                }
                show()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback?.onDateAndTimeFormatChanged()
    }

    interface DateAndTimeFormatCallback {
        fun onDateAndTimeFormatChanged()
    }

    companion object {
        var callback: DateAndTimeFormatCallback? = null
    }
}