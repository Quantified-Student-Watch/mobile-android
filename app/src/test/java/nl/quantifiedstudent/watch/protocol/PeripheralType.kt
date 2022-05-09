package nl.quantifiedstudent.watch.protocol

enum class PeripheralType(
    val manufacturerId: Int,
    val manufacturerData: ByteArray
) {
    TEST_PERIPHERAL_ONE(0, byteArrayOf(0, 0, 0)),
    TEST_PERIPHERAL_TWO(1, byteArrayOf(0, 0, 1)),
    TEST_PERIPHERAL_THREE(2, byteArrayOf(0, 0, 2))
}