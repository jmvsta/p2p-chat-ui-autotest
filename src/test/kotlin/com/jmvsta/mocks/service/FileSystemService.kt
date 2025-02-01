package com.jmvsta.mocks.service

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.NotDirectoryException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class FileSystemService {

    private val baseUrlRegex = "\"(ws|http|https)://([a-z]+\\.?)+:[0-9]{3,5}\"".toRegex()

    fun extractZipFile(inputStream: InputStream, staticScratchFolder: String) {
        val targetFolder = File(staticScratchFolder)
        if (!targetFolder.exists()) targetFolder.mkdirs()

        ZipInputStream(inputStream).use { zipStream ->
            var entry: ZipEntry?
            while (zipStream.nextEntry.also { entry = it } != null) {
                if (!entry!!.isDirectory) {
                    val split = entry!!.name.split("/")
                    val entryName = split.last()
                    val folderPath = split.drop(1).dropLast(1).joinToString("/")

                    val outputDir = File(staticScratchFolder + folderPath)
                    if (!outputDir.exists()) outputDir.mkdir()

                    val outputFile = File(outputDir, entryName)
                    FileOutputStream(outputFile).use { output -> zipStream.copyTo(output) }
                    println("Extracted: $entryName to ${outputFile.absolutePath}")
                }
            }
        }
    }

    fun findFile(directoryPath: String, regex: String): File? {
        val directory = File(directoryPath)
        if (!directory.exists() || !directory.isDirectory) {
            println("Directory does not exist: $directoryPath")
            return null
        }

        return directory.walkTopDown()
            .filter { it.isFile && it.name.matches(regex.toRegex()) }
            .firstOrNull()
    }

    fun createStatic(url: String) {
        val suffix = url.replace("[:/.]".toRegex(), "")
        val targetDir = File("build/resources/test/static$suffix")
        if (!targetDir.exists()) {
            targetDir.mkdir()
        }
        val sourceDir = File("build/static")
        if (!sourceDir.exists()) {
            targetDir.mkdir()
        }
        sourceDir.listFiles()?.forEach { file ->
            file.copyRecursively(File(targetDir, file.name), overwrite = true)
        }
        val jsFile = findFile("build/resources/test/static$suffix/", ".+\\.js")
        if (!jsFile!!.exists()) {
            throw NotDirectoryException("no js file in build/resources/test/static$suffix/")
        }
        val content = jsFile.readText().replace(baseUrlRegex, url)
        jsFile.writeText(content)
    }
}