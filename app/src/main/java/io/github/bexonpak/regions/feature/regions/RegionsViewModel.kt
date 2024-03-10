package io.github.bexonpak.regions.feature.regions

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.viewModelScope
import io.github.bexonpak.regions.feature.base.BaseViewModel
import io.github.bexonpak.regions.helper.SharedPreferenceHelpers
import java.util.Locale
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegionsViewModel : BaseViewModel<RegionsViewState, RegionsViewEvent>(RegionsViewState()) {
    fun checkPermission(context: Context) {
        viewModelScope.launch {
            while (!viewStateFlow.value.permissionGranted) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CHANGE_CONFIGURATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    updateViewState {
                        it.copy(
                            permissionGranted = true
                        )
                    }
                }
                delay(DETECTING_PERMISSION_INTERVAL)
            }
        }
    }

    fun getLocaleList(query: String, context: Context) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                getFavorites(context)
            } else {
                val locales = Locale.getAvailableLocales()
                val filter = locales.filter {
                    (it.displayName.contains(query) || it.toString().contains(query))
                }
                val favorites = SharedPreferenceHelpers.getFavorites(context)
                val favoritesFilter = locales.filter {
                    favorites.contains(it.toString())
                }
                updateViewState {
                    it.copy(
                        locales = filter.toTypedArray(),
                        favorites = favoritesFilter.toTypedArray(),
                        searchText = query
                    )
                }
            }
        }
    }

    fun showPermissionRequiredDialog(show: Boolean) {
        viewModelScope.launch {
            sendViewEvent(RegionsViewEvent.ShowPermissionRequiredDialog(show))
        }
    }

    fun getFavorites(context: Context) {
        viewModelScope.launch {
            val locales = Locale.getAvailableLocales()
            val favorites = SharedPreferenceHelpers.getFavorites(context)
            val filter = locales.filter {
                favorites.contains(it.toString())
            }
            updateViewState {
                it.copy(
                    favorites = filter.toTypedArray(),
                    locales = locales
                )
            }
        }
    }

    fun removeFavorite(context: Context, locale: Locale) {
        viewModelScope.launch {
            SharedPreferenceHelpers.removeFavorite(context, locale)
            if (viewStateFlow.value.searchText.isEmpty()) {
                getFavorites(context)
            } else {
                getLocaleList(viewStateFlow.value.searchText, context)
            }
            removeShortcuts(context, locale)
        }
    }

    fun addFavorite(context: Context, locale: Locale) {
        viewModelScope.launch {
            SharedPreferenceHelpers.addFavorite(context, locale)
            if (viewStateFlow.value.searchText.isEmpty()) {
                getFavorites(context)
            } else {
                getLocaleList(viewStateFlow.value.searchText, context)
            }
            addShortcut(context, locale)
        }
    }

    private fun generatecolorBitmap(): Bitmap {
        val bitmap: Bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.save()
        val r = Random.nextInt(256)
        val g = Random.nextInt(256)
        val b = Random.nextInt(256)
        val color = Color.rgb(r,g,b)
        canvas.drawColor(color)
        canvas.restore()
        return bitmap
    }

    private fun addShortcut(context: Context, locale: Locale) {
        viewModelScope.launch {
            val paint = Paint()
            paint.color = Color.WHITE
            paint.isAntiAlias = true
            val bitmap = generatecolorBitmap()
            val language: String = locale.language ?: ""
            val country: String = locale.country ?: ""
            val variant: String = locale.variant ?: ""
            val shortcut = ShortcutInfoCompat.Builder(context, locale.toString())
                .setShortLabel(locale.toString())
                .setLongLabel(locale.displayName)
                .setIcon(IconCompat.createWithBitmap(bitmap))
                .setActivity(ComponentName(context, MainActivity::class.java))
                .setIntent(
                    Intent(Intent.ACTION_VIEW,

                        Uri.parse("bxn://io.github.bexonpak.regions?language=$language&country=$country&variant=$variant"))
                )
                .build()
            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
        }
    }

    private fun removeShortcuts(context: Context, locale: Locale) {
        viewModelScope.launch {
            ShortcutManagerCompat.removeDynamicShortcuts(context, listOf(locale.toString()))
        }
    }

    companion object {
        private const val TAG = "RegionsViewModel"
        private const val DETECTING_PERMISSION_INTERVAL = 1000L
    }
}