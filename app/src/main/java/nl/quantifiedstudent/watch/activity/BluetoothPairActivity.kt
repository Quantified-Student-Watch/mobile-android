package nl.quantifiedstudent.watch.activity

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
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

class BluetoothPairActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBluetoothPairBinding

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
            if (device != null) {
                //TODO: Start pair process
                Log.i("BluetoothScanResultCallback", "Pair with ${device.name}")
            }
        }
    }

    private val bluetoothScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val index = bluetoothScanResults.indexOfFirst {it.device.address == result.device.address }
            if (index != -1) {
                bluetoothScanResults[index] = result
                bluetoothScanResultAdapter.notifyItemChanged(index)
            } else {
                with(result.device) {
                    Log.i("BluetoothScanCallback", "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }

                bluetoothScanResults.add(result)
                bluetoothScanResultAdapter.notifyItemInserted(bluetoothScanResults.size - 1)
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

        bluetoothLowEnergyScanner.startScan(null, bluetoothScanSettings, bluetoothScanCallback)
    }

    private fun launchBluetoothActivity() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        bluetoothActivityLauncher.launch(intent)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2
    }
}