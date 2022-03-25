package nl.quantifiedstudent.watch.extensions

import java.nio.ByteBuffer

fun ByteBuffer.array(length: Int): ByteArray {
    return this.array().take(length).toByteArray()
}