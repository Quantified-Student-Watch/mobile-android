package nl.quantifiedstudent.watch.extensions

infix fun Byte.shr(bitCount: Int): Byte = (this.toInt() shr bitCount).toByte()

infix fun Byte.shl(bitCount: Int): Byte = (this.toInt() shl bitCount).toByte()

infix fun UByte.shr(bitCount: Int): UByte = (this.toUInt() shr bitCount).toUByte()

infix fun UByte.shl(bitCount: Int): UByte = (this.toUInt() shl bitCount).toUByte()

infix fun UShort.shr(bitCount: Int): UShort = (this.toUInt() shr bitCount).toUShort()

infix fun UShort.shl(bitCount: Int): UShort = (this.toUInt() shl bitCount).toUShort()