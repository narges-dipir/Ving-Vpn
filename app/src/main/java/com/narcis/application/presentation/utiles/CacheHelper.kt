package com.narcis.application.presentation.utiles

import android.content.Context
import coil.Coil
import coil.ImageLoader
import coil.request.CachePolicy

fun setupCachePolicy(applicationContext: Context) {
    val imageLoader = ImageLoader.Builder(applicationContext)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .build()
    Coil.setImageLoader(imageLoader)
}

fun countryFlagUrl(countryCode: String?) =
    "https://raw.githubusercontent.com/hampusborgos/country-flags/main/svg/$countryCode.svg"
