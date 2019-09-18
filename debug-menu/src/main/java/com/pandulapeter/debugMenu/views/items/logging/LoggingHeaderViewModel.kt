package com.pandulapeter.debugMenu.views.items.logging

import com.pandulapeter.debugMenu.views.items.DrawerItem
import com.pandulapeter.debugMenuCore.modules.LoggingModule

internal data class LoggingHeaderViewModel(private val loggingModule: LoggingModule, val isExpanded: Boolean) : DrawerItem {

    override val id = "loggingHeader"
    val title = loggingModule.title
}