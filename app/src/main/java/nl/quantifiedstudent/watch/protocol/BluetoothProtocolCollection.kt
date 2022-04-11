package nl.quantifiedstudent.watch.protocol

import android.util.SparseArray
import androidx.core.util.containsKey

class BluetoothProtocolCollection(
    private val protocols: Array<BluetoothProtocol> = arrayOf()
) {

    fun determineProtocol(manufacturerSpecificData: SparseArray<ByteArray>): BluetoothProtocol? {
        return protocols.firstOrNull { protocol ->
            val compatiblePeripherals = protocol.compatibility
            val peripheral = compatiblePeripherals.firstOrNull { peripheral ->
                manufacturerSpecificData.containsKey(peripheral.manufacturerId)
                manufacturerSpecificData[peripheral.manufacturerId].contentEquals(peripheral.manufacturerData)
            }

            peripheral != null
        }
    }
}