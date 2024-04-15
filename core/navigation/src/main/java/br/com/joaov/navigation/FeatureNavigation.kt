package br.com.joaov.navigation

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FeatureNavigation {

    private val _sharedFlow = MutableSharedFlow<Feature>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val sharedFlow = _sharedFlow.asSharedFlow()

    fun navigateTo(navTarget: Feature) {
        _sharedFlow.tryEmit(navTarget)
    }
}