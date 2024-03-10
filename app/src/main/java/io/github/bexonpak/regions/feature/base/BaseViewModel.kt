package io.github.bexonpak.regions.feature.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<ViewState, ViewEvent>(initialViewState: ViewState) : ViewModel() {
    private val _viewStateFlow: MutableStateFlow<ViewState> = MutableStateFlow(initialViewState)
    val viewStateFlow = _viewStateFlow.asStateFlow()

    private val _viewEventFlow: MutableSharedFlow<ViewEvent> = MutableSharedFlow()
    val viewEventFlow = _viewEventFlow.asSharedFlow()

    private var viewEventFlowValve: CompletableDeferred<Unit> = CompletableDeferred()

    init {
        _viewEventFlow.subscriptionCount
            .map { it > 0 }
            .distinctUntilChanged()
            .onEach {
                if (it) {
                    viewEventFlowValve.complete(Unit)
                } else {
                    viewEventFlowValve = CompletableDeferred()
                }
            }
            .launchIn(viewModelScope)
    }

    protected fun updateViewState(updateFunction: (ViewState) -> ViewState) {
        _viewStateFlow.update {
            updateFunction(it)
        }
    }

    protected suspend fun sendViewEvent(event: ViewEvent) {
        viewEventFlowValve.await()
        _viewEventFlow.emit(event)
    }

}
