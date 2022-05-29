package ru.spbsu.kotlin

import java.io.File
import java.io.OutputStream

class EncodeStream (private val rootFolder: File, private val password: String): OutputStream() {
    init {
        if (!rootFolder.isDirectory || password.isEmpty() || password.any { !it.isDigit() || it == '0'}) {
            throw java.lang.IllegalArgumentException()
        }
    }
    private fun fileName(x: Int): String {
        return "${rootFolder.path}${File.separator}${x.toString().reversed().map { 'a' + it.digitToInt() }[0]}"
    }
    private val files = List(password.length + 2) {
        File(fileName(it)).let { file ->
            file.createNewFile()
            file.setLastModified((file.lastModified() / 60000 * 60 +
                    if (it >= password.length) 0 else {
                        password[it].digitToInt()
                    }) * 1000)
            file
        }
    }
    private val stream = files.last().outputStream()
    var messageLength = 0
    override fun write(b: Int) {
        stream.write(b xor password[messageLength++ % password.length].digitToInt())
    }
}