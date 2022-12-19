package dev.vvasiliev.application.core.di.viewmodel

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

// While METHOD is the only valid target for Dagger, FIELD was added to support Hilt's
// @BindValueIntoMap and TYPE was added to support external extension types since it likely won't
// cause confusion/maintenance issues as this isn't part of Dagger's core API.
// See discussion on https://github.com/google/dagger/pull/2831#issuecomment-919417457 for details.
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)
