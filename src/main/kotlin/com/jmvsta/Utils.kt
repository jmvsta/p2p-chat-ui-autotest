package com.jmvsta

import org.bouncycastle.jcajce.provider.digest.SHA3

private val digest = SHA3.Digest512()

fun computeSHA3_512(input: String): String {
    val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
}