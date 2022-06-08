package rebecca.auth.server.alive

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import java.io.Closeable

class Connection(
    val socket: Socket,
    val readChannel: ByteReadChannel,
    val writeChannel: ByteWriteChannel,
)  {
    fun disconnect() {
        writeChannel.close()
        socket.close()
    }
}