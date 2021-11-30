package nl.quantifiedstudent.watch.protocol.checksum

@ExperimentalUnsignedTypes
interface Crc {
   fun calculate(
        poly: UInt,
        init: UInt,
        data: UByteArray,
        offset: Int,
        length: Int,
        refIn: Boolean,
        refOut: Boolean,
        xorOut: UInt
    ): UInt
}