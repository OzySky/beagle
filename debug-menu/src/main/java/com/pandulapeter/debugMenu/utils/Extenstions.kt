package com.pandulapeter.debugMenu.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.pandulapeter.debugMenuCore.configuration.UiConfiguration

internal fun Context.color(@ColorRes colorResInt: Int) = ContextCompat.getColor(this, colorResInt)

internal fun Context.dimension(@DimenRes dimensionResInt: Int) = resources.getDimensionPixelSize(dimensionResInt)

internal fun Context.drawable(@DrawableRes drawableresId: Int) = AppCompatResources.getDrawable(this, drawableresId)

internal fun Context.getTextColor(uiConfiguration: UiConfiguration) = uiConfiguration.textColor.let { configurationTextColor ->
    if (configurationTextColor == null) {
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        color(typedValue.run { if (resourceId != 0) resourceId else data })
    } else {
        configurationTextColor
    }
}

internal fun View.setBackground(uiConfiguration: UiConfiguration) {
    val backgroundColor = uiConfiguration.backgroundColor
    if (backgroundColor == null) {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        if (typedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && typedValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            setBackgroundColor(typedValue.data)
        } else {
            background = context.drawable(typedValue.resourceId)
        }
    } else {
        setBackgroundColor(backgroundColor)
    }
}

internal var View.visible
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }