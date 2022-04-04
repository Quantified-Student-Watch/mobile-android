package nl.quantifiedstudent.watch.protocol.e66

import nl.quantifiedstudent.watch.protocol.ProtocolCommand

@Deprecated("Belongs to legacy protocol architecture")
enum class E66Command constructor(
    override var bytes: ByteArray,
    override var parameterCount: Int
) : ProtocolCommand {
    GET_BATTERY_PERCENTAGE(byteArrayOf(2, 0, 71, 67), 0),
    GET_BLOOD_PRESSURE(byteArrayOf(2, 6, 66, 80), 0),
    GET_DEVICE_NAME(byteArrayOf(2, 3, 71, 80), 0),
    GET_HEART_RATE(byteArrayOf(2, 5, 72, 82), 0),
    GET_MAC_ADDRESS(byteArrayOf(2, 2, 71, 77), 0),
}