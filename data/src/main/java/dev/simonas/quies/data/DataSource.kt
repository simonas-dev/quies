package dev.simonas.quies.data

import android.content.Context
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import java.io.InputStream
import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Serializable
data class Data(
    val version: Int,
    val gameSets: List<Set>,
) {
    @Serializable
    data class Set(
        val id: String,
        val name: String,
        val level1: List<String>,
        val level2: List<String>,
        val level3: List<String>,
    )
}

interface DataSource {

    fun get(): Data
}

class EncryptedDataSource(
    private val context: Context,
) : DataSource {

    private val yamlContents: String by lazy {
        decryptFile(context.assets.open("data.yaml.enc"))
    }

    val data: Data by lazy {
        Yaml.default.decodeFromString(yamlContents)
    }

    override fun get(): Data = data
}

private fun decryptFile(fileInputStream: InputStream): String {
    val key = Secrets.getAESKey()
    val iv = Secrets.getAesIv()
    val keyByteArr = key.decodeHex()
    val ivByteArr = iv.decodeHex()
    val ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
        init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(keyByteArr, "AES"),
            IvParameterSpec(ivByteArr),
        )
    }
    val originalBytes = ecipher.doFinal(fileInputStream.readBytes())
    val decryptedBytes = String(originalBytes, Charset.forName("UTF-8"))
    return decryptedBytes
}

@Suppress("MagicNumber")
private fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
