package com.spacenine.dora.service

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

fun buildUnwiredLabsService(): UnwiredLabsService =
    Retrofit.Builder()
        .baseUrl(UnwiredLabsService.BASE_URL)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        )
        .build()
        .create(UnwiredLabsService::class.java)
