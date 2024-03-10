package io.github.bexonpak.regions.feature.regions.favorite

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.bexonpak.regions.databinding.FragmentRegionsFavoriteBinding
import io.github.bexonpak.regions.feature.regions.RegionsViewEvent
import io.github.bexonpak.regions.feature.regions.RegionsViewModel
import io.github.bexonpak.regions.feature.regions.RegionsViewState
import io.github.bexonpak.regions.feature.regions.LocaleListAdapter
import io.github.bexonpak.regions.helper.collectOnLifecycle
import io.github.bexonpak.regions.lib.Regions
import java.util.Locale

class RegionsFavoriteFragment : Fragment() {

    private lateinit var binding: FragmentRegionsFavoriteBinding
    private val activityViewModel: RegionsViewModel by activityViewModels()

    private val adapter by lazy {
        LocaleListAdapter(
            LayoutInflater.from(requireContext()),
            lifecycleScope,
            { locale, favorite ->
                Log.d(TAG, "favorite: $favorite")
                if (favorite) {
                    activityViewModel.removeFavorite(requireContext(), locale)
                } else {
                    activityViewModel.addFavorite(requireContext(), locale)
                }
            }
        ) { locale ->
            if (activityViewModel.viewStateFlow.value.permissionGranted) {
                Regions.setLocale(locale)
            } else {
                activityViewModel.showPermissionRequiredDialog(true)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegionsFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initSections()
        setupFlows()

        activityViewModel.getFavorites(requireContext())
    }

    private fun setupFlows() {
        collectOnLifecycle(activityViewModel.viewStateFlow) { renderState(it) }
        collectOnLifecycle(activityViewModel.viewEventFlow) { onEvent(it) }
    }

    private fun onEvent(event: RegionsViewEvent) {

    }

    private fun renderState(state: RegionsViewState) {
        updateStateSectionList(state.favorites)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateStateSectionList(favorites: Array<Locale>) {
        adapter.localeList = favorites
        adapter.favoriteList = favorites
        adapter.notifyDataSetChanged()
    }

    private fun initSections() {
        initSectionList()
    }

    private fun initSectionList() {
        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.adapter = adapter
    }

    companion object {
      private const val TAG = "RegionsFavoriteFragment"
    }
}