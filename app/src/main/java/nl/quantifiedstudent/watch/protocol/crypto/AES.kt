package nl.quantifiedstudent.watch.protocol.crypto

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class AES {
    private val random = SecureRandom()
    private val aes = Cipher.getInstance(TRANSFORMATION)

    fun encrypt(bytes: ByteArray, key: ByteArray, initializationVector: ByteArray): ByteArray {
        val secretKeySpec = SecretKeySpec(key, "AES")
        val paramSpec: AlgorithmParameterSpec = IvParameterSpec(initializationVector)
        aes.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec)

        return aes.doFinal(bytes)
    }

    fun decrypt(bytes: ByteArray, key: ByteArray, initializationVector: ByteArray): ByteArray {
        val secretKeySpec = SecretKeySpec(key, "AES")
        val paramSpec: AlgorithmParameterSpec = IvParameterSpec(initializationVector)
        aes.init(Cipher.DECRYPT_MODE, secretKeySpec, paramSpec)

        return aes.doFinal(bytes)
    }

    fun computeInitializationVector(counter: Int): ByteArray {
        val bytes = ByteArray(12)
        random.nextBytes(bytes)

        val buffer = ByteBuffer.allocate(4)
        buffer.order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(counter)

        return bytes + buffer.array()
    }

    companion object {
        const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    }
}