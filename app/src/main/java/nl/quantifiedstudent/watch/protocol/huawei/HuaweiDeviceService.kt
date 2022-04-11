package nl.quantifiedstudent.watch.protocol.huawei

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import nl.quantifiedstudent.watch.extensions.array
import nl.quantifiedstudent.watch.extensions.toHexString
import nl.quantifiedstudent.watch.protocol.huawei.converters.HuaweiLinkPacketBinaryConverter
import java.nio.ByteBuffer
import java.util.*

@SuppressLint("MissingPermission", "TODO")
class HuaweiDeviceService(private val bluetoothAdapter: BluetoothAdapter, private val gatt: BluetoothGatt) {
    private val converter = HuaweiLinkPacketBinaryConverter()

    @SuppressLint("HardwareIds")
    val localMac: String = bluetoothAdapter.address
    val deviceMac: String = gatt.device.address

    @SuppressLint("MissingPermission", "TODO")
    fun enableNotification() {
        val service = gatt.getService(COMMAND_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(COMMAND_READ_CHARACTERISTIC_UUID)
        val descriptor = characteristic?.getDescriptor(COMMAND_READ_DESCRIPTOR_UUID)

        descriptor?.let { it ->
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            gatt.setCharacteristicNotification(characteristic, true)

            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }
    }

    fun readPacket(bytes: ByteArray): HuaweiLinkPacket {
        val localBuffer = ByteBuffer.wrap(bytes)
        return converter.read(HuaweiLinkPacket::class.java, localBuffer)
    }

    fun sendPacket(packet: HuaweiLinkPacket) {
        val buffer = ByteBuffer.allocate(1024)

        converter.write(packet, buffer)

        sendPacket(buffer.array(buffer.position()))

        buffer.position(0)
    }

    @SuppressLint("MissingPermission", "TODO")
    fun sendPacket(bytes: ByteArray) {
        val service = gatt.getService(COMMAND_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(COMMAND_WRITE_CHARACTERISTIC_UUID)

        Log.i("HuaweiDeviceService", "Sending message: ${bytes.toHexString()}")

        characteristic?.let {
            it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            it.value = bytes
            gatt.writeCharacteristic(it)
        }
    }

    companion object {
        private val COMMAND_SERVICE_UUID = UUID.fromString("0000fe86-0000-1000-8000-00805f9b34fb")
        private val COMMAND_WRITE_CHARACTERISTIC_UUID = UUID.fromString("0000fe01-0000-1000-8000-00805f9b34fb")
        private val COMMAND_READ_CHARACTERISTIC_UUID = UUID.fromString("0000fe02-0000-1000-8000-00805f9b34fb")
        private val COMMAND_READ_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}