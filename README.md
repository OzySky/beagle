# Beagle (Android library)
*A smart, reliable, and highly customizable debug menu library for Android apps that supports screen recording, network activity logging, and many other useful features.*

<img src="metadata/logo.png" width="20%" />

## See it in action
Clone this repository, pick a build variant and run the **app** configuration. It should look something like this:

<img src="metadata/screenshot01.png" width="25%" /><img src="metadata/screenshot02.png" width="25%" /><img src="metadata/screenshot03.png" width="25%" /><img src="metadata/screenshot04.png" width="25%" />

This demo application also contains instructions on how to set up Beagle and how to implement the various features that are being showcased. You should definitely consider giving it a try if you're interested in using the library in your projects. If you don't feel like building it for yourself, you can also download it from the Play Store:

[<img src="https://play.google.com/intl/en_us/badges/images/badge_new.png" />](https://play.google.com/store/apps/details?id=com.pandulapeter.beagle)

The tutorials in the app cover everything from this readme, but in more detail.

## Use it in your project
### Step 0: Check the requirements
- Minimum SDK level: 16+
- Target SDK level: 30+
- Language: Kotlin 1.4.10 (should work with Java as well but the API was not optimized for that)

### Step 1: Add the Jitpack repository
Make sure that the following is part of your project-level build.gradle file:

```groovy
allprojects {
    repositories {
        …
        maven { url "https://jitpack.io" }
    }
}
```

### Step 2: Pick a UI implementation and configure the dependencies
The actual UI of the debug menu can be displayed in multiple ways, which is specified by the suffix of the dependency.
The following versions exist:
* **ui-activity** - Displays the debug menu as a new screen (not recommended: modals are more useful).
* **ui-bottom-sheet** - Displays the debug menu as a modal bottom sheet (recommended).
* **ui-dialog** - Displays the debug menu as a modal dialog (recommended).
* **ui-drawer** - Displays the debug menu as a side navigation drawer (highly recommended).
* **ui-view** - Displaying DebugMenuView is your responsibility (not recommended: shake to open, Beagle.show(), Beagle.hide(), the related VisibilityListener as well as the inset handling logic won't work out of the box).
* **noop** - No UI, no logic. It has the same public API as all other variants, but it does nothing (this is intended for production builds).

So, for example, if you prefer the Drawer UI, something like the following needs to be added to your app-level build.gradle file (check the widget below the code snippet for the latest version):

```groovy
dependencies {
    …
    def beagleVersion = "2.1.2"
    debugImplementation "com.github.pandulapeter.beagle:ui-drawer:$beagleVersion"
    releaseImplementation "com.github.pandulapeter.beagle:noop:$beagleVersion"
}
```

The latest version is:


[![](https://jitpack.io/v/pandulapeter/beagle.svg)](https://jitpack.io/#pandulapeter/beagle)

**Note**: In case of the drawer UI, if you have overwritten the Activity's onBackPressed() method, you might notice that the default back navigation handling does not always work as expected. To fix this, in every Activity's onBackPressed() you should check that Beagle.hide() returns false before doing any other checks or calling the super implementation.

### Step 3: Initialize the library
Just one line of code, preferably in the Application's onCreate() method:

```kotlin
Beagle.initialize(this)
```

Optionally you can add the following parameters to this function:
* The appearance of the menu can be personalized by specifying an [Appearance](https://github.com/pandulapeter/beagle/blob/master/common/src/main/java/com/pandulapeter/beagle/common/configuration/Appearance.kt) instance.
* The behavior of the menu can be personalized by specifying a [Behavior](https://github.com/pandulapeter/beagle/blob/master/common/src/main/java/com/pandulapeter/beagle/common/configuration/Behavior.kt) instance.

By default you can fetch Beagle by shaking the device.

### Step 4: Finish the setup by adding modules
After this a number of modules should be provided, but this configuration can be changed at any time (from any thread) and the UI will automatically be updated. The simplest way of doing this is by calling:

```kotlin
Beagle.set(module1, module2, …)
```
At this point you should be aware of two options:
* The list of [built-in modules](https://github.com/pandulapeter/beagle/blob/master/common/src/main/java/com/pandulapeter/beagle/modules/). Every file in this package is documented. These modules should cover most use cases and have the advantage of also providing a fake, **noop** implementation which means that no part of their logic is compiled into your release builds.
* The ability to write custom modules. For this a good starting point is looking at the built-in implementations from above, but [this document](https://github.com/pandulapeter/beagle/blob/master/metadata/CUSTOM_MODULES.md) also provides some guidance.

Check out [the showcase app](https://play.google.com/store/apps/details?id=com.pandulapeter.beagle) for some ideas on what is possible with the built-in modules or for an interactive tool which can be used to preview any module configuration and generate the code for it.

Here is a minimal example that should work for most projects:

<img align="right" width="30%" src="metadata/screenshot05.png">

```kotlin
Beagle.set(
    HeaderModule(
        title = getString(R.string.app_name),
        subtitle = BuildConfig.APPLICATION_ID,
        text = "${BuildConfig.BUILD_TYPE} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    ),
    AppInfoButtonModule(),
    DeveloperOptionsButtonModule(),
    PaddingModule(),
    TextModule("General", TextModule.Type.SECTION_HEADER),
    KeylineOverlaySwitchModule(),
    AnimationDurationSwitchModule(),
    ScreenCaptureToolboxModule(),
    DividerModule(),
    TextModule("Logs", TextModule.Type.SECTION_HEADER),
    NetworkLogListModule(), // Might require additional setup, see below
    LogListModule(), // Might require additional setup, see below
    LifecycleLogListModule(),
    DividerModule(),
    TextModule("Other", TextModule.Type.SECTION_HEADER),
    DeviceInfoModule()
)
```

## Logging and intercepting network events
To add content to [LogListModule](https://github.com/pandulapeter/beagle/tree/master/common/src/main/java/com/pandulapeter/beagle/modules/LogListModule.kt) or [NetworkLogListModule](https://github.com/pandulapeter/beagle/tree/master/common/src/main/java/com/pandulapeter/beagle/modules/NetworkLogListModule.kt), you can simply call Beagle.log() and Beagle.logNetworkEvent() respectively. However, you might need to access this functionality from pure Java / Kotlin modules or, in the case of network events, you might want to use an Interceptor / Logger that works out of the box.

### Logging
While calling Beagle.log() is the simplest way to add items to the log list, a special workaround is needed to access this functionality from pure Kotlin modules. Another frequent use case is integration with [Timber](https://github.com/JakeWharton/timber).

#### Logging from pure Kotlin modules
To access the same functionality that Beagle.log() provides from a pure Kotlin / Java module, first you need to add the following to the module in question:

```groovy
dependencies {
    …
    api "com.github.pandulapeter.beagle:log:$beagleVersion"

    // Alternative for Android modules:
    // debugApi "com.github.pandulapeter.beagle:log:$beagleVersion"
    // releaseApi "com.github.pandulapeter.beagle:log-noop:$beagleVersion"
}
```

These libraries provide the **BeagleLogger** object which needs to be connected to the main library when it is initialized in the Application class:

```kotlin
Beagle.initialize(
    …
    behavior = Behavior(
        …
        logger = BeagleLogger
    )
)
```

To add log messages, now you can call the following:

```kotlin
BeagleLogger.log(…)
```

The messages list will be merged with the ones logged using the regular Beagle.log() function (unless they are filtered by their tags) and can be displayed using a [LogListModule](https://github.com/pandulapeter/beagle/tree/master/common/src/main/java/com/pandulapeter/beagle/modules/LogListModule.kt). You can also use BeagleLogger.clearLogs() if you cannot access Beagle.clearLogs().

#### Logging with Timber
To automatically add events logged with [Timber](https://github.com/JakeWharton/timber) to the debug menu, planting a special tree is the simplest solution:

```kotlin
Timber.plant(
    object : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) =
            Beagle.log("[$tag] $message", "Timber", t?.stackTraceToString())
    }
)
```

To create a special LogListModule that only displays these logs, simply set the **label** constructor parameter of the module to "Timber".

### Intercepting network events
Not bundling the network interceptor with the main library was mainly done to provide a pure Kotlin dependency that does not use the Android SDK, similarly to the logger solution described above. However, another reason was to provide the ability to choose between multiple implementations, in function of the project tech stack. At the moment, out of the box, Beagle can hook into two networking libraries (but manually calling Beagle.logNetworkEvent() is always an option).

#### OkHttp
Add the following to the module where your networking logic is implemented:

```groovy
dependencies {
    …
    api "com.github.pandulapeter.beagle:log-okhttp:$beagleVersion"
    
    // Alternative for Android modules:
    // debugApi "com.github.pandulapeter.beagle:log-okhttp:$beagleVersion"
    // releaseApi "com.github.pandulapeter.beagle:log-okhttp-noop:$beagleVersion"
}
```

This will introduce the **BeagleOkHttpLogger** object which first needs to be connected to the main library, the moment it gets initialized:

```kotlin
Beagle.initialize(
    …
    behavior = Behavior(
        …
        networkLoggers = listOf(BeagleOkHttpLogger)
    )
)
```

The last step is setting up the Interceptor (the awkward casting is there to make sure the noop implementation does nothing while still having the same public API):

```kotlin
val client = OkHttpClient.Builder()
    …
    .apply { (BeagleOkHttpLogger.logger as? Interceptor?)?.let { addInterceptor(it) } }
    .build()
```

#### Ktor (Android engine)
Add the following to the module where your networking logic is implemented:
 
```groovy
dependencies {
    …
    api "com.github.pandulapeter.beagle:log-ktor:$beagleVersion"
    
    // Alternative for Android modules:
    // debugApi "com.github.pandulapeter.beagle:log-ktor:$beagleVersion"
    // releaseApi "com.github.pandulapeter.beagle:log-ktor-noop:$beagleVersion"
}
```

This will introduce the **BeagleKtorLogger** object which first needs to be connected to the main library, the moment it gets initialized:

```kotlin
Beagle.initialize(
    …
    behavior = Behavior(
        …
        networkLoggers = listOf(BeagleKtorLogger)
    )
)
```

The last step is setting up the Logger (the awkward casting is there to make sure the noop implementation does nothing while still having the same public API):

```kotlin
val client = HttpClient(engine) {
    …
    (BeagleKtorLogger.logger as? HttpClientFeature<*,*>?)?.let { install(it) }
}
```

## Documentation
All public functions are documented with KDoc. The [BeagleContract](https://github.com/pandulapeter/beagle/blob/master/common/src/main/java/com/pandulapeter/beagle/common/contracts/BeagleContract.kt) file is a good start for learning about all the built-in capabilities. For information on the [individual modules](https://github.com/pandulapeter/beagle/tree/master/common/src/main/java/com/pandulapeter/beagle/modules), see the relevant class headers.

If you're interested in what's under the hood, [this document](https://github.com/pandulapeter/beagle/blob/master/metadata/DOCUMENTATION.md) can be helpful while navigating the source code. 

## Changelog
Check out the [Releases](https://github.com/pandulapeter/beagle/releases) page for the changes in every version.

The library uses [semantic versioning](https://semver.org): **MAJOR.MINOR.PATCH** where **PATCH** changes only contain bug fixes, **MINOR** changes add new features and **MAJOR** changes introduce breaking modifications to the API.

## Known issues
Check out the [Issues](https://github.com/pandulapeter/beagle/issues) page for the list of known problems and for the planned enhancements of the library.

Don't hesitate to open a new issue if you find a bug or if you have any questions / feature requests!

## Buy me a beer
If you found my work useful and are considering a small donation, the About section of the [the showcase app](https://play.google.com/store/apps/details?id=com.pandulapeter.beagle) has an option for you to do so. Thanks in advance!

## License
```
Copyright 2020 Pandula Péter

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```