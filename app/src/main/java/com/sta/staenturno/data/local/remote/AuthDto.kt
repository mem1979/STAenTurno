package com.sta.staenturno.data.remote

data class LoginResponse(
    val token: String,
    val expira: Long,
    val passwordDefault: Boolean
)

data class ChangePassResponse(val success: Boolean)
