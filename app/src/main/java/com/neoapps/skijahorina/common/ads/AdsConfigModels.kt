package com.neoapps.skijahorina.common.ads

import com.google.gson.annotations.SerializedName

data class AdsConfigResponse(
    @SerializedName("appId") val appId: String,
    @SerializedName("generatedAt") val generatedAt: String?,
    @SerializedName("screens") val screens: List<AdsScreenPolicyDto> = emptyList()
)

data class AdsScreenPolicyDto(
    @SerializedName("screenId") val screenId: String,
    @SerializedName("enabled") val enabled: Boolean = true,
    @SerializedName("adType") val adType: String = "interstitial",
    @SerializedName("everyNClicks") val everyNClicks: Int = 3,
    @SerializedName("updatedAt") val updatedAt: String? = null
)
