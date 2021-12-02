package nl.quantifiedstudent.watch.adapter

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nl.quantifiedstudent.watch.databinding.BluetoothScanResultRowItemBinding

class BluetoothScanResultAdapter(
    private val items: List<ScanResult>,
    private val onClick: ((device: BluetoothDevice?) -> Unit)
) : RecyclerView.Adapter<BluetoothScanResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BluetoothScanResultRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(
        private val binding: BluetoothScanResultRowItemBinding,
        private val onClickListener: ((device: BluetoothDevice?) -> Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(result: ScanResult) {
            binding.deviceName.text = result.device.name ?: "Unknown"
            binding.macAddress.text = result.device.address
            binding.signalStrength.text = "${result.rssi} dBm"
            binding.root.setOnClickListener { onClickListener.invoke(result.device) }
        }
    }
}