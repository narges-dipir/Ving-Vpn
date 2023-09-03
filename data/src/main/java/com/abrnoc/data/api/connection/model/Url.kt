package com.abrnoc.data.api.connection.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import javax.annotation.Nullable

data class Url(
    @SerializedName("address")
    @Expose
    val address: String,
    @SerializedName("alpn")
    @Expose
    @Nullable
    val alpn: String?,
    @SerializedName("country")
    @Expose
    val country: String,
    @SerializedName("fingerprint")
    @Expose
    @Nullable
    val fingerprint: String?,
    @SerializedName("flag")
    @Expose
    val flag: String,
    @SerializedName("password")
    @Expose
    val password: String,
    @SerializedName("port")
    @Expose
    val port: Int,
    @SerializedName("protocol")
    @Expose
    val protocol: String,
    @SerializedName("security")
    @Expose
    @Nullable
    val security: String?,
    @SerializedName("sni")
    @Expose
    @Nullable
    val sni: String?,
    @SerializedName("type")
    @Expose
    val type: String,
    @SerializedName("url")
    @Expose
    val url: String
)