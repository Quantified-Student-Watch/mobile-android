package nl.quantifiedstudent.watch.binary

import java.nio.ByteBuffer

abstract class BinaryConverter<T> {

    abstract fun read(clazz: Class<T>, buffer: ByteBuffer): T

    abstract fun write(value: T, buffer: ByteBuffer)

}