package apps.jizzu.simpletodo.ui.view.settings.activity

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.WindowManager
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.service.widget.WidgetProvider
import apps.jizzu.simpletodo.ui.view.base.BaseActivity
import apps.jizzu.simpletodo.ui.view.settings.fragment.FragmentSettings
import apps.jizzu.simpletodo.ui.view.settings.fragment.FragmentUI
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.toolbar.*

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        setContentView(R.layout.activity_settings)
        initToolbar(getString(R.string.settings))
        openSettingsFragment()
    }

    fun setToolbarTitle(title: String) {
        tvToolbarTitle.text = title
        checkScreenResolution()
    }

    private fun checkScreenResolution() {
        val displayMetrics = DisplayMetrics()
        (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        if (width <= 480 || height <= 800) {
            tvToolbarTitle.textSize = 18F
        }
    }

    private fun openSettingsFragment() {
        if (!FragmentUI.isThemeChanged) {
            supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, FragmentSettings()).commit()
        } else FragmentUI.isThemeChanged = false
    }

    override fun onResume() {
        super.onResume()
        setToolbarTitle(getString(R.string.settings))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return false
    }

    override fun onPause() {
        super.onPause()

        val intent = Intent(this, WidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(this)
                .getAppWidgetIds(ComponentName(this, WidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}
