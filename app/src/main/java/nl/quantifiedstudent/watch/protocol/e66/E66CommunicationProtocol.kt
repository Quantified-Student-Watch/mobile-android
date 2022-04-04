package nl.quantifiedstudent.watch.protocol.e66

import nl.quantifiedstudent.watch.protocol.CommunicationProtocol
import nl.quantifiedstudent.watch.protocol.ProtocolCommand
import nl.quantifiedstudent.watch.protocol.checksum.Crc
import nl.quantifiedstudent.watch.protocol.checksum.Crc16

@Deprecated("Belongs to legacy protocol architecture")
class E66CommunicationProtocol : CommunicationProtocol {
    private val checksum: Crc = Crc16()

    override fun prepareMessage(command: ProtocolCommand, data: ByteArray): ByteArray {
        if (data.size > command.parameterCount) {
            throw IllegalArgumentException("$command does not support more than ${command.parameterCount} parameters")
        }

        return appendChecksum(command.bytes.plus(data))
    }

    private fun appendChecksum(bytes: ByteArray): ByteArray {
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

        copy[bytes.size] = crc.toByte()
        copy[bytes.size + 1] = (crc shr 8).toByte()

        return copy
    }

    companion object {
        private const val Polynomial = 0x1021
        private const val Initial = 0xffff
        private const val XorOut = 0x0000
    }
}