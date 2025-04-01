class SecureSharedPreferences @Inject constructor(
    context: Context,
    private val cryptoManager: CryptoManager
) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val keyName = "shared_prefs_key"

    fun putString(key: String, value: String) {
        val encryptedData = cryptoManager.encrypt(value, keyName)
        prefs.edit().apply {
            putString("$key.data", Base64.encodeToString(encryptedData.data, Base64.DEFAULT))
            putString("$key.iv", Base64.encodeToString(encryptedData.iv, Base64.DEFAULT))
        }.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        val encryptedData = prefs.getString("$key.data", null)
        val iv = prefs.getString("$key.iv", null)

        if (encryptedData == null || iv == null) {
            return defaultValue
        }

        return try {
            cryptoManager.decrypt(
                EncryptedData(
                    data = Base64.decode(encryptedData, Base64.DEFAULT),
                    iv = Base64.decode(iv, Base64.DEFAULT)
                ),
                keyName
            )
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREF_NAME = "secure_preferences"
    }
} 