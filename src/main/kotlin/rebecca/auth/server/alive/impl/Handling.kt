package rebecca.auth.server.alive.impl

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.joinAll
import org.kodein.db.DB
import org.kodein.db.TypeTable
import org.kodein.db.find
import org.kodein.db.impl.open
import org.kodein.db.kv.LevelDBOptions
import org.kodein.db.orm.kotlinx.KotlinxSerializer
import org.kodein.log.newLogger
import org.kodein.memory.util.freeze
import rebecca.auth.server.alive.Connection
import rebecca.auth.server.db
import rebecca.auth.server.key.User
import rebecca.auth.server.logger
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


suspend fun Connection.write(response: ByteArray) = writeChannel.apply { writeFully(response) }.flush()

suspend fun Connection.readAuth(): String {
    return readChannel.readUTF8Line().toString()
}
suspend fun Connection.readHwid(): String {
    return readChannel.readUTF8Line().toString()
}

suspend fun Connection.handle() {
    if(socket.isClosed) {
        return
    }
    checkResponse(readAuth(), readHwid())
}

suspend fun Connection.checkResponse(key: String, hw: String) {
    db.newSnapshot().let { db ->
        val index = db.find<User>().byId(key)
        if(!index.isValid()) {
            logger.info("Rejected from ${socket.remoteAddress} Key(\"$key\") Hardware(\"$hw\")")
            write("${encrypt(hw, hw.filter { it.isLetterOrDigit() })}\r\n".toByteArray())
            disconnect()
            return
        }
       if(index.model().hwid.isEmpty() || index.model().hwid == hw) {
           logger.info("Accepted from ${socket.remoteAddress} Key(\"$key\") Hardware(\"$hw\")")
           write("${encrypt(hw, hw.filter { it.isLetterOrDigit() })}\r\n".toByteArray())
           disconnect()
           return
        }
    }
}


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