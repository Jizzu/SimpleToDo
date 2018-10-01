package apps.jizzu.simpletodo.settings.activity

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.recycler.RecyclerViewEmptySupport
import apps.jizzu.simpletodo.settings.fragment.FragmentSettings
import apps.jizzu.simpletodo.widget.WidgetProvider
import kotlinx.android.synthetic.main.activity_settings.*

/**
 * Activity which contains SettingsFragment.
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initToolbar()
        openSettingsFragment()
    }

    private fun initToolbar() {
        title = ""
        setToolbarTitle(getString(R.string.settings))

        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_arrow_back_black_24)
        }
    }

    fun setToolbarTitle(title: String) {
        toolbar_title.text = title
    }

    private fun openSettingsFragment() =
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentSettings()).commit()

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

        RecyclerViewEmptySupport.isAnimationCanceled = false

        val intent = Intent(this, WidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(this@SettingsActivity)
                .getAppWidgetIds(ComponentName(this@SettingsActivity, WidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}
