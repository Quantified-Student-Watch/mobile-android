package nl.quantifiedstudent.watch.protocol

import android.bluetooth.le.ScanFilter

interface BluetoothProtocol {

    val compatiblePeripherals: Collection<PeripheralType>

    fun handlePacket(data: ByteArray)

    fun sendPacket(data: ByteArray)

    fun createScanFilters(): Collection<ScanFilter>

}