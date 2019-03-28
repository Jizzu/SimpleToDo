package apps.jizzu.simpletodo.ui.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
        restoreData()
    }

    fun hideKeyboard(editText: EditText) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    private fun restoreData() {
        val note = intent.getStringExtra("note")
        taskNote.apply {
            setText(note)
            setSelection(note.length)
        }
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
            android.R.id.home -> onBackPressed()
            R.id.action_save -> saveNote()
        }
        return super.onOptionsItemSelected(item)
    }
}