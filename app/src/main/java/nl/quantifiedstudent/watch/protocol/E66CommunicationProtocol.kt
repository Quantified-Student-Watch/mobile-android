package nl.quantifiedstudent.watch.protocol

import nl.quantifiedstudent.watch.protocol.checksum.Crc
import nl.quantifiedstudent.watch.protocol.checksum.Crc16

@ExperimentalUnsignedTypes
class E66CommunicationProtocol : CommunicationProtocol {
    private val checksum: Crc = Crc16()

    override fun prepareMessage(command: ProtocolCommand, data: UByteArray) : UByteArray {
        if (data.size > command.parameterCount) {
            throw IllegalArgumentException("$command does not support more than ${command.parameterCount} parameters")
        }

        return appendChecksum(command.bytes.plus(data))
    }

    private fun appendChecksum(bytes: UByteArray): UByteArray {
        val crc = checksum.calculate(
            Polynomial,
            Initial,
            bytes,
            0,
            bytes.size,
            refIn = false,
            refOut = false,
            xorOut = XorOut
        )

        val copy = bytes.copyOf(bytes.size + 2)

        copy[bytes.size] = crc.toUByte()
        copy[bytes.size + 1] = (crc shr 8).toUByte()

        return copy
    }

    companion object {
        private const val Polynomial = 0x1021u
        private const val Initial = 0xffffu
        private const val XorOut = 0x0000u
    }
}