package dev.vvasiliev.audio.service.state.holder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

open class StateHolder<Type>(
    private val value: MutableSharedFlow<Type>,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    fun set(value: Type) {
        coroutineScope.launch {
            this@StateHolder.value.emit(value)
        }
    }
}
