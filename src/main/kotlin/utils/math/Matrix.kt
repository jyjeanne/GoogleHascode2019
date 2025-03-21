package com.github.jacklt.hashcode.utils

import java.util.ArrayList

/*

       export from : https://github.com/yinpeng/kotlin-matrix

       This also allows you to create 2d arrays in a similar manner to 1d arrays, e.g. something like

       // Use literals
        val m1 = matrixOf(  4, 3,         // numbers of columns and rows
                            3, 5, 6, 9,     // elements in the matrix
                            8, 8, 2, 4,
                            0, 5, 5, 8
        )

        // By lambdas
        val m2 = createMatrix(4, 3) { x, y -> "$x-$y" }

        // From Iterable
        val m3 = (1..100).toMatrix(20, 5)

        // Mutable matrices
        val m4 = mutableMatrixOf(2, 2, 1.5, 2.3, 4.4, 3.6)
        val m5 = createMutableMatrix(100, 100) { x, y -> x * y }
        val m6 = ('A'..'Z').toMutableMatrix(2, 13)

       Access elements in a matrix:

        val m = (1..12).toMutableMatrix(4, 3)
        val a = m[0, 0]         // index start from 0
        m[2, 2] = 5             // change an element of the mutable matrix

       matrice[1, 2] = 5
 */


interface Matrix<out T> {
    val cols: Int
    val rows: Int

    operator fun get(x: Int, y: Int): T
}

val <T> Matrix<T>.size: Int
    get() = this.cols * this.rows

interface MutableMatrix<T>: Matrix<T> {
    operator fun set(x: Int, y: Int, value: T)
}

abstract class AbstractMatrix<out T>: Matrix<T> {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('[')
        forEachIndexed { x, y, value ->
            if (x === 0)
                sb.append('[')
            sb.append(value.toString())
            if (x===cols-1) {
                sb.append(']')
                if (y < rows-1)
                    sb.append(", ")
            } else {
                sb.append(", ")
            }
        }
        sb.append(']')
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix<*>) return false
        if (rows !== other.rows || cols !== other.cols) return false

        var eq = true
        forEachIndexed { x, y, value ->
            if (value === null) {
                if (other[x, y] !== null) {
                    eq = false
                    return@forEachIndexed
                }
            } else {
                if (!value.equals(other[x, y])) {
                    eq = false
                    return@forEachIndexed
                }
            }
        }
        return eq
    }

    override fun hashCode(): Int {
        var h = 17
        h = h * 39 + cols
        h = h * 39 + rows
        forEach { h = h * 37 + (it?.hashCode() ?: 1)}
        return h
    }
}

internal open class TransposedMatrix<out T>(protected val original: Matrix<T>): AbstractMatrix<T>() {
    override val cols: Int
        get() = original.rows

    override val rows: Int
        get() = original.cols

    override fun get(x: Int, y: Int): T = original[y, x]
}

internal class TransposedMutableMatrix<T>(original: MutableMatrix<T>) :
        TransposedMatrix<T>(original), MutableMatrix<T> {
    override fun set(x: Int, y: Int, value: T) {
        (original as MutableMatrix<T>)[y, x] = value
    }
}

fun <T> Matrix<T>.asTransposed() : Matrix<T> = TransposedMatrix(this)

fun <T> MutableMatrix<T>.asTransposed(): MutableMatrix<T> = TransposedMutableMatrix(this)

internal open class ListMatrix<out T>(override val cols: Int, override val rows: Int,
                                      protected val list: List<T>) :
        AbstractMatrix<T>() {
    override operator fun get(x: Int, y: Int): T = list[y*cols+x]
}

internal class MutableListMatrix<T>(cols: Int, rows: Int, list: MutableList<T>):
        ListMatrix<T>(cols, rows, list), MutableMatrix<T> {
    override fun set(x: Int, y: Int, value: T) {
        (list as MutableList<T>)[y*cols+x] = value
    }
}

fun <T> matrixOf(cols: Int, rows: Int, vararg elements: T): Matrix<T> {
    return ListMatrix(cols, rows, elements.asList())
}

fun <T> mutableMatrixOf(cols: Int, rows: Int, vararg elements: T): MutableMatrix<T> {
    return MutableListMatrix(cols, rows, elements.toMutableList())
}

inline private fun <T> prepareListForMatrix(cols: Int, rows: Int, init: (Int, Int) -> T): ArrayList<T> {
    val list = ArrayList<T>(cols * rows)
    for (y in 0..rows - 1) {
        for (x in 0..cols - 1) {
            list.add(init(x, y))
        }
    }
    return list
}

@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <T> createMatrix(cols: Int, rows: Int, init: (Int, Int) -> T): Matrix<T> {
    return ListMatrix(cols, rows, prepareListForMatrix(cols, rows, init))
}

@Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
inline fun <T> createMutableMatrix(cols: Int, rows: Int, init: (Int, Int) -> T): MutableMatrix<T> {
    return MutableListMatrix(cols, rows, prepareListForMatrix(cols, rows, init))
}

inline fun <T, U> Matrix<T>.mapIndexed(transform: (Int, Int, T) -> U): Matrix<U> {
    return createMatrix(cols, rows) { x, y -> transform(x, y, this[x, y]) }
}

inline fun <T, U> Matrix<T>.map(transform: (T) -> U): Matrix<U> {
    return mapIndexed { x, y, value -> transform(value) }
}

inline fun <T> Matrix<T>.forEachIndexed(action: (Int, Int, T) -> Unit): Unit {
    for (y in 0..rows-1) {
        for (x in 0..cols-1) {
            action(x, y, this[x, y])
        }
    }
}

inline fun <T> Matrix<T>.forEach(action: (T) -> Unit): Unit {
    forEachIndexed { x, y, value -> action(value) }
}

fun <T> Matrix<T>.toList(): List<T> {
    return prepareListForMatrix(cols, rows, { x, y -> this[x, y] })
}

fun <T> Matrix<T>.toMutableList(): MutableList<T> {
    return prepareListForMatrix(cols, rows, { x, y -> this[x, y] })
}

private fun <T> Iterable<T>.toArrayList(size: Int): ArrayList<T> {
    val list = ArrayList<T>(size)
    val itr = iterator()

    for (i in 0..size - 1) {
        if (itr.hasNext()) {
            list.add(itr.next())
        } else {
            throw IllegalArgumentException("No enough elements")
        }
    }
    return list
}

fun <T> Iterable<T>.toMatrix(cols: Int, rows: Int): Matrix<T> {
    val list = toArrayList(cols * rows)
    return ListMatrix(cols, rows, list)
}

fun <T> Iterable<T>.toMutableMatrix(cols: Int, rows: Int): MutableMatrix<T> {
    val list = toArrayList(cols * rows)
    return MutableListMatrix(cols, rows, list)
}