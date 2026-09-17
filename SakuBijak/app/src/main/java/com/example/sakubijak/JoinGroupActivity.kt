package com.example.sakubijak

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.ActivityJoinGroupBinding
import kotlinx.coroutines.launch
import org.json.JSONObject

class JoinGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinGroupBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ApiService
        apiService = ApiClient.getApiService(this)

        binding.btnBack.setOnClickListener { finish() }

        // 1. Cek jika Activity dibuka dari Link / URL Intent
        handleDeepLink(intent)

        // 2. Click listener tombol Gabung Manual
        binding.btnJoinGroup.setOnClickListener {
            val input = binding.etGroupCode.text.toString().trim()
            if (input.isEmpty()) {
                binding.tilGroupCode.error = "Kode atau Link tidak boleh kosong"
                return@setOnClickListener
            }
            binding.tilGroupCode.error = null

            val code = extractCodeFromInput(input)
            processJoinGroup(code)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val appLinkData: Uri? = intent?.data
        if (appLinkData != null) {
            // Contoh format link: https://sakubijak.com/join?code=GRP-88X2A
            val code = appLinkData.getQueryParameter("code")
            if (!code.isNullOrEmpty()) {
                binding.etGroupCode.setText(code)
                processJoinGroup(code)
            }
        }
    }

    private fun extractCodeFromInput(input: String): String {
        // Jika pengguna menempelkan full URL (misal: https://sakubijak.com/join?code=GRP-88X2A)
        return if (input.contains("code=")) {
            Uri.parse(input).getQueryParameter("code") ?: input
        } else {
            input
        }
    }

    private fun processJoinGroup(groupCode: String) {
        lifecycleScope.launch {
            try {
                // 1. Kirim kode ke API
                val response = apiService.joinGroup(GroupJoinRequest(code = groupCode))

                // Cek ketersediaan response & status dari BaseApiResponse
                if (response.isSuccessful && response.body()?.status == "success") {

                    // PERBAIKAN: Ambil WalletResponse dari dalam field 'data'
                    val wallet: WalletResponse? = response.body()?.data

                    if (wallet != null) {
                        val walletName = wallet.name
                        val walletId = wallet.id

                        Toast.makeText(
                            this@JoinGroupActivity,
                            "Berhasil bergabung ke $walletName!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // 2. Simpan wallet id ke PreferenceManager
                        val prefManager = PreferenceManager(this@JoinGroupActivity)
                        prefManager.saveActiveWalletId(walletId)

                        // 3. Kirim sinyal RESULT_OK ke Activity pemanggil (WalletListActivity/MainActivity)
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(
                            this@JoinGroupActivity,
                            "Data dompet tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Ambil pesan error dari backend
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        JSONObject(errorBody).optString("message", "Kode grup tidak valid atau sudah kadaluarsa")
                    } else {
                        response.body()?.message ?: "Kode grup tidak valid atau sudah kadaluarsa"
                    }

                    Toast.makeText(
                        this@JoinGroupActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@JoinGroupActivity,
                    "Gagal terhubung ke server: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}