package nl.quantifiedstudent.watch.protocol.huawei

data class HuaweiLinkPacket(
    var command: HuaweiLinkCommand,
) {
    companion object {
        const val Magic: Byte = 0x5a
    }
}
