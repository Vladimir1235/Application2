package dev.vvasiliev.application.core.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class ViewModelInjection @Inject constructor(
    private val viewModelFactories: Map<
            Class<out ViewModel>,
            @JvmSuppressWildcards
            Provider<ViewModel>
            >
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val returnValue = viewModelFactories.getValue(modelClass).get()
        return returnValue as T
    }
}