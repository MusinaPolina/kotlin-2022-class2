package ru.spbsu.kotlin

import java.io.File
import java.io.InputStream

fun File.dfs(): List<File> {
    if (this.isFile)
        return listOf(this)
    return this.listFiles()!!
        .sortedWith(compareBy<File> { it.isFile }.thenBy { it.name }).flatMap {it.dfs()}
}

class DecodeStream(rootFolder: File) : InputStream() {
    private val files = rootFolder.dfs().filter { it.isFile }
    private val password = files.map { it.lastModified() / 1000 % 10 }.joinToString("").substringBefore("0").map { it.digitToInt() }

    private val message = files.flatMap { it.readBytes().toList() }.map { it.toUByte().toInt() }
    private var messageLength = 0

    override fun read(): Int {
        if (messageLength == message.size) return -1
        return message[messageLength] xor password[messageLength++ % password.size]
    }
}