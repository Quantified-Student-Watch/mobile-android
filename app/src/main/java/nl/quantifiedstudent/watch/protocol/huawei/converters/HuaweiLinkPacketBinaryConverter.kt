package nl.quantifiedstudent.watch.protocol.huawei.converters

import nl.quantifiedstudent.watch.binary.BinaryConverter
import nl.quantifiedstudent.watch.protocol.checksum.Crc16
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiLinkCommand
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiLinkPacket
import java.nio.ByteBuffer

class HuaweiLinkPacketBinaryConverter : BinaryConverter<HuaweiLinkPacket>() {
    private val commandConverter = HuaweiLinkCommandBinaryConverter()
    private val crc = Crc16()

    override fun read(clazz: Class<HuaweiLinkPacket>, buffer: ByteBuffer): HuaweiLinkPacket {
        buffer.position(1)
        val length = buffer.getShort(1)
        buffer.position(4)

        val command = commandConverter.read(HuaweiLinkCommand::class.java, buffer)
        val checksum = buffer.short

        //TODO: Validate checksum

        return HuaweiLinkPacket(command)
    }

    override fun write(value: HuaweiLinkPacket, buffer: ByteBuffer) {
        buffer.put(HuaweiLinkPacket.Magic)
        buffer.position(4)

        commandConverter.write(value.command, buffer)

        val finalPosition = buffer.position()
        val length: Short = (buffer.position() - 3).toShort()
        buffer.position(1)
        buffer.putShort(length)
        buffer.put(0)

        val checksum = crc.calculate(Polynomial, Initial, buffer.array(), 0, finalPosition)

        buffer.position(finalPosition)
        buffer.putShort(checksum.toShort())
    }

    companion object {
        const val Polynomial: Int = 4129
        const val Initial: Int = 0
    }
}