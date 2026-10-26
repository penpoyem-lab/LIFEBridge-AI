package com.example.data.network

import com.example.data.model.ActionCardItem
import com.example.data.model.DocumentExplanation
import com.example.data.model.ScamAnalysis
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

data class ApiDocumentRequest(
    val title: String = "",
    val content: String,
    val category: String? = null,
    val target_language: String = "English"
)

data class ApiSafetyRequest(
    val content: String,
    val check_type: String = "Message"
)

data class ApiTranslationRequest(
    val text: String,
    val target_language: String
)

data class ApiTranslationResponse(
    val translated_text: String,
    val source_language: String,
    val target_language: String
)

data class ApiHealthResponse(
    val status: String,
    val service: String,
    val database: String
)

interface LifeBridgeApiService {
    @GET("api/v1/health")
    suspend fun checkHealth(): Response<ApiHealthResponse>

    @POST("api/v1/documents")
    suspend fun analyzeDocument(@Body request: ApiDocumentRequest): Response<Map<String, Any>>

    @POST("api/v1/safety/message-check")
    suspend fun checkSafetyMessage(@Body request: ApiSafetyRequest): Response<Map<String, Any>>

    @POST("api/v1/translation")
    suspend fun translateText(@Body request: ApiTranslationRequest): Response<ApiTranslationResponse>
}

object NetworkClient {
    // Standard backend host URL (configurable or pointing to local backend / loopback)
    var baseUrl = "http://10.0.2.2:8000/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val apiService: LifeBridgeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(LifeBridgeApiService::class.java)
    }
}
