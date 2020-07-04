package com.pandulapeter.beagle.appDemo.feature.shared.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.pandulapeter.beagle.appDemo.R
import com.pandulapeter.beagle.appDemo.databinding.ItemSpaceBinding
import com.pandulapeter.beagle.appDemo.feature.main.inspiration.authentication.list.AuthenticationListItem
import com.pandulapeter.beagle.appDemo.feature.main.inspiration.simpleSetup.list.SimpleSetupListItem
import com.pandulapeter.beagle.appDemo.feature.main.inspiration.featureToggles.list.FeatureTogglesListItem
import com.pandulapeter.beagle.appDemo.feature.main.inspiration.list.InspirationListItem
import com.pandulapeter.beagle.appDemo.feature.main.setup.list.SetupListItem
import java.util.UUID

class SpaceViewHolder private constructor(
    binding: ItemSpaceBinding
) : BaseViewHolder<ItemSpaceBinding, SpaceViewHolder.UiModel>(binding) {

    data class UiModel(
        override val id: String = "space_${UUID.randomUUID()}"
    ) : ListItem, SetupListItem, InspirationListItem, SimpleSetupListItem, FeatureTogglesListItem, AuthenticationListItem

    companion object {
        fun create(
            parent: ViewGroup
        ) = SpaceViewHolder(
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_space, parent, false)
        )
    }
}