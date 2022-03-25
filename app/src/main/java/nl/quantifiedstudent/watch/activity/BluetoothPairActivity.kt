package nl.quantifiedstudent.watch.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nl.quantifiedstudent.watch.adapter.BluetoothScanResultAdapter
import nl.quantifiedstudent.watch.databinding.ActivityBluetoothPairBinding
import nl.quantifiedstudent.watch.extensions.toHexString
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiDeviceService
import nl.quantifiedstudent.watch.protocol.huawei.HuaweiHandshakeService

@ExperimentalUnsignedTypes
@SuppressLint("MissingPermission", "TODO")
class BluetoothPairActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBluetoothPairBinding

    private lateinit var deviceService: HuaweiDeviceService
    private lateinit var handshakeService: HuaweiHandshakeService

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK && !bluetoothAdapter.isEnabled) {
            launchBluetoothActivity()
        }
    }

    private val bluetoothLowEnergyScanner by lazy { bluetoothAdapter.bluetoothLeScanner }
    private val bluetoothScanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
    private val bluetoothScanResults = mutableListOf<ScanResult>()

    private val bluetoothScanResultAdapter: BluetoothScanResultAdapter by lazy {
        BluetoothScanResultAdapter(bluetoothScanResults) { device ->
            if (device == null) return@BluetoothScanResultAdapter

            bluetoothLowEnergyScanner.stopScan(bluetoothScanCallback)

            with(device) {
                Log.i("BluetoothScanResultCallback", "Pair with $name")
                connectGatt(this@BluetoothPairActivity, false, bluetoothGattCallback)
            }
        }
    }

    private val bluetoothScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val index = bluetoothScanResults.indexOfFirst { it.device.address == result.device.address }
            if (index != -1) {
                bluetoothScanResults[index] = result
                bluetoothScanResultAdapter.notifyItemChanged(index)
            } else {
                Log.i("BluetoothScanCallback", "Device, Name: ${result.device.name ?: "Unnamed"}, address: ${result.device.address}")

                bluetoothScanResults.add(result)
                bluetoothScanResultAdapter.notifyItemInserted(bluetoothScanResults.size - 1)
            }
        }
    }

    // TODO: Move GATT callback to Huawei protocol
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address

            bluetoothLowEnergyScanner.stopScan(bluetoothScanCallback)

            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i("BluetoothGattCallback", "Successfully connected to $deviceAddress")
                    Log.i("BluetoothGattCallback", "Service discovery: ${gatt.discoverServices()}")
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i("BluetoothGattCallback", "Successfully disconnected from $deviceAddress")
                    gatt.close()
                }
            } else {
                Log.i("BluetoothGattCallback", "Error $status encountered for $deviceAddress! Disconnecting...")
                gatt.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (gatt == null) {
                return;
            }

            deviceService = HuaweiDeviceService(bluetoothAdapter, gatt)
            deviceService.enableNotification()
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            if (descriptor == null) {
                return
            }

            with(descriptor) {
                when (status) {
                    BluetoothGatt.GATT_SUCCESS -> Log.i("BluetoothGattCallback", "Write descriptor $uuid: ${value.toHexString()}")
                    BluetoothGatt.GATT_READ_NOT_PERMITTED -> Log.e("BluetoothGattCallback", "Read not permitted for $uuid!")
                    else -> Log.e("BluetoothGattCallback", "Descriptor read failed for $uuid, error: $status")
                }
            }

            handshakeService = HuaweiHandshakeService(deviceService)
            handshakeService.startHandshake()
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            with(characteristic) {
                Log.i("BluetoothGattCallback", "Characteristic $uuid changed | value: ${value.toHexString()}")

                val packet = deviceService.readPacket(value)
                handshakeService.handlePacket(packet)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothPairBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )

        binding.bluetoothScanResultRecyclerView.apply {
            adapter = bluetoothScanResultAdapter
            layoutManager = LinearLayoutManager(
                this@BluetoothPairActivity,
                RecyclerView.VERTICAL,
                false
            )
            isNestedScrollingEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()

        if (!bluetoothAdapter.isEnabled) {
            launchBluetoothActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) return
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) return finishAffinity()

        // TODO: Currently filters on Huawei manufacturer and model
        val filter = ScanFilter.Builder()
            .setManufacturerData(637, byteArrayOf(1, 3, 0, -1, -1))
            .build()

        bluetoothLowEnergyScanner.startScan(
            listOf(filter),
            bluetoothScanSettings,
            bluetoothScanCallback
        )
    }

    private fun launchBluetoothActivity() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothActivityLauncher.launch(intent)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }
}