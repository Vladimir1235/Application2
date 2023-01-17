package dev.vvasiliev.structures

import android.graphics.Point
import dev.vvasiliev.structures.sort.MergeSort
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import org.junit.Assert.*
import org.junit.Test
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
            val secondArray = arrayExample.clone()
            println("unsorted ${arrayExample.copyOfRange(60, 70).toList()}")
            println("unsorted2 ${secondArray.copyOfRange(60, 70).toList()}")
            val duration = measureTime {
                recursiveSort(arrayExample)
                println("sorted 1" + sort(arrayExample).first().copyOfRange(60, 70).toList())
            }
            val duration2 = measureTime {
                secondArray.sort()
                println("sorted 2" + secondArray.copyOfRange(60, 70).toList())
            }
            print("\n Time: ${duration.inWholeSeconds} seconds; Time2: ${duration2.inWholeSeconds}\n seconds")
        }
        assertEquals(4, 2 + 2)
    }

    data class Point(val x: Int, val y: Int)

    @Test
    fun coroutine() {
        runBlocking {
            val context = Dispatchers.Default
            val scope = CoroutineScope(context)
            val handler = CoroutineExceptionHandler { coroutineContext, throwable ->

            }
            val job = scope.launch {
                repeat(10) {
                    delay(100)
                    println("job1 working")
                }
            }
            val job2 = scope.launch(handler) {
                repeat(10) {
                    delay(100)
                    println("job2 working")
                }
            }
            job.cancel()
            job.join()
            job2.join()
            println("main coroutine job: ${scope.coroutineContext.job}\n $job \n$job2")
            scope.coroutineContext.job.join()
        }
    }

    @Test
    fun main() {
        val key1 = "one"
        var key2 = "two"
        val map: HashMap<String, Point> = hashMapOf(
            Pair(key1, Point(0, 0)),
            Pair(key2, Point(0, 0)),
            Pair("three", Point(0, 0)),
            Pair("four", Point(0, 0)),
            Pair("five", Point(0, 0))
        )
        println(map)
    }

    @Test
    fun variance() {
        val myObject = object : MyType<String> {
            override var value: String = "A"
        }
        println("value is " + myObject.readValue())
        myObject.writeValue("new value")
        println("value is " + myObject.readValue())
    }
}

interface MyType<Type> {
    var value: Type
    fun readValue(): Type = value
    fun writeValue(newValue: Type) {
        value = newValue
    }
}