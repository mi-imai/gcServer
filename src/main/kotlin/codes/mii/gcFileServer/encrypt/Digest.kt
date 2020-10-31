package codes.mii.gcFileServer.encrypt

import java.security.MessageDigest

class Digest {
    fun getSHA256(text: String): String {
        return MessageDigest.getInstance("SHA-256")
                .digest(text.toByteArray())
                .joinToString(separator = "") {
                    "%02x".format(it)
                }
    }
}