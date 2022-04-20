package nl.quantifiedstudent.watch.protocol

import android.bluetooth.le.ScanFilter
import android.util.SparseArray
import androidx.core.util.containsKey

class BluetoothProtocolCollection(
    private val protocols: Collection<AbstractBluetoothProtocol> = emptyList()
) {
    fun determineProtocol(manufacturerSpecificData: SparseArray<ByteArray>): AbstractBluetoothProtocol? {
        return protocols.firstOrNull { protocol ->
            val compatiblePeripherals = protocol.compatibility
            val peripheral = compatiblePeripherals.firstOrNull { peripheral ->
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