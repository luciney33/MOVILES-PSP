package com.example.navigationcompose.data.remote.entity

import com.google.gson.annotations.SerializedName

data class UpdatePublicKeyRequest(
    @SerializedName("publicKeyBase64")
    val publicKeyBase64: String
)

