package com.neoapps.skijahorina.features.skicenter.camera

object WebcamUrlHelper {

    private val streamPatterns = listOf(
        ".m3u8",
        ".mp4",
        ".webm",
        "/mjpg",
        "/mjpeg",
        "mjpeg",
        "stream",
        "hls",
        "live"
    )

    fun isLikelyStream(url: String): Boolean {
        if (url.isBlank()) return false
        val lower = url.lowercase()
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".gif")) {
            return false
        }
        return streamPatterns.any { lower.contains(it) }
    }
}
