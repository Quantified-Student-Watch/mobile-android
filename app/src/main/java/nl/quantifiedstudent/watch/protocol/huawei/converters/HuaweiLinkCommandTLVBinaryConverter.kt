package nl.quantifiedstudent.watch.protocol.huawei.converters

import nl.quantifiedstudent.watch.binary.BinaryConverter
import nl.quantifiedstudent.watch.binary.VarInt
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiLinkCommandTLV
import java.nio.ByteBuffer

class HuaweiLinkCommandTLVBinaryConverter : BinaryConverter<HuaweiLinkCommandTLV>() {
    override fun read(clazz: Class<HuaweiLinkCommandTLV>, buffer: ByteBuffer): HuaweiLinkCommandTLV {
        val tag = buffer.get()
        val length = VarInt.getVarInt(buffer)

        val data = ByteArray(length)
        buffer.get(data)

        return HuaweiLinkCommandTLV(tag, data)
    }

    override fun write(value: HuaweiLinkCommandTLV, buffer: ByteBuffer) {
        buffer.put(value.tag)

        val data = value.value

        VarInt.putVarInt(data.count(), buffer)
        buffer.put(data)
    }
}