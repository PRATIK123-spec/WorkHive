package com.example.workhive.network

import com.example.workhive.network.*
import com.example.workhive.network.RecommendResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/generateDescription")
    suspend fun generateDescription(@Body request: TitleRequest): Response<DescriptionResponse>

    @POST("/generateSummary")
    suspend fun generateSummary(@Body request: SummaryRequest): Response<SummaryResponse>

    @POST("/recommendEmployee")
    suspend fun recommendEmployee(@Body request: RecommendRequest): Response<RecommendResponse>
}
