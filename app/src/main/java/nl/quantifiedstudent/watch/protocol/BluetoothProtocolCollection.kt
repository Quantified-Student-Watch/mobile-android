package nl.quantifiedstudent.watch.protocol

import android.bluetooth.le.ScanFilter

class BluetoothProtocolCollection(
    private val protocols: Collection<BluetoothProtocol>
) {
    fun determineProtocol(manufacturerSpecificData: Map<Int, ByteArray>): BluetoothProtocol? {
        return protocols.firstOrNull { protocol ->
            val peripherals = protocol.compatiblePeripherals
            val peripheral = peripherals.firstOrNull { peripheral ->
                manufacturerSpecificData.containsKey(peripheral.manufacturerId)
                manufacturerSpecificData[peripheral.manufacturerId].contentEquals(peripheral.manufacturerData)
            }

            peripheral != null
        }
    }

    fun buildScanFilters(): List<ScanFilter> {
        return protocols.flatMap { protocol ->
            protocol.createScanFilters()
        }
    }
}