package nl.quantifiedstudent.watch.protocol.huawei

data class HuaweiLinkPacket(
    val command: HuaweiLinkCommand,
) {
    companion object {
        const val Magic: Byte = 0x5a
    }
}
