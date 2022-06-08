package rebecca.auth.server.cipher.encrypt

import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

fun encrypt(strToEncrypt: String, secret: String): String? {
    try {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, setKey(secret))
        return Base64.getEncoder()
            .encodeToString(cipher.doFinal(strToEncrypt.toByteArray(charset("UTF-8"))))
    } catch (e: Exception) {
        println("Error while encrypting: $e")
    }
    return null
}

fun setKey(myKey: String) : SecretKeySpec {
    var sha: MessageDigest? = null
    var key = myKey.toByteArray(charset("UTF-8"))
    sha = MessageDigest.getInstance("SHA-1")
    key = sha.digest(key)
    key = Arrays.copyOf(key, 16)
    return SecretKeySpec(key, "AES")
}