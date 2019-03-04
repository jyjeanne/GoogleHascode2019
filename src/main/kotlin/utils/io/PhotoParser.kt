package com.github.jacklt.hashcode.utils.io

import com.github.jacklt.hashcode.model.Orientation
import com.github.jacklt.hashcode.model.Photo

class PhotoParser(val input: String) {

    var id = 0

    fun parse(): List<Photo> {
        val lines = this.input.lines()
        // read first line with N

        val numberOfPhotos = lines[0].toInt()

        println("Number of photos: $numberOfPhotos")

        // [H|V] <n_tags> <space separated tags>
        return lines
                .slice(1..(lines.size - 2))
                .map { lineToPhoto(it) }
    }

    private fun lineToPhoto(line: String): Photo {
        val splitLine = line.split(" ")
        val orientation = if (splitLine[0] == "H") Orientation.HORIZONTAL else Orientation.VERTICAL
        val numberOfTags = splitLine[1].toInt()
        val tags = splitLine.slice(2..numberOfTags).toSet()
        return Photo(id++, orientation, tags)
    }
}