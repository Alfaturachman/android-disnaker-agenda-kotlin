package com.example.disnakeragenda.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.disnakeragenda.MainActivity
import com.example.disnakeragenda.R
import com.example.disnakeragenda.api.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var buttonLogin: Button
    private lateinit var tvRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        window.statusBarColor = resources.getColor(R.color.white, theme)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        emailEditText = findViewById(R.id.et_email)
        passwordEditText = findViewById(R.id.et_password)
        buttonLogin = findViewById(R.id.buttonLogin)
        tvRegister = findViewById(R.id.tvRegister)

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id_user", -1)

        if (userId != -1) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        buttonLogin.setOnClickListener {
            val username = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (TextUtils.isEmpty(username)) {
                emailEditText.error = "Username is required"
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                passwordEditText.error = "Password is required"
                return@setOnClickListener
            }

            loginUser(username, password)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        Log.d("LoginActivity", "Memulai loginUser() dengan email: $email")

        val requestData = JSONObject()
        requestData.put("email", email)
        requestData.put("password", password)
        Log.d("LoginActivity", "Request JSON: $requestData")

        val body = RequestBody.create("application/json".toMediaTypeOrNull(), requestData.toString())

        val call = RetrofitClient.instance.loginUser(body)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("LoginActivity", "onResponse() dipanggil")
                if (response.isSuccessful) {
                    try {
                        val result = response.body()?.string()
                        Log.d("LoginActivity", "Response body: $result")

                        val jsonResponse = JSONObject(result ?: "")
                        Log.d("LoginActivity", "JSON Parsed: $jsonResponse")

                        if (jsonResponse.getBoolean("status")) {
                            Log.d("LoginActivity", "Status login: true")

                            val userDetails = jsonResponse.getJSONObject("user_details")
                            Log.d("LoginActivity", "user_details: $userDetails")

                            val userDetailId = when {
                                userDetails.has("id") -> userDetails.getString("id").toInt()
                                userDetails.has("id_laporan") -> userDetails.getString("id_laporan").toInt()
                                userDetails.has("id_mediator") -> userDetails.getString("id_mediator").toInt()
                                else -> 0
                            }
                            Log.d("LoginActivity", "userDetailId: $userDetailId")

                            val userId = jsonResponse.getString("id_user").toInt()
                            val userEmail = jsonResponse.getString("email")
                            val userLevel = jsonResponse.getString("level")
                            val userNama = userDetails.getString("nama")
                            val userTelp = userDetails.optString("telp", "")
                            val userAlamat = userDetails.optString("alamat", "")

                            Log.d("LoginActivity", "userId: $userId")
                            Log.d("LoginActivity", "userEmail: $userEmail")
                            Log.d("LoginActivity", "userLevel: $userLevel")
                            Log.d("LoginActivity", "userNama: $userNama")
                            Log.d("LoginActivity", "userTelp: $userTelp")
                            Log.d("LoginActivity", "userAlamat: $userAlamat")

                            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putInt("id_user_detail", userDetailId)
                            editor.putInt("id_user", userId)
                            editor.putString("email", userEmail)
                            editor.putString("level", userLevel)
                            editor.putString("nama", userNama)
                            editor.putString("telp", userTelp)
                            editor.putString("alamat", userAlamat)
                            editor.apply()
                            Log.d("LoginActivity", "Data user disimpan ke SharedPreferences")

                            Toast.makeText(this@LoginActivity, "Login Berhasil", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            val errorMsg = jsonResponse.optString("error", "Login gagal")
                            Log.e("LoginActivity", "Login gagal: $errorMsg")
                            Toast.makeText(this@LoginActivity, "Login Gagal: $errorMsg", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Log.e("LoginActivity", "JSONException saat parsing: ${e.message}")
                        Toast.makeText(this@LoginActivity, "Error Parsing Response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginActivity", "Response error ${response.code()}: ${response.message()}")
                    Log.e("LoginActivity", "Isi errorBody: $errorBody")
                    Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("LoginActivity", "Request gagal: ${t.message}", t)
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}