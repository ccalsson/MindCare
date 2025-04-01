@Singleton
class DatabaseEncryption @Inject constructor(
    private val cryptoManager: CryptoManager
) {
    private val encryptedFields = setOf(
        "content",
        "mood",
        "medicalInfo",
        "personalNotes"
    )

    fun encryptField(value: String): String {
        return cryptoManager.encrypt(value, DATABASE_KEY).toBase64()
    }

    fun decryptField(value: String): String {
        return cryptoManager.decrypt(value.fromBase64(), DATABASE_KEY)
    }

    companion object {
        private const val DATABASE_KEY = "db_encryption_key"
    }
} 