package com.example.disnakeragenda.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.disnakeragenda.R
import com.example.disnakeragenda.api.ApiResponse
import com.example.disnakeragenda.api.RetrofitClient
import com.example.disnakeragenda.model.UserProfile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailProfileActivity : AppCompatActivity() {

    private var idUser: Int = -1
    private lateinit var ivProfile: ImageView
    private lateinit var tvNama: TextView
    private lateinit var tvLevel: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_profile)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        findViewById<ImageButton>(R.id.btnKembali).setOnClickListener { finish() }

        ivProfile = findViewById(R.id.ivProfile)
        tvNama = findViewById(R.id.tvNama)
        tvLevel = findViewById(R.id.tvLevel)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvAddress = findViewById(R.id.tvAddress)

        idUser = getuserIdFromSharedPreferences()

        if (idUser != -1) {
            getProfileFromServer(idUser)
        } else {
            Toast.makeText(this, "User tidak ditemukan di session", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getProfileFromServer(idUser: Int) {
        val requestBody = mapOf("id_user" to idUser)

        Log.d("DetailProfileActivity", "Mengirim request getProfile dengan id_user: $idUser")

        RetrofitClient.instance.getProfile(requestBody).enqueue(object : Callback<ApiResponse<UserProfile>> {
            override fun onResponse(call: Call<ApiResponse<UserProfile>>, response: Response<ApiResponse<UserProfile>>) {
                Log.d("DetailProfileActivity", "Response diterima: code=${response.code()}")

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("DetailProfileActivity", "Status: ${apiResponse?.status}, Message: ${apiResponse?.message}")

                    apiResponse?.data?.let { userProfile ->
                        tvEmail.text = userProfile.user_info.email
                        tvLevel.text = userProfile.user_type

                        when (userProfile.user_type) {
                            "mediator" -> {
                                userProfile.mediator_info?.let {
                                    tvNama.text = it.nama
                                    tvPhone.text = it.telp
                                    tvAddress.text = it.alamat
                                    Glide.with(this@DetailProfileActivity)
                                        .load("${RetrofitClient.BASE_URL_UPLOADS}${it.profile}")
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_background)
                                        .into(ivProfile)
                                }
                            }
                            "pelapor" -> {
                                userProfile.pelapor_info?.let {
                                    tvNama.text = it.nama
                                    tvPhone.text = it.telp
                                    tvAddress.text = it.alamat
                                    Glide.with(this@DetailProfileActivity)
                                        .load("${RetrofitClient.BASE_URL_UPLOADS}${it.profile}")
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_background)
                                        .into(ivProfile)
                                }
                            }
                            "admin" -> {
                                userProfile.admin_info?.let {
                                    tvNama.text = it.nama
                                    tvPhone.text = it.telp
                                    tvAddress.text = it.alamat
                                    Glide.with(this@DetailProfileActivity)
                                        .load("${RetrofitClient.BASE_URL_UPLOADS}${it.profile}")
                                        .placeholder(R.drawable.ic_launcher_background)
                                        .error(R.drawable.ic_launcher_background)
                                        .into(ivProfile)
                                }
                            }
                            else -> {
                                Log.w("DetailProfileActivity", "User type tidak dikenali: ${userProfile.user_type}")
                                Toast.makeText(this@DetailProfileActivity, "User type tidak dikenali", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } ?: run {
                        Log.e("DetailProfileActivity", "Data profile kosong.")
                        Toast.makeText(this@DetailProfileActivity, "Data profile kosong", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("DetailProfileActivity", "Gagal memuat data. Code: ${response.code()} Body: ${response.errorBody()?.string()}")
                    Toast.makeText(this@DetailProfileActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<UserProfile>>, t: Throwable) {
                Log.e("DetailProfileActivity", "Request gagal: ${t.message}", t)
                Toast.makeText(this@DetailProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getuserIdFromSharedPreferences(): Int {
        val sharedPreferences = this.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val idUser = sharedPreferences.getInt("id_user", -1)
        Log.d("DetailProfileActivity", "id_user dari SharedPreferences: $idUser")
        return idUser
    }
}
