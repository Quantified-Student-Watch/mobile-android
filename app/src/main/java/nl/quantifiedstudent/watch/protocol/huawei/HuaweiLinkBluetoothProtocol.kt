package nl.quantifiedstudent.watch.protocol.huawei

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.util.Log
import nl.quantifiedstudent.watch.extensions.array
import nl.quantifiedstudent.watch.extensions.toHexString
import nl.quantifiedstudent.watch.protocol.BluetoothProtocol
import nl.quantifiedstudent.watch.protocol.CompatiblePeripheral
import nl.quantifiedstudent.watch.protocol.huawei.converters.HuaweiLinkPacketBinaryConverter
import java.nio.ByteBuffer
import java.util.*

@SuppressLint("MissingPermission", "TODO")
@ExperimentalUnsignedTypes
class HuaweiLinkBluetoothProtocol : BluetoothProtocol() {
    private val converter = HuaweiLinkPacketBinaryConverter()
    private val services = HuaweiLinkServiceCollection()

    val localMac: String = "02:00:00:00:00:00"
    lateinit var deviceMac: String

    override val compatibility: Array<CompatiblePeripheral> = arrayOf(
        CompatiblePeripheral.HUAWEI_BAND_6
    )

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        super.onServicesDiscovered(gatt, status)

        deviceMac = gatt?.device?.address.toString()

        val service = gatt?.getService(COMMAND_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(COMMAND_READ_CHARACTERISTIC_UUID)
        val descriptor = characteristic?.getDescriptor(COMMAND_READ_DESCRIPTOR_UUID)

        descriptor?.let { it ->
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            gatt.setCharacteristicNotification(characteristic, true)

            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(it)
        }
    }

    override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
        super.onDescriptorWrite(gatt, descriptor, status)
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        with(characteristic) {
            Log.i("HuaweiLinkBluetoothProtocol", "Characteristic $uuid changed, new value is: ${value.toHexString()}")
            handlePacket(value)
        }
    }

    override fun handlePacket(data: ByteArray) {
        val localBuffer = ByteBuffer.wrap(data)
        val packet = converter.read(HuaweiLinkPacket::class.java, localBuffer)

        val service = services.determineService(packet.command.serviceId)

        if (service != null) {
            service.handlePacket(packet)
        } else {
            Log.i("HuaweiLinkBluetoothProtocol", "Unable to handle service with id ${packet.command.serviceId}")
        }
    }

    fun sendPacket(packet: HuaweiLinkPacket) {
        val buffer = ByteBuffer.allocate(1024)
        converter.write(packet, buffer)

        sendPacket(buffer.array(buffer.position()))
    }

    override fun sendPacket(data: ByteArray) {
        val service = gatt.getService(COMMAND_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(COMMAND_WRITE_CHARACTERISTIC_UUID)

        Log.i("HuaweiLinkBluetoothProtocol", "Sending message: ${data.toHexString()}")

        characteristic?.let {
            it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            it.value = data
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