package nl.quantifiedstudent.watch.binary

import java.nio.ByteBuffer

fun ByteBuffer.getVarUInt(): UInt {
    var result = 0U

    if (!hasRemaining()) {
        return result
    }

    (0 until 5 * 7 step 7).forEach { shift ->
        val current = get().toUInt()
        result = result or current.and(127U).shl(shift)

        if ((current and 128U) != 128U || !hasRemaining()) {
            return result
        }
    }

    throw IllegalArgumentException("VarInt input too big")
}

fun ByteBuffer.putVarUInt(value: UInt) {
    var currentValue = value

    repeat(5) {
        val maskedValue = currentValue.and(0x7fU).toInt()
        currentValue = currentValue.shr(7)
        if (currentValue == 0U) {
            put(maskedValue.toByte())
        } else {
            put(maskedValue.or(0x80).toByte())
        }
    }
}