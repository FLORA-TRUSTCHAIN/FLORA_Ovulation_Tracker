package com.example.flora1.core.presentation.ui.toast

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

private fun Context.showToast(
    @StringRes stringId: Int,
    duration: Int = LENGTH_LONG,
) {
    Toast.makeText(this, stringId, duration).show()
}

private var toast: Toast? = null

fun Context.showSingleToast(
    @StringRes stringId: Int,
    duration: Int = LENGTH_LONG,
) {
    toast?.cancel()
    toast = Toast.makeText(this, stringId, duration).also { it.show() }
}

fun Context.showSingleToast(
    text: String,
    duration: Int = LENGTH_LONG,
) {
    toast?.cancel()
    toast = Toast.makeText(this, text, duration).also { it.show() }
}

private fun Fragment.showToast(
    @StringRes stringId: Int,
    duration: Int = LENGTH_LONG,
) = context?.showToast(stringId, duration)

fun Fragment.showSingleToast(
    @StringRes stringId: Int,
    duration: Int = LENGTH_LONG,
) = context?.showSingleToast(stringId, duration)
