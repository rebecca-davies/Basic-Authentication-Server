package rebecca.auth.server.cipher.hash

import java.security.MessageDigest
import java.security.SecureRandom

fun generateSalt(): ByteArray {
    val random = SecureRandom()
    val bytes = ByteArray(20)
    random.nextBytes(bytes)
    return bytes
}

fun generateHash(passwordToHash: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    md.update(generateSalt())
    val bytes = md.digest(passwordToHash.toByteArray())
    val sb = StringBuilder()
    for (i in bytes.indices) {
        sb.append(
            ((bytes[i].toInt() and 0xff) + 0x100).toString(16)
                .substring(1)
        )
    }
    return sb.toString()
}