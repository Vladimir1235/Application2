package dev.vvasiliev.structures

import dev.vvasiliev.structures.sort.MergeSort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test

import org.junit.Assert.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @OptIn(ExperimentalTime::class)
    @Test
    fun addition_isCorrect() {

        val merger = MergeSort()
        merger.run {
            recursiveSort(arrayExample)
            val duration = measureTime {
                    sort(arrayExample).toTypedArray()[0]
            }
            val duration2 = measureTime {
                arrayExample.sort()
            }
            print("\n Time: ${duration.inWholeSeconds} seconds; Time2: ${duration2.inWholeSeconds}\n")
        }
        assertEquals(4, 2 + 2)
    }
}