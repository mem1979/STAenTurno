package com.sta.staenturno.data.local.remote

data class LoginResponse(
    val token: String,
    val expira: Long,
    val passwordDefault: Boolean
)

data class ChangePassResponse(val success: Boolean)
