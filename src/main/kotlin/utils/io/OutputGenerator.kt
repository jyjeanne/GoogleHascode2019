package com.github.jacklt.hashcode.utils.io

import com.github.jacklt.hashcode.model.DoubleSlide
import com.github.jacklt.hashcode.model.SingleSlide
import com.github.jacklt.hashcode.model.Slide
import com.github.jacklt.hashcode.model.Slideshow
import java.io.File
import java.nio.file.Paths

class OutputGenerator(val slideshow: Slideshow, filename: String) {

    val targetFilename = "out/"+File(filename).nameWithoutExtension + "_out.txt"
    val outputFile = File(Paths.get("", targetFilename).toAbsolutePath().toString())

    fun generateOutput() {
        outputFile.writeText(slideshow.slides.size.toString(10) + "\n")
        slideshow.slides.forEach { outputFile.appendText(generateLineForSlide(it)) }
    }

    private fun generateLineForSlide(slide: Slide) : String {
        return when(slide) {
            is SingleSlide -> slide.photo.id.toString(10) + "\n"
            is DoubleSlide -> slide.photo1.id.toString(10) + " " + slide.photo2.id.toString(10) + "\n"
            else -> "\n"
        }
    }
}
