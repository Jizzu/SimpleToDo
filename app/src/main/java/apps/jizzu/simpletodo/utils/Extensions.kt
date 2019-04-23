package apps.jizzu.simpletodo.utils

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun View?.gone() {
    this?.visibility = View.GONE
}

fun View?.visible() {
    this?.visibility = View.VISIBLE
}

fun View?.invisible() {
    this?.visibility = View.INVISIBLE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isNotVisible() = visibility != View.VISIBLE

fun Activity?.toast(text: String) {
    if (this != null) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment?.toast(text: String) {
    if (this?.context != null) {
        android.widget.Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
    }
}

fun Activity?.toastLong(text: String) {
    if (this != null) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}

fun Fragment?.toastLong(text: String) {
    if (this?.context != null) {
        android.widget.Toast.makeText(this.context, text, Toast.LENGTH_LONG).show()
    }
}