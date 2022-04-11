package nl.quantifiedstudent.watch.protocol.huawei

import android.util.Log
import nl.quantifiedstudent.watch.extensions.toHexString
import nl.quantifiedstudent.watch.protocol.crypto.AES
import nl.quantifiedstudent.watch.protocol.crypto.HMacSha256
import java.security.SecureRandom

@ExperimentalUnsignedTypes
class HuaweiLinkDeviceConfigService(
    private val protocol: HuaweiLinkBluetoothProtocol
) : HuaweiLinkService {
    private val aes = AES()
    private val hMacSha256 = HMacSha256()
    private val deviceKey = DeviceKey()

    private lateinit var authVersion: ByteArray
    private lateinit var localNonce: ByteArray
    private lateinit var deviceNonce: ByteArray

    override val serviceId: Byte = 1

    override fun handlePacket(packet: HuaweiLinkPacket) {
        if (packet.command.serviceId.toInt() != 1) {
            Log.i("HuaweiHandshakeService", "Unknown service id ${packet.command.serviceId}")
        } else {
            when (packet.command.commandId.toInt()) {
                1 -> requestAuthentication(packet.command)
                19 -> requestBondParameters(packet.command)
                15 -> requestBond()
                else -> Log.i("HuaweiHandshakeService", "Unknown command id ${packet.command.commandId}")
            }
        }
    }

    fun requestLinkParams() {
        protocol.sendPacket(INIT_PACKET)
    }

    private fun requestAuthentication(command: HuaweiLinkCommand) {
        val nonce = command.getTlv(5).value

        authVersion = nonce.take(2).toByteArray()
        localNonce = generateLocalNonce()
        deviceNonce = nonce.copyOfRange(2, nonce.size)

        Log.i("HuaweiHandshakeService", "Auth version: ${authVersion.contentToString()}, Device nonce: ${deviceNonce.contentToString()}")

        val requestAuthPacket = HuaweiLinkPacket(
            HuaweiLinkCommand(
                1, 19, arrayOf(
                    HuaweiLinkCommandTLV(1, digestChallenge(localNonce, deviceNonce)),
                    HuaweiLinkCommandTLV(2, byteArrayOf(0, 1) + localNonce),
                )
            )
        )

        protocol.sendPacket(requestAuthPacket)
    }

    private fun requestBondParameters(command: HuaweiLinkCommand) {
        val expected = digestResponse(localNonce, deviceNonce)
        val response = command.getTlv(1).value

        Log.i("HuaweiHandshakeService", "Expected: ${expected.toHexString()}, Response: ${response.toHexString()}")

        val localMac = protocol.localMac
        val localSerial = localMac.replace(":", "").take(6)

        val requestBondParameters = HuaweiLinkPacket(
            HuaweiLinkCommand(
                1, 15, arrayOf(
                    HuaweiLinkCommandTLV(1),
                    HuaweiLinkCommandTLV(3, localSerial.encodeToByteArray()),
                    HuaweiLinkCommandTLV(4, byteArrayOf(2)),
                    HuaweiLinkCommandTLV(5),
                    HuaweiLinkCommandTLV(7, localMac.encodeToByteArray()),
                    HuaweiLinkCommandTLV(9),
                )
            )
        )

        protocol.sendPacket(requestBondParameters)
    }

    private fun requestBond() {
        val localSerial = protocol.localMac.replace(":", "").take(6)
        val iv = aes.computeInitializationVector(1)

        val requestBond = HuaweiLinkPacket(
            HuaweiLinkCommand(
                1, 14, arrayOf(
                    HuaweiLinkCommandTLV(1),
                    HuaweiLinkCommandTLV(3, byteArrayOf(0)),
                    HuaweiLinkCommandTLV(5, localSerial.encodeToByteArray()),
                    HuaweiLinkCommandTLV(6, createBondingKey(protocol.deviceMac, byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9), iv)),
                    HuaweiLinkCommandTLV(7, iv),
                    HuaweiLinkCommandTLV(9, "Quantified Student".encodeToByteArray()),
                )
            )
        )

        protocol.sendPacket(requestBond)
    }

    @ExperimentalUnsignedTypes
    private fun createBondingKey(macAddress: String, key: ByteArray, initializationVector: ByteArray): ByteArray {
        return aes.encrypt(key, deviceKey.create(macAddress), initializationVector)
    }

    private fun computeDigestForHuawei(data: ByteArray, localNonce: ByteArray, deviceNonce: ByteArray): ByteArray {
        val nonce = deviceNonce + localNonce
        val key = hMacSha256.digest(DIGEST_SECRET + data, nonce)

        return hMacSha256.digest(key, nonce)
    }

    private fun digestChallenge(localNonce: ByteArray, deviceNonce: ByteArray): ByteArray {
        return computeDigestForHuawei(byteArrayOf(1, 0), localNonce, deviceNonce)
    }

    private fun digestResponse(localNonce: ByteArray, deviceNonce: ByteArray): ByteArray {
        return computeDigestForHuawei(byteArrayOf(1, 16), localNonce, deviceNonce)
    }

    private fun generateLocalNonce(): ByteArray {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    companion object {
        private val INIT_PACKET = HuaweiLinkPacket(
            HuaweiLinkCommand(
                1, 1, arrayOf(
                    HuaweiLinkCommandTLV(1),
                    HuaweiLinkCommandTLV(2),
                    HuaweiLinkCommandTLV(3),
                    HuaweiLinkCommandTLV(4),
                )
            )
        )

        private val DIGEST_SECRET = byteArrayOf(112, -5, 108, 36, 3, 95, -37, 85, 47, 56, -119, -118, -18, -34, 63, 105)
    }
}