package nl.quantifiedstudent.watch.protocol.huawei

data class HuaweiLinkCommandTLV(
    var tag: Byte,
    val length: Int,
    var value: ByteArray = byteArrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HuaweiLinkCommandTLV

        if (tag != other.tag) return false
        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = tag.toInt()
        result = 31 * result + value.contentHashCode()
        return result
    }
}
