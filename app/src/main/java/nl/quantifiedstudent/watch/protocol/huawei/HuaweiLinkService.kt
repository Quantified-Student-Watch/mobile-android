package nl.quantifiedstudent.watch.protocol.huawei

interface HuaweiLinkService {
    val serviceId: Byte

    @ExperimentalUnsignedTypes
    fun handlePacket(packet: HuaweiLinkPacket)
}