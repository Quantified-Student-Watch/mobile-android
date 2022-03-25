package nl.quantifiedstudent.watch.protocol.huawei.converters

import nl.quantifiedstudent.watch.binary.BinaryConverter
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiLinkCommand
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiLinkCommandTLV
import java.nio.ByteBuffer

class HuaweiLinkCommandBinaryConverter : BinaryConverter<HuaweiLinkCommand>() {
    private val tlvConverter = HuaweiLinkCommandTLVBinaryConverter()

    override fun read(clazz: Class<HuaweiLinkCommand>, buffer: ByteBuffer): HuaweiLinkCommand {
        val serviceId = buffer.get()
        val commandId = buffer.get()

        val tlvs = mutableListOf<HuaweiLinkCommandTLV>()
        while (buffer.remaining() > 2) {
            val tlv = tlvConverter.read(HuaweiLinkCommandTLV::class.java, buffer)
            tlvs.add(tlv)
        }

        return HuaweiLinkCommand(serviceId, commandId, tlvs.toTypedArray())
    }

    override fun write(value: HuaweiLinkCommand, buffer: ByteBuffer) {
        buffer.put(value.serviceId)
        buffer.put(value.commandId)

        value.tlvs.forEach { tlvConverter.write(it, buffer) }
    }
}