package apps.jizzu.simpletodo.ui.view

import android.os.Bundle
import android.view.MenuItem
import apps.jizzu.simpletodo.BuildConfig
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.base.BaseActivity
import apps.jizzu.simpletodo.utils.PreferenceHelper
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_changelog.*

class ChangelogActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        setContentView(R.layout.activity_changelog)
        initToolbar(getString(R.string.whats_new_title), R.drawable.round_close_black_24)
        btnConfirm.setOnClickListener { onBackPressed() }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            if (item.itemId == android.R.id.home) {
                onBackPressed()
                true
            } else false

    override fun onBackPressed() {
        super.onBackPressed()
        PreferenceHelper.getInstance().putInt(PreferenceHelper.VERSION_CODE, BuildConfig.VERSION_CODE)
    }
}