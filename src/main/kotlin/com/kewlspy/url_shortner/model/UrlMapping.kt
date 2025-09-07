package com.kewlspy.url_shortner.model

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(
        name = "url_table",
)
data class UrlMapping(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
        @Column(name = "short_key", nullable = false, unique = true, length = 10)
        val shortKey: String = "",
        @Column(name = "original_url", nullable = false, length = 2048)
        val originalUrl: String = "",
        @Column(name = "created_at", nullable = false)
        val createdAt: OffsetDateTime = OffsetDateTime.now(),
        @Column(name = "expires_at", nullable = true)
        val expiresAt: OffsetDateTime? = createdAt.plusDays(30),
        @Column(nullable = false) var hits: Long = 0
)
