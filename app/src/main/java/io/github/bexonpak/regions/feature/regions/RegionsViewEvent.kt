package io.github.bexonpak.regions.feature.regions

sealed class RegionsViewEvent {
    data class ShowPermissionRequiredDialog(val show: Boolean) : RegionsViewEvent()
}
