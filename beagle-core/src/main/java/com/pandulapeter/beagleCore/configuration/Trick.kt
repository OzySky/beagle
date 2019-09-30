package com.pandulapeter.beagleCore.configuration

import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import com.pandulapeter.beagleCore.contracts.BeagleListItemContract
import java.util.UUID

@Suppress("unused")
typealias Module = Trick

/**
 * Contains all supported modules that can be added to the drawer.
 */
sealed class Trick {

    //region Implementation details
    abstract val id: String

    interface Expandable {

        val id: String
        val title: CharSequence
        val isInitiallyExpanded: Boolean
    }
    //endregion

    //region Generic modules
    /**
     * Displays simple text content.
     * This module can be added multiple times as long as the ID is unique.
     *
     * @param id - A unique ID for the module. If you don't intend to dynamically remove / modify the module, a suitable default value is auto-generated.
     * @param text - The text that should be displayed.
     * @param isTitle - Whether or not the text should appear in bold style. False by default.
     */
    data class Text(
        override val id: String = UUID.randomUUID().toString(),
        val text: CharSequence,
        val isTitle: Boolean = false
    ) : Trick()

    /**
     * Displays a longer piece of text that can be collapsed into a title.
     * This module can be added multiple times as long as the ID is unique.
     *
     * @param id - A unique ID for the module. If you don't intend to dynamically remove / modify the module, a suitable default value is auto-generated.
     * @param title - The title of the module.
     * @param text - The text that should be displayed.
     * @param isInitiallyExpanded - Whether or not the list should be expanded when the drawer is opened for the first time. False by default.
     */
    data class LongText(
        override val id: String = UUID.randomUUID().toString(),
        override val title: CharSequence,
        val text: CharSequence,
        override val isInitiallyExpanded: Boolean = false
    ) : Trick(), Expandable

    /**
     * Displays a switch with configurable title and behavior - ideal for feature toggles.
     * This module can be added multiple times as long as the ID is unique.
     *
     * @param id - A unique ID for the module. If you don't intend to dynamically remove / modify the module, a suitable default value is auto-generated.
     * @param title - The text that appears near the switch. "Keyline overlay" by default.
     * @param initialValue - The initial value of the toggle. False by default.
     * @param onValueChanged - Callback that gets invoked when the user changes the value of the toggle.
     */
    data class Toggle(
        override val id: String = UUID.randomUUID().toString(),
        val title: CharSequence,
        val initialValue: Boolean = false,
        val onValueChanged: (newValue: Boolean) -> Unit
    ) : Trick()

    /**
     * Displays a button with configurable text and action.
     * This module can be added multiple times as long as the ID is unique.
     *
     * @param id - A unique ID for the module. If you don't intend to dynamically remove / modify the module, a suitable default value is auto-generated.
     * @param text - The text that should be displayed on the button.
     * @param onButtonPressed - The callback that gets invoked when the user presses the button.
     */
    //TODO: The Buttons don't look great if the app uses Material theme.
    data class Button(
        override val id: String = UUID.randomUUID().toString(),
        val text: CharSequence,
        val onButtonPressed: () -> Unit
    ) : Trick()


    /**
     * Displays an expandable list of custom items and exposes a callback when the user makes a selection. A possible use case could be providing a list of test accounts to make the authentication flow faster.
     * The class is generic to a representation of a list item which must implement the [BeagleListItemContract] interface.
     * This module can be added multiple times as long as the ID is unique.
     *
     * @param id - A unique ID for the module. If you don't intend to dynamically remove / modify the list, a suitable default value is auto-generated.
     * @param title - The text that appears in the header of the module.
     * @param items - The hardcoded list of items implementing the [BeagleListItemContract] interface.
     * @param isInitiallyExpanded - Whether or not the list should be expanded when the drawer is opened for the first time. False by default.
     * @param onItemSelected - The callback that will get executed when an item is selected.
     */
    data class SimpleList<T : BeagleListItemContract>(
        override val id: String = UUID.randomUUID().toString(),
        override val title: CharSequence,
        override val isInitiallyExpanded: Boolean = false,
        val items: List<T>,
        val onItemSelected: (item: T) -> Unit
    ) : Trick(), Expandable {

        fun invokeItemSelectedCallback(id: String) = onItemSelected(items.first { it.id == id })
    }

    /**
     * Displays a list of radio buttons. A possible use case could be changing the base URL of the application to simplify testing on different backend environments.
     * The class is generic to a representation of a list item which must implement the [BeagleListItemContract] interface.
     * This module can be added multiple times as long as the ID is unique.
     *
     * @param id - A unique ID for the module. If you don't intend to dynamically remove / modify the list, a suitable default value is auto-generated.
     * @param title - The text that appears in the header of the module.
     * @param items - The hardcoded list of items implementing the [BeagleListItemContract] interface.
     * @param isInitiallyExpanded - Whether or not the list should be expanded when the drawer is opened for the first time. False by default.
     * @param initialSelectionId - The ID of the item that is selected when the drawer is opened for the first time, or null if no selection should be made initially. Null by default.
     * @param onItemSelectionChanged - The callback that will get executed when the selected item is changed.
     */
    data class SingleSelectionList<T : BeagleListItemContract>(
        override val id: String = UUID.randomUUID().toString(),
        override val title: CharSequence,
        override val isInitiallyExpanded: Boolean = false,
        val items: List<T>,
        val initialSelectionId: String? = null,
        val onItemSelectionChanged: (item: T) -> Unit
    ) : Trick(), Expandable {

        fun invokeItemSelectedCallback(id: String) = onItemSelectionChanged(items.first { it.id == id })
    }
    //endregion

    //region Unique modules
    /**
     * Displays a header on top of the drawer with general information about the app / build.
     * This module can only be added once and will always appear on top.
     *
     * @param title - The title of the app / the debug menu. Null by default (hidden title).
     * @param subtitle - The subtitle of the the debug menu. Consider using the build version ("v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"). Null by default (hidden subtitle).
     * @param text - Additional text you want to display on the debug drawer. Null by default (hidden text).
     */
    data class Header(
        val title: CharSequence? = null,
        val subtitle: CharSequence? = null,
        val text: CharSequence? = null
    ) : Trick() {

        override val id = ID

        companion object {
            const val ID = "header"
        }
    }

    /**
     * Displays a switch that, when enabled, draws a grid over your app with configurable dimensions that you can use to check the alignments of your Views.
     * This module can only be added once.
     *
     * @param title - The text that appears near the switch. "Keyline overlay" by default.
     * @param keylineGrid - The distance between the grid lines. 8dp by default.
     * @param keylinePrimary - The distance between the edge of the screen and the primary keyline. 16dp by default (24dp on tablets).
     * @param keylinePrimary - The distance between the edge of the screen and the secondary keyline. 72dp by default (80dp on tablets).
     * @param gridColor - The color to be used when drawing the grid. By default it will be the debug menu's text color.
     */
    data class KeylineOverlayToggle(
        val title: CharSequence = "Keyline overlay",
        @Dimension val keylineGrid: Int? = null,
        @Dimension val keylinePrimary: Int? = null,
        @Dimension val keylineSecondary: Int? = null,
        @ColorInt val gridColor: Int? = null
    ) : Trick() {

        override val id = ID

        companion object {
            const val ID = "keylineOverlayToggle"
        }
    }

    /**
     * Displays a button that links to the Android App Info page for your app.
     * This module can only be added once.
     *
     * @param text - The text that should be displayed on the button. "Show app info" by default.
     */
    data class AppInfoButton(
        val text: CharSequence = "Show app info"
    ) : Trick() {

        override val id = ID

        companion object {
            const val ID = "appInfoButton"
        }
    }

    /**
     * Displays a button that takes a screenshot of the current layout and allows the user to share it.
     * This module can only be added once.
     *
     * @param text - The text that should be displayed on the button. "Take a screenshot" by default.
     */
    data class ScreenshotButton(
        val text: CharSequence = "Take a screenshot"
    ) : Trick() {

        override val id = ID

        companion object {
            const val ID = "screenshotButton"
        }
    }

    /**
     * Displays an expandable list of historical network activity.
     * Use BeagleInterceptorContract to push a new message to the top of the list.
     * This module can only be added once.
     *
     * @param title - The title of the module. "Network activity" by default.
     * @param baseUrl - When not empty, all URL-s will have the specified String filtered out. Empty by default.
     * @param shouldShowHeaders - Whether of not the detail dialog should also contain the request / response headers. False by default.
     * @param maxItemCount - The maximum number of messages that will appear when expanded. 10 by default.
     * @param shouldShowTimestamp - Whether or not each message should display the timestamp when it was added. False by default.
     * @param isInitiallyExpanded - Whether or not the list should be expanded when the drawer is opened for the first time. False by default.
     */
    data class NetworkLogList(
        override val title: CharSequence = "Network activity",
        val baseUrl: String = "",
        val shouldShowHeaders: Boolean = false,
        val maxItemCount: Int = 10,
        val shouldShowTimestamp: Boolean = false,
        override val isInitiallyExpanded: Boolean = false
    ) : Trick(), Expandable {

        override val id = ID

        init {
            require(maxItemCount > 0) { "Beagle: maxItemCount must be larger than 0 for the NetworkLogListTrick." }
        }

        companion object {
            const val ID = "networkLogging"
        }
    }

    /**
     * Displays an expandable list of your custom logs. An example use case could be logging analytics events.
     * Use Beagle.log() to push a new message to the top of the list.
     * This module can only be added once.
     *
     * @param title - The title of the module. "Logs" by default.
     * @param maxItemCount - The maximum number of messages that will appear when expanded. 10 by default.
     * @param shouldShowTimestamp - Whether or not each message should display the timestamp when it was added. False by default.
     * @param isInitiallyExpanded - Whether or not the list should be expanded when the drawer is opened for the first time. False by default.
     */
    data class LogList(
        override val title: CharSequence = "Logs",
        val maxItemCount: Int = 10,
        val shouldShowTimestamp: Boolean = false,
        override val isInitiallyExpanded: Boolean = false
    ) : Trick(), Expandable {

        override val id = ID

        init {
            require(maxItemCount > 0) { "Beagle: maxItemCount must be larger than 0 for the LogListTrick." }
        }

        companion object {
            const val ID = "logList"
        }
    }
    //endregion
}