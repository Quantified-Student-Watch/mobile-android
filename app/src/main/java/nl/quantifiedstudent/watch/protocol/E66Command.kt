package nl.quantifiedstudent.watch.protocol

@ExperimentalUnsignedTypes
enum class E66Command constructor(
    override var bytes: UByteArray,
    override var parameterCount: Int
) : ProtocolCommand {
    GET_BATTERY_PERCENTAGE(ubyteArrayOf(2u, 0u, 71u, 67u), 0),
    GET_BLOOD_PRESSURE(ubyteArrayOf(2u, 6u, 66u, 80u), 0),
    GET_DEVICE_NAME(ubyteArrayOf(2u, 3u, 71u, 80u), 0),
    GET_HEART_RATE(ubyteArrayOf(2u, 5u, 72u, 82u), 0),
    GET_MAC_ADDRESS(ubyteArrayOf(2u, 2u, 71u, 77u), 0),
}