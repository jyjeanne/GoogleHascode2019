package com.github.jacklt.hashcode

import com.github.jacklt.hashcode.model.Orientation
import com.github.jacklt.hashcode.solvers.GreedySlideshowBuilder
import com.github.jacklt.hashcode.utils.io.OutputGenerator
import com.github.jacklt.hashcode.utils.io.PhotoParser
import kotlinx.coroutines.*
import java.io.File
import kotlin.system.measureTimeMillis


fun main(args: Array<String>) {

    //args.forEach { println(it) }

    if (args.size == 1) {
        val filename = args[0]
        println("treat only one file : $filename.txt")
        val file = File("in/$filename.txt")
        val parser = PhotoParser(file.readText())
        val photos = parser.parse()

        // Split photos into horizontal and vertical
        val (horizontalPhotos, verticalPhotos) = photos.partition { it.orientation == Orientation.HORIZONTAL}

        val slideshow = GreedySlideshowBuilder(horizontalPhotos, verticalPhotos).build()

        // Do output generation
        val outputGenerator = OutputGenerator(slideshow, args[0])
        outputGenerator.generateOutput()

        println("Finished.")
    }
    else
    {
        println("treat all files")

    measureTimeMillis {
        runBlocking {
            val filelist = listOf("a_example", "b_lovely_landscapes", "c_memorable_moments", "d_pet_pictures", "e_shiny_selfies")
            filelist.slice(0..(filelist.size-1)).forEach {

                App.solveForDataSet(inputFile = File("in/$it.txt")).join()
            }
        }
    }.also { println("Completed in ${it}ms") }
    }
}

object App : CoroutineScope {
    const val DEBUG = true
    val job = Job()
    override val coroutineContext = job

    fun solveForDataSet(inputFile: File) = launch {

        println("File in treatment : "+inputFile)

        // Data extraction
        val parser = PhotoParser(inputFile.readText())
        val photos = parser.parse()

        // Split photos into horizontal and vertical
        val (horizontalPhotos, verticalPhotos) = photos.partition { it.orientation == Orientation.HORIZONTAL}

        val slideshow  = async {

            GreedySlideshowBuilder(horizontalPhotos, verticalPhotos).build()
        }

        if (DEBUG) println("Solution: ${slideshow.await()}()")

        // Do output generation
        val outputGenerator = OutputGenerator(slideshow.await(), inputFile.name)
            outputGenerator.generateOutput()

        println("Finished.")


    }
}


