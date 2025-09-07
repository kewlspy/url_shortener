package com.kewlspy.url_shortner.service

import com.kewlspy.url_shortner.dto.CreateShortUrlResponse
import com.kewlspy.url_shortner.dto.UrlListItemResponse
import com.kewlspy.url_shortner.model.UrlMapping
import com.kewlspy.url_shortner.repository.UrlRepository
import com.kewlspy.url_shortner.util.Base62
import jakarta.transaction.Transactional
import java.net.URI
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class UrlService(
        private val repository: UrlRepository,
        @Value("\${app.base-url}") private val baseUrl: String
) {

    /**
     * Basic URL validation — ensures presence of scheme and only http/https allowed. Returns a
     * parsed URI for further use if needed.
     */
    fun validateUrl(url: String): URI {
        val uri =
                try {
                    URI(url)
                } catch (ex: Exception) {
                    throw IllegalArgumentException("Invalid URL format")
                }

        if (uri.scheme == null ||
                        !(uri.scheme.equals("http", true) || uri.scheme.equals("https", true))
        ) {
            throw IllegalArgumentException("Only http/https URLs are allowed")
        }
        return uri
    }

    /**
     * Create a short URL mapping (idempotent). If original URL already exists, returns existing
     * slug. Transactional because it writes to DB.
     */
    @Transactional
    fun shorten(url: String): CreateShortUrlResponse {
        val normalized = normalize(url)

        // Check existing mapping (avoid using Optional.ifPresent with non-inline lambda & return)
        val existingOpt = repository.findByOriginalUrl(normalized)
        if (existingOpt.isPresent) {
            val existing = existingOpt.get()
            return CreateShortUrlResponse(
                    id = existing.shortKey,
                    shortKey = buildShortUrl(existing.shortKey),
                    originalUrl = existing.originalUrl
            )
        }

        // Generate new short key and persist new mapping
        val shortKey = generateUniqueShortKey()
        val saved = repository.save(UrlMapping(shortKey = shortKey, originalUrl = normalized))
        return CreateShortUrlResponse(
                id = saved.shortKey,
                shortKey = buildShortUrl(saved.shortKey),
                originalUrl = saved.originalUrl
        )
    }

    /** Resolve slug to payload and increment hits atomically in a transaction. */
    @Transactional
    fun resolve(slug: String): UrlMapping {
        val mapping =
                repository.findByShortKey(slug).orElseThrow {
                    NoSuchElementException("Short url not found")
                }

        // increment hits and persist
        mapping.hits = mapping.hits + 1
        repository.save(mapping)

        return mapping
    }

    /** Paginated listing of Url mappings, mapped into DTOs that contain slug + full shortUrl. */
    fun listAll(
            page: Int = 0,
            size: Int = 20,
            sortBy: String = "createdAt",
            direction: Sort.Direction = Sort.Direction.DESC
    ): Page<UrlListItemResponse> {
        val pageable = PageRequest.of(page, size, direction, sortBy)
        return repository.findAll(pageable).map { mapping ->
            UrlListItemResponse(
                    id = mapping.shortKey,
                    shortKey = buildShortUrl(mapping.shortKey),
                    originalUrl = mapping.originalUrl,
                    createdAt = mapping.createdAt,
                    hits = mapping.hits
            )
        }
    }

    /**
     * Minimal normalization - trim for now. Extend later (strip UTM, unify trailing slash,
     * lower-case host, etc.)
     */
    private fun normalize(url: String): String {
        return url.trim()
    }

    /** Generate a unique short key using Base62 encoding. */
    private fun generateUniqueShortKey(): String {
        var shortKey: String
        var attempts = 0
        do {
            // Generate a random short key
            val randomId = (System.currentTimeMillis() % 1000000) + attempts
            shortKey = Base62.encode(randomId)
            attempts++
        } while (repository.findByShortKey(shortKey).isPresent && attempts < 10)

        if (attempts >= 10) {
            throw RuntimeException("Unable to generate unique short key after 10 attempts")
        }

        return shortKey
    }

    /** Build full short URL using baseUrl from configuration. */
    private fun buildShortUrl(slug: String): String =
            if (baseUrl.endsWith("/")) baseUrl + slug else "$baseUrl/$slug"
}
