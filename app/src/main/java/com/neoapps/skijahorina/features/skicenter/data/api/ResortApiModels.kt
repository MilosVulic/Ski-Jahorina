package com.neoapps.skijahorina.features.skicenter.data.api

import com.google.gson.annotations.SerializedName

data class ResortApiResponse(
    @SerializedName("resortId") val resortId: String,
    @SerializedName("skiCenterEnum") val skiCenterEnum: String,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("temperature") val temperature: String,
    @SerializedName("wind") val wind: String,
    @SerializedName("snow") val snow: String,
    @SerializedName("weatherImage") val weatherImage: String,
    @SerializedName("forecast") val forecast: String,
    @SerializedName("slopes") val slopes: String,
    @SerializedName("lifts") val lifts: String
)
