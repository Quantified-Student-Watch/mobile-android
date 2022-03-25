package nl.quantifiedstudent.watch.protocol.huawei

data class HuaweiLinkPacket(
    var command: HuaweiLinkCommand,
) {
    companion object {
        const val Magic: Byte = 0x5a

        const val Polynomial: Int = 4129
        const val Initial: Int = 0
        const val Offset: Int = 0
    }
}
