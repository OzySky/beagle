package com.pandulapeter.beagle.appDemo.data

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.pandulapeter.beagle.appDemo.data.model.ModuleWrapper
import java.util.Collections

class ModuleRepository {

    private val listeners = mutableListOf<Listener>()
    private val _modules = mutableListOf<ModuleWrapper>().apply {
        add(ModuleWrapper.HeaderWrapper)
        add(ModuleWrapper.AppInfoButtonWrapper)
        add(ModuleWrapper.DeveloperOptionsButtonWrapper)
        add(ModuleWrapper.PaddingWrapper())
        add(ModuleWrapper.TextSectionHeaderWrapper())
        add(ModuleWrapper.KeylineOverlaySwitchWrapper)
        add(ModuleWrapper.AnimationDurationSwitchWrapper)
        add(ModuleWrapper.ScreenCaptureToolboxWrapper)
        add(ModuleWrapper.DividerWrapper())
        add(ModuleWrapper.TextSectionHeaderWrapper())
        add(ModuleWrapper.NetworkLogListWrapper)
        add(ModuleWrapper.LogListWrapper())
        add(ModuleWrapper.LifecycleLogListWrapper)
        add(ModuleWrapper.DividerWrapper())
        add(ModuleWrapper.TextSectionHeaderWrapper())
        add(ModuleWrapper.DeviceInfoWrapper)
    }
    val modules: List<ModuleWrapper> get() = _modules

    @Suppress("unused")
    fun registerListener(lifecycleOwner: LifecycleOwner, listener: Listener) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            fun onCreate() {
                if (!listeners.contains(listener)) {
                    listeners.add(listener)
                    listener.onModuleListChanged()
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                listeners.remove(listener)
            }
        })
    }

    fun addModule(moduleWrapper: ModuleWrapper) {
        _modules.add(moduleWrapper)
        notifyListeners()
    }

    fun removeModule(id: String) {
        _modules.removeAll { it.id == id }
        notifyListeners()
    }

    fun onModulesSwapped(oldPosition: Int, newPosition: Int) {
        if (oldPosition < newPosition) {
            for (i in oldPosition until newPosition) {
                Collections.swap(_modules, i, i + 1)
            }
        } else {
            for (i in oldPosition downTo newPosition + 1) {
                Collections.swap(_modules, i, i - 1)
            }
        }
        notifyListeners()
    }

    private fun notifyListeners() = listeners.forEach { it.onModuleListChanged() }

    interface Listener {
        fun onModuleListChanged()
    }
}