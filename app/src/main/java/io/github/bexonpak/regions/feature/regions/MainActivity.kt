package io.github.bexonpak.regions.feature.regions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import io.github.bexonpak.regions.feature.license.LicenseActivity
import io.github.bexonpak.regions.R
import io.github.bexonpak.regions.customui.PermissionRequiredDialog
import io.github.bexonpak.regions.databinding.ActivityMainBinding
import io.github.bexonpak.regions.helper.collectOnLifecycle
import io.github.bexonpak.regions.lib.Regions
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val viewModel: RegionsViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchView: SearchView
    private lateinit var regionsCollectionAdapter: RegionsCollectionAdapter
    private lateinit var viewPager: ViewPager2

    private val permissionRequiredDialog: PermissionRequiredDialog by lazy {
        PermissionRequiredDialog(this@MainActivity)
    }

    private val onSearchQueryTextListener: SearchView.OnQueryTextListener by lazy {
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    Log.d(TAG, "onQueryTextSubmit: $it")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    Log.d(TAG, "onQueryTextChange: $it")
                    viewModel.getLocaleList(it, this@MainActivity)
                }
                return true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.action == Intent.ACTION_VIEW) {
            intent.data?.let {  uri ->
                val language = uri.getQueryParameter("language") ?: ""
                val country = uri.getQueryParameter("country") ?: ""
                val variant = uri.getQueryParameter("variant") ?: ""
                val local : Locale? = if (language != "" && country != "" && variant != "") {
                    Locale(language, country, variant)
                } else if (language != "" && country != "") {
                    Locale(language, country)
                } else if (language != ""){
                    Locale(language)
                } else {
                    null
                }
                local?.let { Regions.setLocale(it) }
            }
        }

        initSections()
        setupFlows()

        viewModel.checkPermission(this)
        viewModel.getFavorites(this)
    }

    private fun initSections() {
        initSupportActionBar()
        initViewPage()
        initStateSectionPermissionBarView()
    }

    private fun initViewPage() {
        regionsCollectionAdapter = RegionsCollectionAdapter(supportFragmentManager, lifecycle)
        viewPager = binding.pager
        viewPager.adapter = regionsCollectionAdapter
        TabLayoutMediator(binding.tabs, viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.all)
                1 -> getString(R.string.favorite)
                else -> ""
            }
        }.attach()
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(onSearchQueryTextListener)
    }

    private fun initSupportActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun initStateSectionPermissionBarView() {
        binding.tips.setOnClickListener {
            viewModel.showPermissionRequiredDialog(true)
        }
    }

    private fun setupFlows() {
        collectOnLifecycle(viewModel.viewStateFlow) { renderState(it) }
        collectOnLifecycle(viewModel.viewEventFlow) { onEvent(it) }
    }

    private fun onEvent(event: RegionsViewEvent) {
        when(event) {
            is RegionsViewEvent.ShowPermissionRequiredDialog -> {
                if (event.show) {
                    permissionRequiredDialog.show()
                } else {
                    permissionRequiredDialog.dismiss()
                }
            }
        }
    }

    private fun renderState(state: RegionsViewState) {
        Log.d(TAG, "render state")
        updateStateSectionPermissionBarView(state.permissionGranted)
    }

    private fun updateStateSectionPermissionBarView(permissionGranted: Boolean) {
        if (permissionGranted) {
            binding.permissionBarView.isVisible = false
            viewModel.showPermissionRequiredDialog(false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            menuInflater.inflate(R.menu.options_menu, it)
            searchView = it.findItem(R.id.search).actionView as SearchView
            initSearchView()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.how_to_do -> {
                viewModel.showPermissionRequiredDialog(true)
                true
            }
            R.id.license -> {
                startActivity(Intent(this, LicenseActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}