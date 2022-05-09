package nl.quantifiedstudent.watch.protocol

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanFilter

abstract class AbstractBluetoothProtocol : DefaultBluetoothGattCallback(), BluetoothProtocol {

    protected lateinit var gatt: BluetoothGatt

    abstract override val compatiblePeripherals: Collection<PeripheralType>

    @SuppressLint("MissingPermission")
    fun isGattConnected(): Boolean {
        return this::gatt.isInitialized && gatt.device.bondState == BluetoothDevice.BOND_BONDED
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)

        this.gatt = gatt
    }

    abstract override fun handlePacket(data: ByteArray)

    abstract override fun sendPacket(data: ByteArray)

    override fun createScanFilters(): Collection<ScanFilter> {
        return compatiblePeripherals.map { peripheral ->
            ScanFilter.Builder()
                .setManufacturerData(peripheral.manufacturerId, peripheral.manufacturerData)
                .build()
        }
    }
}