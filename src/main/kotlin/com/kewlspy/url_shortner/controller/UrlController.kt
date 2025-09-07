package com.kewlspy.url_shortner.controller

import com.kewlspy.url_shortner.dto.CreateShortUrlRequest
import com.kewlspy.url_shortner.dto.CreateShortUrlResponse
import com.kewlspy.url_shortner.dto.UrlListItemResponse
import com.kewlspy.url_shortner.service.UrlService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UrlController(private val urlService: UrlService) {

    @GetMapping("/api/v1/urls")
    fun listUrls(
            @RequestParam(name = "page", defaultValue = "0") page: Int,
            @RequestParam(name = "size", defaultValue = "20") size: Int,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") sortBy: String,
            @RequestParam(name = "dir", defaultValue = "desc") dir: String
    ): ResponseEntity<Page<UrlListItemResponse>> {
        val direction =
                if (dir.equals("asc", ignoreCase = true)) Sort.Direction.ASC
                else Sort.Direction.DESC
        val pageResult = urlService.listAll(page, size, sortBy, direction)
        return ResponseEntity.ok(pageResult)
    }

    @PostMapping("/api/v1/shorten")
    fun shorten(
            @Valid @RequestBody request: CreateShortUrlRequest
    ): ResponseEntity<CreateShortUrlResponse> {
        urlService.validateUrl(request.url)
        val response = urlService.shorten(request.url)
        return ResponseEntity.status(HttpStatus.CREATED).body(response) 
    }

    @GetMapping("/api/v1/urls/{slug}")
    fun getOriginal(@PathVariable slug: String): ResponseEntity<Map<String, Any>> {
        val mapping = urlService.resolve(slug)
        return ResponseEntity.ok(
                mapOf("originalUrl" to mapping.originalUrl, "hits" to mapping.hits)
        )
    }

    // Friendly redirect endpoint for browsers or simple HTTP clients
    @GetMapping("/{slug}")
    fun redirect(@PathVariable slug: String): ResponseEntity<Void> {
        val mapping = urlService.resolve(slug)
        val headers = HttpHeaders()
        headers.location = java.net.URI.create(mapping.originalUrl)
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build()
    }
}
