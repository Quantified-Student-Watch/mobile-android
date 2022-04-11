package nl.quantifiedstudent.watch.protocol

import android.bluetooth.le.ScanFilter

enum class CompatiblePeripheral(
    val manufacturerId: Int,
    val manufacturerData: ByteArray
) {
    HUAWEI_BAND_6(637, byteArrayOf(1, 3, 0, -1, -1));

    companion object {
        fun buildScanFilters(): List<ScanFilter> {
            return values().map { peripheral ->
                ScanFilter.Builder()
                    .setManufacturerData(peripheral.manufacturerId, peripheral.manufacturerData)
                    .build()
            }
        }
    }
}