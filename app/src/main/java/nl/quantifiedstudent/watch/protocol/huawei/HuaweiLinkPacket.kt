package nl.quantifiedstudent.watch.protocol.huawei

data class HuaweiLinkPacket(
    var length: Short,
    var command: HuaweiLinkCommand,
    var checksum: Short,
) {
    companion object {
        const val Magic: Byte = 0x5a

        const val Polynomial: Int = 4129
        const val Initial: Int = 0
        const val Offset: Int = 0
    }
}
