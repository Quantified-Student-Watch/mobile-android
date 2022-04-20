package nl.quantifiedstudent.watch.protocol

enum class PeripheralType(
    val manufacturerId: Int,
    val manufacturerData: ByteArray
) {
    HUAWEI_BAND_6(637, byteArrayOf(1, 3, 0, -1, -1));
}