package nl.quantifiedstudent.watch.protocol

import nl.quantifiedstudent.watch.protocol.huawei.HuaweiLinkBluetoothProtocol

@ExperimentalUnsignedTypes
enum class SupportedBluetoothProtocol(val protocol: BluetoothProtocol) {
    HUAWEI_LINK(HuaweiLinkBluetoothProtocol())
}