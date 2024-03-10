package io.github.bexonpak.regions.feature.regions

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.bexonpak.regions.feature.regions.all.RegionsAllFragment
import io.github.bexonpak.regions.feature.regions.favorite.RegionsFavoriteFragment

class RegionsCollectionAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        Log.d("TAG", "createFragment: $position")
        val fragment = when (position) {
            0 -> RegionsAllFragment()
            1 -> RegionsFavoriteFragment()
            else -> RegionsAllFragment()
        }
        return fragment
    }

}