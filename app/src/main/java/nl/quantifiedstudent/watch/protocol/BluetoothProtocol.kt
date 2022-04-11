package nl.quantifiedstudent.watch.protocol

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt

abstract class BluetoothProtocol : DefaultBluetoothGattCallback() {
    protected lateinit var gatt: BluetoothGatt

    @SuppressLint("MissingPermission")
    fun isGattConnected(): Boolean {
        return this::gatt.isInitialized && gatt.device.bondState == BluetoothDevice.BOND_BONDED
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        super.onConnectionStateChange(gatt, status, newState)

        this.gatt = gatt
    }

    abstract val compatibility: Array<CompatiblePeripheral>

    abstract fun handlePacket(data: ByteArray)

    abstract fun sendPacket(data: ByteArray)
}