package dev.joguenco.roqui.util

import java.security.Key
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object OwnEncryption {
    private val ALGO = "AES"
    // Cambia la serie de números para generar una clave distinta
    private var keyValue =
        byteArrayOf('q'.toByte(), 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16)

    fun setKey(key: String) {
        keyValue = key.toByteArray()
    }

    /**
     * Encrypt a string with AES algorithm.
     *
     * @param data is a string
     * @return the encrypted string
     */
    @Throws(Exception::class)
    fun encrypt(data: String): String {
        val key = generateKey()
        val c = Cipher.getInstance(ALGO)
        c.init(Cipher.ENCRYPT_MODE, key)
        val encVal = c.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encVal)
    }

    /**
     * Decrypt a string with AES algorithm.
     *
     * @param encryptedData is a string
     * @return the decrypted string
     */
    @Throws(Exception::class)
    fun decrypt(encryptedData: String): String {
        val key = generateKey()
        val c = Cipher.getInstance(ALGO)
        c.init(Cipher.DECRYPT_MODE, key)
        val decordedValue = Base64.getDecoder().decode(encryptedData)
        val decValue = c.doFinal(decordedValue)
        return String(decValue)
    }

    /** Generate a new encryption key. */
    @Throws(Exception::class)
    private fun generateKey(): Key {
        return SecretKeySpec(keyValue, ALGO)
    }
}
