package io.github.bexonpak.regions.customui

import android.content.Context
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.bexonpak.regions.R
import io.github.bexonpak.regions.databinding.DialogPermissionRequiredBinding

class PermissionRequiredDialog(context: Context) {

    private val dialog: AlertDialog

    private val binding: DialogPermissionRequiredBinding by lazy {
        DialogPermissionRequiredBinding.inflate(
            LayoutInflater.from(context)
        )
    }

    init {
        binding.permissionRequiredDescription.setTextIsSelectable(true)
        binding.permissionRequiredDescription.movementMethod = LinkMovementMethod.getInstance()
        dialog = MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
            .setView(binding.root)
            .create()
        dialog.setTitle(R.string.permissionRequired)

    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}