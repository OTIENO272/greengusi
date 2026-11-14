package com.example.myapplication

import java.security.MessageDigest

object SecurityUtils {

    /**
     * Hashes passwords using SHA-256 to ensure no plain-text passwords
     * are stored in local storage, compliant with Google Play Store standards.
     */
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, storedHash: String): Boolean {
        return hashPassword(password) == storedHash
    }
}
