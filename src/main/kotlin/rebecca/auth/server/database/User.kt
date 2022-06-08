package rebecca.auth.server.database

import kotlinx.serialization.Serializable
import org.kodein.db.model.Id
import org.kodein.db.model.orm.Metadata

@Serializable
data class User(@Id val key: String, val uid: String, var hwid: String) : Metadata {
    override val id = key
    override fun indexes() = mapOf("cipher" to listOf(key, uid, hwid))
}