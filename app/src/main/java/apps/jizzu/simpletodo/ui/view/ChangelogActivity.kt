package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.utils.PreferenceHelper
import kotlinx.android.synthetic.main.activity_changelog.*

class ChangelogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changelog)
        initToolbar()
        confirmButton.setOnClickListener { onBackPressed() }
    }

    private fun initToolbar() {
        title = ""
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.round_close_black_24)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else false

    override fun onBackPressed() {
        super.onBackPressed()
        PreferenceHelper.getInstance().putBoolean(PreferenceHelper.IS_FIRST_APP_LAUNCH, false)
    }
}