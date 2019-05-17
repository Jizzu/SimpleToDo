package apps.jizzu.simpletodo.ui.view.task

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
import kotlinx.android.synthetic.main.activity_task_note.*

class TaskNoteActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        matchDressCode()
        setContentView(R.layout.activity_task_note)
        initToolbar(getString(R.string.task_note), R.drawable.round_close_black_24)
        initScrollViewListener(svTaskDetails)
        restoreData()
    }

    private fun restoreData() {
        val note = intent.getStringExtra("note")
        if (note.isNotEmpty()) {
            tvTaskNote.apply {
                setText(note)
                setSelection(note.length)
            }
        } else showKeyboard(tvTaskNote)
    }

    private fun saveNote() {
        setResult(Activity.RESULT_OK, Intent().putExtra("note", tvTaskNote.text.toString()))
        hideKeyboard(tvTaskNote)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.task_note_menu, menu)

        menu.getItem(0).icon.apply {
            mutate()
            setColorFilter(ContextCompat.getColor(this@TaskNoteActivity, R.color.blue),
                    PorterDuff.Mode.SRC_IN)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                hideKeyboard(tvTaskNote)
                onBackPressed()
            }
            R.id.action_save -> saveNote()
        }
        return super.onOptionsItemSelected(item)
    }
}