package rebecca.auth.server.util

fun String.addChar(ch: String, position: Int): String {
    return substring(0, position) + ch + substring(position)
}