package com.kewlspy.url_shortner.dto

import java.time.OffsetDateTime

data class UrlListItemResponse(
        val id: String,
        val shortKey: String,
        val originalUrl: String,
        val createdAt: OffsetDateTime,
        val hits: Long
)
