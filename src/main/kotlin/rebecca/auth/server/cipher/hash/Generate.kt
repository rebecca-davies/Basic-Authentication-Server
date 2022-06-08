package rebecca.auth.server.cipher.hash

import com.itsradiix.DiscordWebHookMessage
import kotlinx.serialization.Serializable
import org.kodein.db.model.Id
import org.kodein.db.model.orm.Metadata
import rebecca.auth.server.database
import rebecca.auth.server.discord.WEBHOOK_URL
import rebecca.auth.server.discord.confirmationMessage
import rebecca.auth.server.cipher.hash.generateHash
import rebecca.auth.server.database.User
import rebecca.auth.server.logger
import rebecca.auth.server.util.addChar

fun generate(discordId: String) {
    val key = generateHash(((discordId.toLong() shl 32) shr 8).toString()).substring(0, 16).addChar("-", 4).addChar("-", 9).addChar("-", 14)
    val user = User(key, discordId, "")
    database.put(user)
    DiscordWebHookMessage.sendMessage(WEBHOOK_URL, confirmationMessage(discordId, key))
    logger.info("Generated cipher for ${user.uid}, cipher = ${user.key}")
}


