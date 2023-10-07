package io.lb.extensions

import org.mindrot.jbcrypt.BCrypt

fun String.encrypt(): String? {
    val salt = BCrypt.gensalt(12)
    return BCrypt.hashpw(this, salt)
}

fun String.passwordCheck(encryptedPassword: String): Boolean {
    return BCrypt.checkpw(encryptedPassword, this)
}
