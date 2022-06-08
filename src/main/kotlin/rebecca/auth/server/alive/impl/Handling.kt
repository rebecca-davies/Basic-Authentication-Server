package rebecca.auth.server.alive.impl

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import org.kodein.db.find
import rebecca.auth.server.alive.Connection
import rebecca.auth.server.database
import rebecca.auth.server.cipher.encrypt.encrypt
import rebecca.auth.server.database.User
import rebecca.auth.server.logger


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
    database.newSnapshot().let { db ->
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


