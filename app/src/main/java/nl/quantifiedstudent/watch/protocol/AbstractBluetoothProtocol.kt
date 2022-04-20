package nl.quantifiedstudent.watch.protocol

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanFilter

abstract class AbstractBluetoothProtocol : DefaultBluetoothGattCallback() {

    protected lateinit var gatt: BluetoothGatt

    abstract val compatiblePeripherals: Collection<PeripheralType>

    @SuppressLint("MissingPermission")
    fun isGattConnected(): Boolean {
        return this::gatt.isInitialized && gatt.device.bondState == BluetoothDevice.BOND_BONDED
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)

        this.gatt = gatt
    }

    fun createScanFilters(): Collection<ScanFilter> {
        return compatiblePeripherals.map { peripheral ->
            ScanFilter.Builder()
                .setManufacturerData(peripheral.manufacturerId, peripheral.manufacturerData)
                .build()
        }
    }

    abstract fun handlePacket(data: ByteArray)

    abstract fun sendPacket(data: ByteArray)
}