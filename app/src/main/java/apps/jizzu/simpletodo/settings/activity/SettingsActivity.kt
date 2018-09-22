package apps.jizzu.simpletodo.settings.activity

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.recycler.RecyclerViewEmptySupport
import apps.jizzu.simpletodo.settings.fragment.SettingsFragment
import apps.jizzu.simpletodo.widget.WidgetProvider

/**
 * Activity which contains SettingsFragment.
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        title = ""
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        toolbarTitle.text = getString(R.string.settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            //toolbar.setTitleTextColor(resources.getColor(R.color.white))
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        fragmentManager.beginTransaction().replace(R.id.content_frame, SettingsFragment()).commit()
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
