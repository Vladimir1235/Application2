package dev.vvasiliev.structures.monad

import java.util.function.Supplier

open class Monad<ErasedType>(protected val value: ErasedType) {
    companion object {
        fun <Type> buildFrom(value: Type) = Monad(value)
    }

    fun <ReturnType> map(transformation: (ErasedType) -> ReturnType): Monad<ReturnType> =
        buildFrom(transformation(value))
}

class LazyMonad<ErasedType> constructor(
    value: ErasedType? = null,
    private var supplier: Supplier<ErasedType>? = null
) : Monad<ErasedType?>(value) {

    companion object {
        fun <AnyType> buildFrom(value: AnyType) = LazyMonad(value = value)
    }

    fun <ReturnType> doNext(transformation: (ErasedType) -> ReturnType): LazyMonad<ReturnType> {
        val newSupplier: Supplier<ReturnType> = Supplier {
            value?.let(transformation)?: supplier?.get()?.let(transformation)!!
        }

        return LazyMonad(newSupplier.get())
    }

    fun perform(): ErasedType = value ?: supplier?.get()!!

}