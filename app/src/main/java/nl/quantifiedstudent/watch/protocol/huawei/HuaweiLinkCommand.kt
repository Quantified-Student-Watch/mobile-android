package nl.quantifiedstudent.watch.protocol.huawei

data class HuaweiLinkCommand(
    var serviceId: Byte,
    var commandId: Byte,
    var tlvs: Array<HuaweiLinkCommandTLV>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HuaweiLinkCommand

        if (serviceId != other.serviceId) return false
        if (commandId != other.commandId) return false
        if (!tlvs.contentEquals(other.tlvs)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serviceId.toInt()
        result = 31 * result + commandId
        result = 31 * result + tlvs.contentHashCode()
        return result
    }
}