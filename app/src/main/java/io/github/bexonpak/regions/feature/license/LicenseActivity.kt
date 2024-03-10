package io.github.bexonpak.regions.feature.license

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.bexonpak.regions.databinding.ActivityLicenseBinding

class LicenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLicenseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
    }
}