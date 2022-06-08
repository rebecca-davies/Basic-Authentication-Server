package rebecca.auth.server

import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.server.engine.*
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import org.kodein.db.DB
import org.kodein.db.Snapshot
import org.kodein.db.TypeTable
import org.kodein.db.impl.default
import org.kodein.db.impl.open
import org.kodein.db.kv.LevelDBOptions
import org.kodein.db.orm.kotlinx.KotlinxSerializer
import org.kodein.memory.util.isFrozen
import org.slf4j.Logger
import rebecca.auth.server.alive.Connection
import rebecca.auth.server.alive.impl.handle
import rebecca.auth.server.alive.impl.write
import rebecca.auth.server.key.User
import rebecca.auth.server.key.generate
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.Executors

lateinit var logger: Logger

val db = DB.open("./db")

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

fun main(args: Array<String>) = commandLineEnvironment(args).start()

fun Application.module() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    startAuthenticationServer()
}
