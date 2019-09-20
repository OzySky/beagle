package com.pandulapeter.debugMenuCore.configuration.modules

import androidx.annotation.ColorInt
import androidx.annotation.Dimension

/**
 * Displays a switch that draws a keyline grid over the application window when enabled.
 * This module can only be added once.
 *
 * @param title - The text that appears near the switch. "Keyline overlay" by default.
 * @param keylineGrid - The distance between the grid lines. 8dp by default.
 * @param keylinePrimary - The distance between the edge of the screen and the primary keyline. 16dp by default (24dp on tablets).
 * @param keylinePrimary - The distance between the edge of the screen and the secondary keyline. 72dp by default (80dp on tablets).
 * @param gridColor - The color to be used when drawing the grid. By default it will be the debug menu's text color.
 */
//TODO: Create a generic parent module for switches.
data class KeylineOverlayModule(
    val title: String = "Keyline overlay",
    @Dimension val keylineGrid: Int? = null,
    @Dimension val keylinePrimary: Int? = null,
    @Dimension val keylineSecondary: Int? = null,
    @ColorInt val gridColor: Int? = null
) : DebugMenuModule {

    override val id = "keylineOverlay"
}