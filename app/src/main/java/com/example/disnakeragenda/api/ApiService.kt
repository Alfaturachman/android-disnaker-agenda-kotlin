package com.example.disnakeragenda.api

import com.example.disnakeragenda.model.AgendaLaporan
import com.example.disnakeragenda.model.TotalRekapData
import com.example.disnakeragenda.model.AgendaMediasi
import com.example.disnakeragenda.model.LaporanRequest
import com.example.disnakeragenda.model.Mediator
import com.example.disnakeragenda.model.StatsTotalData
import com.example.disnakeragenda.model.TambahPelapor
import com.example.disnakeragenda.model.UpdateAgendaAdmin
import com.example.disnakeragenda.model.UpdateAgendaMediator
import com.example.disnakeragenda.model.UserProfile
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
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
    @POST("get_pelapor_total_data.php")
    fun TotalDataPelapor(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<TotalRekapData>>

    @Headers("Content-Type: application/json")
    @POST("get_stats_pelapor.php")
    fun StatsDataPelapor(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<List<StatsTotalData>>>

    @Headers("Content-Type: application/json")
    @POST("get_agenda_pelapor.php")
    fun getRiwayatPelaporanPelapor(@Body request: Map<String, Int>): Call<ApiResponse<List<AgendaMediasi>>>

    @Headers("Content-Type: application/json")
    @POST("get_detail_pelaporan.php")
    fun getDetailPelaporanPelapor(@Body request: Map<String, Int>): Call<ApiResponse<AgendaMediasi>>

    @POST("post_agenda_mediasi.php")
    fun tambahPelapor(@Body request: TambahPelapor): Call<ApiResponse<TambahPelapor>>

    // Mediator
    @Headers("Content-Type: application/json")
    @POST("get_mediator_total_data.php")
    fun TotalDataMediator(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<TotalRekapData>>

    @Headers("Content-Type: application/json")
    @POST("get_stats_mediator.php")
    fun StatsDataMediator(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<List<StatsTotalData>>>

    @POST("get_all_agenda_mediator.php")
    fun getIdMediator(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<List<AgendaMediasi>>>

    @GET("get_all_agenda.php")
    fun getAllAgenda(): Call<ApiResponse<List<AgendaMediasi>>>

    @GET("get_all_mediator.php")
    fun getMediator(): Call<ApiResponse<List<Mediator>>>

    @POST("update_agenda_mediator.php")
    fun updateMediator(@Body request: UpdateAgendaMediator): Call<ApiResponse<Unit>>

    @POST("update_agenda_admin.php")
    fun updateAgendaAdmin(@Body request: UpdateAgendaAdmin): Call<ApiResponse<Unit>>

    @POST("delete_mediasi.php")
    fun deleteMediasi(@Body body: RequestBody): Call<ResponseBody>

    // Laporan
    @GET("get_all_laporan.php")
    fun getLaporan(): Call<ApiResponse<List<AgendaLaporan>>>

    @Headers("Content-Type: application/json")
    @POST("get_spinner_agenda.php")
    fun getAgendaMediasi(@Body requestBody: HashMap<String, Int>): Call<ApiResponse<List<AgendaLaporan>>>

    @POST("post_laporan_mediasi.php")
    fun simpanLaporan(@Body request: LaporanRequest): Call<ApiResponse<Unit>>

    @POST("delete_laporan.php")
    fun deleteLaporan(@Body body: RequestBody): Call<ResponseBody>

    @POST("get_profile.php")
    fun getProfile(@Body requestBody: Map<String, Int>): Call<ApiResponse<UserProfile>>
}
