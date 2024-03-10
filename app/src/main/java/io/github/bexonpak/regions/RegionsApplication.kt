package io.github.bexonpak.regions

import android.app.Application
import io.github.bexonpak.regions.helper.SharedPreferenceHelpers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegionsApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            SharedPreferenceHelpers.init(this@RegionsApplication)
        }
    }
}