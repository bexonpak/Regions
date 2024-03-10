package io.github.bexonpak.regions.helper

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T> AppCompatActivity.collectOnLifecycle(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    flowCollector: FlowCollector<T>
): Job {
    return lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(state) {
            flow.collect(flowCollector)
        }
    }
}

fun <T> Fragment.collectOnLifecycle(
    flow: Flow<T>,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    flowCollector: FlowCollector<T>
) {
    viewLifecycleOwner.lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(state) {
            flow.collect(flowCollector)
        }
    }
}