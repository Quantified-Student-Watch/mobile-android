package nl.quantifiedstudent.watch.protocol.huawei

import nl.quantifiedstudent.watch.extensions.shr
import java.security.MessageDigest

class DeviceKey {
    private val sha256 = MessageDigest.getInstance(ALGORITHM)

    @ExperimentalUnsignedTypes
    fun create(macAddress: String): ByteArray {
        val macAddressBytes = (macAddress.replace(":", "") + "0000").toByteArray().toUByteArray()

        val finalMixedKey = UByteArray(macAddressBytes.size)
        for (i in finalMixedKey.indices) {
            finalMixedKey[i] = ((SECRET_KEY[i] shr 6) xor macAddressBytes[i]) and 0xFF.toUByte()
        }

        return sha256.digest(finalMixedKey.toByteArray()).take(16).toByteArray()
    }

    companion object {
        const val ALGORITHM = "SHA-256"

        @ExperimentalUnsignedTypes
        private val SECRET_KEY = ubyteArrayOf(
            139u, 97u, 254u, 153u, 54u, 32u, 183u, 254u, 248u, 147u, 111u, 100u, 249u, 176u, 192u,
            45u, 250u, 18u, 77u, 252u, 176u, 151u, 25u, 190u, 230u, 9u, 245u, 214u, 39u, 98u, 4u, 75u
        )
    }
}