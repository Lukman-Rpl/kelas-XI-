package com.example.sakubijak

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.ActivityTransferBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransferActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransferBinding
    private lateinit var apiService: ApiService
    private val walletList = ArrayList<WalletResponse>()

    private var selectedFromWalletId: Long? = null
    private var selectedToNoWallet: String? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getApiService(this)

        setupUI()
        fetchWallets()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener { finish() }

        updateDateLabel()
        binding.etDate.setOnClickListener { showDatePicker() }

        binding.btnTransfer.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateLabel()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun updateDateLabel() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        binding.etDate.setText(sdf.format(calendar.time))
    }

    private fun fetchWallets() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = apiService.getWallets()
                showLoading(false)

                if (response.isSuccessful && response.body()?.status == "success") {
                    val groupData = response.body()?.data
                    val wallets = groupData?.all ?: emptyList()

                    walletList.clear()
                    walletList.addAll(wallets)

                    setupDropdownAdapters()
                } else {
                    Toast.makeText(
                        this@TransferActivity,
                        "Gagal mengambil daftar dompet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                e.printStackTrace()
                Toast.makeText(
                    this@TransferActivity,
                    "Koneksi Error: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupDropdownAdapters() {
        val walletNames = walletList.map { wallet ->
            val noWalletText = if (!wallet.noWallet.isNullOrEmpty()) " (${wallet.noWallet})" else ""
            "${wallet.name}$noWalletText - Rp ${wallet.getSafeBalance().toLong()}"
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, walletNames)

        // Dropdown Dompet Asal
        binding.spinnerFromWallet.setAdapter(adapter)
        binding.spinnerFromWallet.setOnItemClickListener { _, _, position, _ ->
            selectedFromWalletId = walletList[position].id
        }

        // Input / Dropdown Dompet Tujuan
        binding.etToNoWallet.setAdapter(adapter)
        binding.etToNoWallet.setOnItemClickListener { _, _, position, _ ->
            selectedToNoWallet = walletList[position].noWallet
        }
    }

    private fun validateAndSubmit() {
        val amountText = binding.etAmount.text.toString().trim()
        val dateText = binding.etDate.text.toString().trim()
        val descriptionText = binding.etDescription.text.toString().trim()

        if (selectedFromWalletId == null) {
            binding.tilFromWallet.error = "Pilih dompet asal"
            return
        } else {
            binding.tilFromWallet.error = null
        }

        // Ambil nilai dari listener item dropdown (jika dipilih) atau teks yang diketik manual
        val manualInputNoWallet = binding.etToNoWallet.text.toString().trim()
        val targetNoWallet = selectedToNoWallet ?: manualInputNoWallet

        if (targetNoWallet.isEmpty()) {
            binding.tilToWallet.error = "Masukkan atau pilih dompet tujuan"
            return
        } else {
            binding.tilToWallet.error = null
        }

        val fromWallet = walletList.find { it.id == selectedFromWalletId }

        if (fromWallet != null && fromWallet.noWallet == targetNoWallet) {
            Toast.makeText(this, "Dompet asal dan tujuan tidak boleh sama!", Toast.LENGTH_LONG).show()
            return
        }

        if (amountText.isEmpty()) {
            binding.tilAmount.error = "Masukkan nominal transfer"
            return
        } else {
            binding.tilAmount.error = null
        }

        val amount = amountText.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            binding.tilAmount.error = "Nominal harus lebih dari 0"
            return
        }

        if (fromWallet != null && amount > fromWallet.getSafeBalance()) {
            binding.tilAmount.error = "Saldo dompet asal tidak mencukupi"
            return
        }

        sendTransferApi(
            fromWalletId = selectedFromWalletId!!,
            toNoWallet = targetNoWallet,
            amount = amount,
            date = dateText,
            description = descriptionText.ifEmpty { null }
        )
    }

    private fun sendTransferApi(
        fromWalletId: Long,
        toNoWallet: String,
        amount: Double,
        date: String,
        description: String?
    ) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val request = TransferRequest(
                    fromWalletId = fromWalletId,
                    toNoWallet = toNoWallet,
                    amount = amount,
                    date = date,
                    description = description
                )

                val response = apiService.transferBalance(request)
                showLoading(false)

                if (response.isSuccessful) {
                    Toast.makeText(
                        this@TransferActivity,
                        "Transfer saldo berhasil!",
                        Toast.LENGTH_SHORT
                    ).show()

                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val jsonObject = if (!errorBody.isNullOrEmpty()) JSONObject(errorBody) else JSONObject()
                    val errorMessage = jsonObject.optString("message", "Gagal melakukan transfer")

                    Toast.makeText(this@TransferActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                e.printStackTrace()
                Toast.makeText(
                    this@TransferActivity,
                    "Koneksi Error: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnTransfer.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnTransfer.isEnabled = true
        }
    }
}