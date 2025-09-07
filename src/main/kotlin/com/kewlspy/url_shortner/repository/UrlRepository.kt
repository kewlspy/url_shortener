package com.kewlspy.url_shortner.repository

import com.kewlspy.url_shortner.model.UrlMapping
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UrlRepository : JpaRepository<UrlMapping, Long> {
    @Query("SELECT u FROM UrlMapping u WHERE u.originalUrl = :originalUrl")
    fun findByOriginalUrl(@Param("originalUrl") originalUrl: String): Optional<UrlMapping>

    @Query("SELECT u FROM UrlMapping u WHERE u.shortKey = :shortKey")
    fun findByShortKey(@Param("shortKey") shortKey: String): Optional<UrlMapping>
}
