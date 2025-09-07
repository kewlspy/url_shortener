package com.kewlspy.url_shortner.dto

import java.time.OffsetDateTime

data class UrlListItemResponse(
        val id: String, // the short slug (base62)
        val shortKey: String, // full short URL (app.base-url + slug)
        val originalUrl: String,
        val createdAt: OffsetDateTime,
        val hits: Long
)
