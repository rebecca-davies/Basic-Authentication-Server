package rebecca.auth.server

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.server.engine.*
import kotlinx.coroutines.*
import org.kodein.db.DB
import org.kodein.db.impl.open
import org.slf4j.Logger
import rebecca.auth.server.alive.Connection
import rebecca.auth.server.alive.impl.handle
import rebecca.auth.server.cipher.hash.generate
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

lateinit var logger: Logger

val database = DB.open("./database")

fun main(args: Array<String>) = commandLineEnvironment(args).start()

fun Application.startAuthenticationServer() = runBlocking {
    val port = ConfigFactory.load().getInt("ktor.deployment.port")
    val dispatcher = Executors.newCachedThreadPool().asCoroutineDispatcher()
    val selector = ActorSelectorManager(dispatcher)
    val server = aSocket(selector).tcp().bind(InetSocketAddress(port))
    logger = log
    logger.info("Authentication server listening on Port $port")
    while (true) {
        val socket = server.accept()
        val client = Connection(socket, socket.openReadChannel(), socket.openWriteChannel())
        launch(Dispatchers.IO) {
            readLine()?.split(" ")?.let { input ->
                if(input.first() == "generate") {
                    generate(input.last())
                }
            }
        }
        launch(Dispatchers.IO) {
            client.handle()
        }
    }
}

fun Application.module() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    startAuthenticationServer()
}
