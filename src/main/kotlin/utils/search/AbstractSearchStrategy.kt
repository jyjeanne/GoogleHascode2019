package com.github.jacklt.hashcode.utils.search

abstract class AbstractSearchStrategy<T> {
    abstract fun perform(arr: Array<T>, element: T): Int
}