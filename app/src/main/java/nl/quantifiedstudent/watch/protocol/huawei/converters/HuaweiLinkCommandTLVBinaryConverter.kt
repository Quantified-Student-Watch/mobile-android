package nl.quantifiedstudent.watch.protocol.huawei.converters

import nl.quantifiedstudent.watch.binary.BinaryConverter
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiLinkCommandTLV
import java.nio.ByteBuffer

class HuaweiLinkCommandTLVBinaryConverter : BinaryConverter<HuaweiLinkCommandTLV>() {
    override fun read(clazz: Class<HuaweiLinkCommandTLV>, buffer: ByteBuffer): HuaweiLinkCommandTLV {
        val tag = buffer.get()
        val length = buffer.get() //TODO: Read VarInt

        val data = ByteArray(length.toInt())
        buffer.get(data)

        return HuaweiLinkCommandTLV(tag, data)
    }

    override fun write(value: HuaweiLinkCommandTLV, buffer: ByteBuffer) {
        buffer.put(value.tag)

        val data = value.value

        buffer.put(data.count().toByte()) //TODO: Write VarInt
        buffer.put(data)
    }
}