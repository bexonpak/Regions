package io.github.bexonpak.regions.feature.regions

import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import io.github.bexonpak.regions.R
import io.github.bexonpak.regions.databinding.ItemLocaleBinding
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LocaleListAdapter(
    private val inflater: LayoutInflater,
    private val coroutineScope: LifecycleCoroutineScope,
    private val onFavorite: (locale: Locale, favorite: Boolean) -> Unit,
    private val onLocaleSelected: (locale: Locale) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    var localeList: Array<Locale> = emptyArray()
    var favoriteList: Array<Locale> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LocaleViewHolder(
            inflater.inflate(R.layout.item_locale, parent, false),
            coroutineScope,
            onLocaleSelected
        )
    }

    override fun getItemCount(): Int = localeList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LocaleViewHolder) {
            val isFavorite = favoriteList.contains(localeList[position])
            holder.bind(localeList[position], isFavorite, onFavorite)
        }
    }

    class LocaleViewHolder(
        itemView: View,
        private val coroutineScope: LifecycleCoroutineScope,
        private val onLocaleSelected: (locale: Locale) -> Unit,
    ) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemLocaleBinding.bind(itemView)

        fun bind(locale: Locale, isFavorite: Boolean, onFavorite: (locale: Locale, favorite: Boolean) -> Unit) {
            binding.name.text = locale.displayName
            binding.code.text = locale.toString()
            binding.root.setOnClickListener {
                coroutineScope.launch {
                    onLocaleSelected(locale)
                }
            }
            binding.materialButton.setOnClickListener {
                coroutineScope.launch {
                    onFavorite.invoke(locale, isFavorite)
                }
            }
            if (isFavorite) {
                binding.materialButton.icon = AppCompatResources.getDrawable(itemView.context, R.drawable.twotone_star_24_yellow)
                binding.materialButton.iconTint = ColorStateList.valueOf(
                    ContextCompat.getColor(itemView.context, R.color.yellow)
                )
            } else {
                binding.materialButton.icon = AppCompatResources.getDrawable(itemView.context, R.drawable.twotone_star_24)
            }
        }

    }
}