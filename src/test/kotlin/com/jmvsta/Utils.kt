package com.jmvsta

import org.apache.hc.core5.net.URIBuilder
import org.apache.hc.core5.net.URLEncodedUtils
import org.apache.hc.core5.net.WWWFormCodec
import org.bouncycastle.jcajce.provider.digest.SHA3
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

private val digest = SHA3.Digest512()

fun computeSHA3_512(input: String): String {
    val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
}

fun parseFormData(data: String): Map<String, String> {
    val params = WWWFormCodec.parse(data, StandardCharsets.UTF_8)
    return params.associate { it.name to it.value }
}
