package com.kewlspy.url_shortner.dto

import jakarta.validation.constraints.NotBlank

data class CreateShortUrlRequest(
        @field:NotBlank(message = "url must not be blank") val url: String
)
