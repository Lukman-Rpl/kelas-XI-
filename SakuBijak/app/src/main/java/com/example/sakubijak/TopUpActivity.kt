package com.example.sakubijak

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.ActivityTopUpBinding
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import kotlinx.coroutines.launch
import org.json.JSONObject

class TopUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTopUpBinding
    private lateinit var apiService: ApiService
    private lateinit var prefManager: PreferenceManager

    private lateinit var midtransLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var walletList: List<WalletResponse> = emptyList()
    private var selectedWalletId: Long = -1L

    private val clientKey = "Mid-client-ILtK1zeJ53TP1e2-"

    companion object {
        private const val CHANNEL_ID = "topup_notifications"
        private const val CHANNEL_NAME = "Notifikasi Top Up"
        private const val NOTIFICATION_ID_SUCCESS = 101
        private const val NOTIFICATION_ID_PENDING = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getApiService(this)
        prefManager = PreferenceManager(this)

        // 1. Buat Notification Channel untuk Android 8.0+
        createNotificationChannel()

        // 2. Launcher untuk Meminta Izin Notifikasi (Android 13+)
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Izin notifikasi ditolak", Toast.LENGTH_SHORT).show()
            }
        }
        checkNotificationPermission()

        // 3. Penanganan callback hasil transaksi dari Midtrans SDK v2
        midtransLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val intentData = result.data!!

                val transactionResult: TransactionResult? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intentData.getParcelableExtra(UiKitConstants.KEY_TRANSACTION_RESULT, TransactionResult::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    intentData.getParcelableExtra(UiKitConstants.KEY_TRANSACTION_RESULT)
                }

                val amountText = binding.etAmount.text.toString().trim()

                when (transactionResult?.status) {
                    "success" -> {
                        showLocalNotification(
                            NOTIFICATION_ID_SUCCESS,
                            "Top Up Berhasil! 🎉",
                            "Top Up sebesar Rp $amountText telah berhasil ditambahkan ke dompet Anda."
                        )
                        Toast.makeText(this, "Top Up Berhasil!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                    "pending" -> {
                        showLocalNotification(
                            NOTIFICATION_ID_PENDING,
                            "Pembayaran Menunggu Dikonfirmasi ⏳",
                            "Silakan selesaikan pembayaran Top Up sebesar Rp $amountText."
                        )
                        Toast.makeText(this, "Pembayaran Menunggu Dikonfirmasi", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                    "failed" -> {
                        Toast.makeText(this, "Pembayaran Gagal!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "Pembayaran Dibatalkan", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        initMidtransSdk()
        setupListeners()
        loadWalletsToDropdown()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi status transaksi Top Up Sakubijak"
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showLocalNotification(notificationId: Int, title: String, message: String) {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (!hasPermission) return

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(notificationId, builder.build())
    }

    private fun initMidtransSdk() {
        val merchantUrl = ApiClient.BASE_URL

        UiKitApi.Builder()
            .withContext(this)
            .withMerchantUrl(merchantUrl)
            .withMerchantClientKey(clientKey)
            .enableLog(true)
            .build()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnClear.setOnClickListener {
            binding.etAmount.setText("")

            if (walletList.isNotEmpty()) {
                val activeId = prefManager.getActiveWalletId()
                val defaultWallet = walletList.find { it.id == activeId } ?: walletList.first()
                selectedWalletId = defaultWallet.id

                val defaultLabel = "${defaultWallet.getFormattedLabel()} — Rp ${String.format("%.0f", defaultWallet.getSafeBalance())}"
                binding.dropdownWallet.setText(defaultLabel, false)
            }

            Toast.makeText(this, "Form telah dibersihkan", Toast.LENGTH_SHORT).show()
        }

        binding.btnPayTopup.setOnClickListener {
            val amountStr = binding.etAmount.text.toString().trim()
            if (amountStr.isEmpty()) {
                binding.etAmount.error = "Masukkan nominal top up"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount < 10000) {
                Toast.makeText(this, "Minimal top up adalah Rp 10.000", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedWalletId == -1L) {
                Toast.makeText(this, "Silakan pilih dompet tujuan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            requestSnapToken(selectedWalletId, amount, "Top Up Dompet")
        }
    }

    private fun loadWalletsToDropdown() {
        lifecycleScope.launch {
            try {
                val response = apiService.getWallets()
                if (response.isSuccessful && response.body()?.status == "success") {

                    // PERBAIKAN: Ambil properti .all dari objek WalletGroupData
                    val groupData = response.body()?.data
                    walletList = groupData?.all ?: emptyList()

                    if (walletList.isNotEmpty()) {
                        val walletDisplayItems = walletList.map { wallet ->
                            "${wallet.getFormattedLabel()} — Rp ${String.format("%.0f", wallet.getSafeBalance())}"
                        }

                        val adapter = ArrayAdapter(
                            this@TopUpActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            walletDisplayItems
                        )
                        binding.dropdownWallet.setAdapter(adapter)

                        val activeId = prefManager.getActiveWalletId()
                        val defaultWallet = walletList.find { it.id == activeId } ?: walletList.first()

                        selectedWalletId = defaultWallet.id
                        val defaultLabel = "${defaultWallet.getFormattedLabel()} — Rp ${String.format("%.0f", defaultWallet.getSafeBalance())}"
                        binding.dropdownWallet.setText(defaultLabel, false)

                        binding.dropdownWallet.setOnItemClickListener { _, _, position, _ ->
                            selectedWalletId = walletList[position].id
                        }
                    }
                } else {
                    Toast.makeText(this@TopUpActivity, "Gagal memuat daftar dompet", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@TopUpActivity, "Koneksi Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestSnapToken(walletId: Long, amount: Double, note: String) {
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                val request = TopUpRequest(
                    walletId = walletId,
                    amount = amount,
                    description = note
                )

                val response = apiService.topUp(request)
                setLoadingState(false)

                if (response.isSuccessful && response.body()?.status == "success") {
                    val snapToken = response.body()?.data?.snapToken

                    if (!snapToken.isNullOrEmpty()) {
                        openMidtransSdk(snapToken)
                    } else {
                        Toast.makeText(this@TopUpActivity, "Gagal mendapatkan Snap Token", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val jsonObject = if (!errorBody.isNullOrEmpty()) JSONObject(errorBody) else JSONObject()
                    val message = jsonObject.optString("message", "Gagal memproses Top Up")
                    Toast.makeText(this@TopUpActivity, message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                setLoadingState(false)
                e.printStackTrace()
                Toast.makeText(this@TopUpActivity, "Koneksi Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openMidtransSdk(snapToken: String) {
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            activity = this,
            launcher = midtransLauncher,
            snapToken = snapToken
        )
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnPayTopup.isEnabled = !isLoading
        binding.btnPayTopup.text = if (isLoading) "Memproses..." else "Lanjut ke Pembayaran"
    }
}