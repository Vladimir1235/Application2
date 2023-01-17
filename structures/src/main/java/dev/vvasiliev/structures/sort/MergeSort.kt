package dev.vvasiliev.structures.sort

class MergeSort {

    val arrayExample = Array(1929999) { index ->
        (index * Math.pow(-index.toDouble(), index.toDouble())).toInt()
    }.apply { shuffle() }

    fun Array<Int>.split(): List<Array<Int>> {
        if (size > 1) {
            val middle = if (size % 2 > 0) (size / 2) + 1 else (size / 2)
            return listOf(copyOfRange(0, middle), copyOfRange(middle, size))
        } else throw Exception("can not split anymore")
    }

    fun Array<Int>.print() {
        print("[")
        this.forEachIndexed { index, i ->
            print("$i ")
        }
        print("] ")
    }

    infix fun Array<Int>.merge(array: Array<Int>): Array<Int> {
        return arrayOf(*this, *array).apply { sort() }
    }

    val splittedArrays = mutableListOf<Array<Int>>()
    var mergedArrays = mutableListOf<Array<Int>>()

    fun sort(array: Array<Int>): MutableList<Array<Int>> {
        mergeSort(*splittedArrays.toTypedArray())
        return mergedArrays
    }

    fun recursiveSort(array: Array<Int>) {
        if (array.size > 1) {
            array.split().iterator().run {
                while (hasNext())
                        recursiveSort(this@run.next())
            }
        } else {
            splittedArrays.add(array)
        }
    }

    private fun mergeSort(vararg arrays: Array<Int>) {
        if (arrays.size > 1) {
            val newArray = mutableListOf<Array<Int>>()
            val iterator = arrays.iterator()
            while (iterator.hasNext()) {
                newArray.add(iterator.run {
                    val left = next()
                    if (hasNext()) {
                        val right = next()
                        left merge right
                    } else left
                })
            }
            mergeSort(*newArray.toTypedArray())
        } else {
            mergedArrays = arrays.toMutableList()
        }
    }
}