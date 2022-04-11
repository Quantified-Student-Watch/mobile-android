package nl.quantifiedstudent.watch.protocol

import android.annotation.SuppressLint
import android.bluetooth.*
import android.util.Log
import nl.quantifiedstudent.watch.extensions.toHexString

abstract class DefaultBluetoothGattCallback : BluetoothGattCallback() {
    @SuppressLint("MissingPermission")
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        val deviceAddress = gatt.device.address

        if (status == BluetoothGatt.GATT_SUCCESS) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i("DefaultBluetoothGattCallback", "Successfully connected to $deviceAddress")
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i("DefaultBluetoothGattCallback", "Successfully disconnected from $deviceAddress")
                    gatt.close()
                }
            }
        } else {
            Log.i("DefaultBluetoothGattCallback", "Error $status encountered for $deviceAddress! Disconnecting...")
            gatt.close()
        }
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        if (descriptor == null) return

        with(descriptor) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> Log.i("DefaultBluetoothGattCallback", "Write descriptor $uuid: ${value.toHexString()}")
                BluetoothGatt.GATT_READ_NOT_PERMITTED -> Log.e("DefaultBluetoothGattCallback", "Read not permitted for $uuid!")
                else -> Log.e("DefaultBluetoothGattCallback", "Descriptor read failed for $uuid, error: $status")
            }
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        with(characteristic) {
            Log.i("DefaultBluetoothGattCallback", "Characteristic $uuid changed | value: ${value.toHexString()}")
        }
    }
}