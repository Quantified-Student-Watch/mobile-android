package nl.quantifiedstudent.watch.protocol.crypto

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HMacSha256 {
    private val instance = Mac.getInstance(ALGORITHM)

    fun digest(key: ByteArray, data: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(key, ALGORITHM)
        instance.init(secretKey)

        return instance.doFinal(data)
    }

    companion object {
        private const val ALGORITHM = "HmacSHA256"
    }
}