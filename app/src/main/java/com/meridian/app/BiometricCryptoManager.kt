package com.meridian.app

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BiometricCryptoManager {

    companion object {
        private const val KEY_NAME =
            "meridian_biometric_key"

        private const val ANDROID_KEYSTORE =
            "AndroidKeyStore"
    }

    /*
     * Creates the key the first time it is needed.
     *
     * The key is stored inside Android Keystore
     * instead of being hard-coded inside the app.
     */
    fun createSecretKey() {

        val keyStore =
            KeyStore.getInstance(
                ANDROID_KEYSTORE
            )

        keyStore.load(null)

        // Do not create another key if one already exists.
        if (keyStore.containsAlias(KEY_NAME)) {
            return
        }

        val keyGenerator =
            KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

        val keySpec =
            KeyGenParameterSpec.Builder(
                KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(
                    KeyProperties.BLOCK_MODE_CBC
                )
                .setEncryptionPaddings(
                    KeyProperties.ENCRYPTION_PADDING_PKCS7
                )

                /*
                 * The key can only be used after
                 * successful user authentication.
                 */
                .setUserAuthenticationRequired(true)

                .build()

        keyGenerator.init(keySpec)

        keyGenerator.generateKey()
    }

    /*
     * Retrieves the key from Android Keystore.
     */
    private fun getSecretKey(): SecretKey {

        val keyStore =
            KeyStore.getInstance(
                ANDROID_KEYSTORE
            )

        keyStore.load(null)

        return keyStore.getKey(
            KEY_NAME,
            null
        ) as SecretKey
    }

    /*
     * Creates an AES Cipher initialized for encryption.
     *
     * This Cipher will be wrapped inside
     * BiometricPrompt.CryptoObject.
     */
    fun getEncryptCipher(): Cipher {

        createSecretKey()

        val cipher =
            Cipher.getInstance(
                "${KeyProperties.KEY_ALGORITHM_AES}/" +
                        "${KeyProperties.BLOCK_MODE_CBC}/" +
                        KeyProperties.ENCRYPTION_PADDING_PKCS7
            )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getSecretKey()
        )

        return cipher
    }
}