package apps.jizzu.simpletodo.ui.view

import android.app.Activity
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.ui.view.base.BaseActivity
import daio.io.dresscode.matchDressCode
import kotlinx.android.synthetic.main.activity_task_description.*

class TaskDescriptionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        setContentView(R.layout.activity_task_description)
        initToolbar("N O T E", R.drawable.round_arrow_back_black_24)
        initListeners()
        restoreData()
    }

    private fun initListeners() {
        var isShadowShown = false

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.scrollY > 0 && !isShadowShown) {
                setToolbarShadow(0f, 10f)
                isShadowShown = true
            } else if (scrollView.scrollY == 0 && isShadowShown) {
                setToolbarShadow(10f, 0f)
                isShadowShown = false
            }
        }
    }

    private fun restoreData() {
        val note = intent.getStringExtra("note")
        if (!note.isEmpty()) {
            taskNote.apply {
                setText(note)
                setSelection(note.length)
            }
        } else showKeyboard(taskNote)
    }

    private fun saveNote() {
        setResult(Activity.RESULT_OK, Intent().putExtra("note", taskNote.text.toString()))
        hideKeyboard(taskNote)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.task_note_menu, menu)

        menu.getItem(0).icon.apply {
            mutate()
            setColorFilter(ContextCompat.getColor(this@TaskDescriptionActivity, R.color.blue),
                    PorterDuff.Mode.SRC_IN)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard(taskNote)
                onBackPressed()
            }
            R.id.action_save -> saveNote()
        }
        return super.onOptionsItemSelected(item)
    }
}