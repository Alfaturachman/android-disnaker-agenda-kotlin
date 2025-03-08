package com.example.disnakeragenda.api

import com.example.disnakeragenda.model.RiwayatPelapor
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    // POST
    @POST("login.php")
    fun loginUser(@Body body: RequestBody): Call<ResponseBody>

    @POST("post_pelapor.php")
    fun registerUser(@Body body: RequestBody): Call<ResponseBody>

    // Pelapor
    @Headers("Content-Type: application/json")
    @POST("get_pelaporan_pelapor.php")
    fun getRiwayatPelaporanPelapor(@Body request: Map<String, Int>): Call<ApiResponse<List<RiwayatPelapor>>>

    @Headers("Content-Type: application/json")
    @POST("get_detail_pelaporan.php")
    fun getDetailPelaporanPelapor(@Body request: Map<String, Int>): Call<ApiResponse<RiwayatPelapor>>
}
