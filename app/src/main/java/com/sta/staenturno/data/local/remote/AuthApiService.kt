// data/remote/AuthApiService.kt
package com.sta.staenturno.data.remote

import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Header("X-Device-ID") deviceId: String,
        @Field("usuario") usuario: String,
        @Field("contrasena") contrasena: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("auth/cambiarClave")
    suspend fun changePassword(
        @Header("Authorization") bearer: String,
        @Field("nueva") nuevaClave: String
    ): ChangePassResponse
}

