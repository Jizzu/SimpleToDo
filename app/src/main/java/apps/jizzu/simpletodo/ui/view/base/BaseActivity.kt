package apps.jizzu.simpletodo.ui.view.base

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import apps.jizzu.simpletodo.R
import daio.io.dresscode.dressCodeStyleId
import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        initStatusBar()
    }

    fun initToolbar(titleText: String, drawable: Int? = R.drawable.round_close_black_24, view: Toolbar? = toolbar) {
        title = ""
        toolbarTitle.text = titleText

        if (toolbar != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                val color = when (dressCodeStyleId) {
                    R.style.AppTheme_Light -> R.color.greyWhite
                    R.style.AppTheme_Dark -> R.color.deepBlueGrey
                    R.style.AppTheme_Black -> R.color.black
                    else -> R.color.greyWhite
                }
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, color)
            }
            setSupportActionBar(view)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            if (drawable != null) {
                supportActionBar?.setHomeAsUpIndicator(drawable)
            }
        }
    }

    private fun initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when(dressCodeStyleId) {
                R.style.AppTheme_Light -> {
                    var flags = toolbar.systemUiVisibility
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    toolbar.systemUiVisibility = flags
                    this.window.statusBarColor = Color.WHITE
                }
                R.style.AppTheme_Dark -> ContextCompat.getColor(this, R.color.deepBlueGrey)
                R.style.AppTheme_Black -> ContextCompat.getColor(this, R.color.black)
            }
        }
    }

    fun setToolbarShadow(start: Float, end: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator.ofFloat(start, end).apply {
                addUpdateListener { updatedAnimation ->
                    toolbar.elevation = updatedAnimation.animatedValue as Float
                }
                duration = 500
                start()
            }
        }
    }

    fun initScrollViewListener(scrollView: ScrollView) {
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

    fun showKeyboard(editText: EditText) {
        editText.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboard(editText: EditText) {
        editText.clearFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}