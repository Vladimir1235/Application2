package dev.vvasiliev.application

import dev.vvasiliev.structures.monad.LazyMonad
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val lazyMonad = LazyMonad.buildFrom("Initiallize").doNext {
            it.plus("\n Action first")
        }.doNext {
            it.plus("\n Action second")
        }.doNext {
            println(it)
        }


        lazyMonad.perform()

        assertEquals(4, 2 + 2)
    }
}