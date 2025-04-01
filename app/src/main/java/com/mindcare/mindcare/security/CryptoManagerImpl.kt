class CryptoManagerImpl @Inject constructor(
    private val context: Context
) : CryptoManager {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    private val encryptCipher = Cipher.getInstance(TRANSFORMATION)
    private val decryptCipher = Cipher.getInstance(TRANSFORMATION)

    private fun getOrCreateSecretKey(keyName: String): SecretKey {
        keyStore.getKey(keyName, null)?.let { return it as SecretKey }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setUserAuthenticationRequired(false)
            setRandomizedEncryptionRequired(true)
        }.build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    override fun encrypt(data: String, keyName: String): EncryptedData {
        val secretKey = getOrCreateSecretKey(keyName)
        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = encryptCipher.doFinal(data.toByteArray())
        return EncryptedData(
            data = encryptedBytes,
            iv = encryptCipher.iv
        )
    }

    override fun decrypt(encryptedData: EncryptedData, keyName: String): String {
        val secretKey = getOrCreateSecretKey(keyName)
        decryptCipher.init(
            Cipher.DECRYPT_MODE,
            secretKey,
            GCMParameterSpec(128, encryptedData.iv)
        )
        return String(decryptCipher.doFinal(encryptedData.data))
    }

    companion object {
        private const val TRANSFORMATION = 
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
    }
} 