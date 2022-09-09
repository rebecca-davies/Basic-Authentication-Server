package rebecca.auth.server.discord

import com.itsradiix.DiscordWebHookMessage
import com.itsradiix.embed.Embed

const val WEBHOOK_URL = ""

fun confirmationMessage(discordId: String, key: String): DiscordWebHookMessage = DiscordWebHookMessage.Builder()
    .username("Key Confirmation")
    .avatar_url("https://discordhome.com/user_upload/emojis/blusher[1]-pngemoji.png")
    .embed(
        Embed.Builder()
            .title("Confirmation of Key purchase")
            .color("38a832")
            .description("Dear <@$discordId>, your purchase is complete and your cipher has been generated, please log in using this and do **NOT** share it, it'll remain locked to your device, any attempts at cracking or cipher sharing will result in your cipher being revoked.\n\n**Your cipher is:** $key")
            .build()
    ).build()
