package rebecca.auth.server.key

import com.itsradiix.DiscordWebHookMessage
import com.itsradiix.embed.Embed
import io.ktor.application.*
import kotlinx.serialization.Serializable
import org.kodein.db.*
import org.kodein.db.impl.open
import org.kodein.db.model.Id
import org.kodein.db.model.orm.Metadata
import org.kodein.db.orm.kotlinx.KotlinxSerializer
import rebecca.auth.server.db
import java.security.MessageDigest
import java.security.SecureRandom

@Serializable
data class User(@Id val key: String, val uid: String, var hwid: String) : Metadata {
    override val id = key
    override fun indexes() = mapOf("key" to listOf(key, uid, hwid))
}

fun Application.generate(id: String) {
    val id = ((id.toLong() shl 32) shr 8).toString()
    val key = generateHash(id).substring(0, 16).addChar("-", 4).addChar("-", 9).addChar("-", 14)
    val user = User(key, id, "")
    db.put(user)
    println("Generated key for ${user.uid}, key = ${user.key}")
    DiscordWebHookMessage.sendMessage("https://discordapp.com/api/webhooks/983916418900844566/fEUuWUTmtQGuVIiPaV9lP07usSy0me6i3yAHQHOf8xDSQ57N5xXRk9LMKNZ-GesN0WPM", message(id, key))
}

fun String.addChar(ch: String, position: Int): String {
    return substring(0, position) + ch + substring(position)
}

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

fun message(id: String, key: String): DiscordWebHookMessage = DiscordWebHookMessage.Builder()
    .username("Key Confirmation")
    .avatar_url("https://discordhome.com/user_upload/emojis/blusher[1]-pngemoji.png")
    .embed(
        Embed.Builder()
            .title("Confirmation of Key purchase")
            .color("38a832")
            .description("Dear <@$id>, your purchase is complete and your key has been generated, please log in using this and do **NOT** share it, it'll remain locked to your device, any attempts at cracking or key sharing will result in your key being revoked.\n\n**Your key is:** $key")
            .build()
    ).build()
