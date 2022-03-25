package nl.quantifiedstudent.watch.protocol.checksum

interface Crc {
    fun calculate(
        poly: Int,
        init: Int,
        data: ByteArray,
        offset: Int,
        length: Int,
        refIn: Boolean,
        refOut: Boolean,
        xorOut: Int
    ): Int
}