package nl.quantifiedstudent.watch.protocol.checksum

@ExperimentalUnsignedTypes
class Crc16 : Crc {
    override fun calculate(
        poly: UInt,
        init: UInt,
        data: UByteArray,
        offset: Int,
        length: Int,
        refIn: Boolean,
        refOut: Boolean,
        xorOut: UInt
    ): UInt {
        var crc = init
        var i = offset

        while (i < offset + length && i < data.size) {
            val b = data[i]

            for (j in 0..7) {
                val k = if (refIn) 7 - j else j
                val bit = b.toInt() shr 7 - k and 1 == 1
                val c15 = crc shr 15 and 1u == 1u
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor poly
            }

            ++i
        }
        return when {
            refOut -> Integer.reverse(crc.toInt()).toUInt() shr 16 xor xorOut
            else -> crc xor xorOut and 0xFFFFu
        }
    }
}