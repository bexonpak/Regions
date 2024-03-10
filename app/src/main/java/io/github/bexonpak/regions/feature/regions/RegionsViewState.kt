package io.github.bexonpak.regions.feature.regions

import java.util.Locale

data class RegionsViewState(
    val default: Locale = Locale.getDefault(),
    val locales: Array<Locale> = emptyArray(),
    val favorites: Array<Locale> = emptyArray(),
    val permissionGranted: Boolean = false,
    val searchText: String = ""
)
