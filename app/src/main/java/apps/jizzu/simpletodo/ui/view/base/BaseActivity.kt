package apps.jizzu.simpletodo.ui.view.base

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import apps.jizzu.simpletodo.R
import apps.jizzu.simpletodo.data.models.Task
import apps.jizzu.simpletodo.ui.view.task.EditTaskActivity
import apps.jizzu.simpletodo.utils.toastLong
import com.google.android.material.snackbar.Snackbar
import daio.io.dresscode.dressCodeStyleId
import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        initStatusBar()
    }

    fun initToolbar(titleText: String = "", drawable: Int? = R.drawable.round_arrow_back_black_24, view: Toolbar? = toolbar) {
        title = ""
        tvToolbarTitle.text = titleText

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
            when (dressCodeStyleId) {
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

    private fun openApplicationSettings() =
            startActivityForResult(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")), PERMISSION_REQUEST_CODE)

    fun requestPerms(permission: String, fragment: Fragment? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fragment != null) {
                fragment.requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
            } else requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
        }
    }

    fun showTaskDetailsActivity(task: Task) {
        val intent = Intent(this, EditTaskActivity::class.java).apply {
            putExtra("id", task.id)
            putExtra("title", task.title)
            putExtra("note", task.note)
            putExtra("position", task.position)
            putExtra("time_stamp", task.timeStamp)
            if (task.date != 0L) {
                putExtra("date", task.date)
            }
        }
        startActivity(intent)
    }

    fun setToolbarShadow(start: Float, end: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

    fun isHasPermissions(permission: String): Boolean {
        var result: Int

        for (currentPermission in arrayOf(permission)) {
            result = checkCallingOrSelfPermission(currentPermission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun showNoPermissionSnackbar(view: View, snackbarMessage: String, toastMessage: String, anchorView: View? = null) {
        val snackbar = Snackbar.make(view, snackbarMessage, Snackbar.LENGTH_LONG)
                .setAction(R.string.permission_snackbar_button_settings) {
                    openApplicationSettings()
                    toastLong(toastMessage)
                }
        if (anchorView != null) {
            snackbar.anchorView = anchorView
        }
        snackbar.show()
    }

    fun requestPermissionWithRationale(view: View, message: String, permission: String, callback: PermissionRequestListener? = null, anchorView: View? = null) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.permission_snackbar_button_grant) {
                        if (callback != null) {
                            callback.onPermissionRequest()
                        } else requestPerms(permission)
                    }
            if (anchorView != null) {
                snackbar.anchorView = anchorView
            }
            snackbar.show()
        } else {
            if (callback != null) {
                callback.onPermissionRequest()
            } else requestPerms(permission)
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

    interface PermissionRequestListener {
        fun onPermissionRequest()
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 123
    }
}