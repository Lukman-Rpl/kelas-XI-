package com.example.sakubijak

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.ActivityAddTransactionBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val calendar = Calendar.getInstance()

    private lateinit var apiService: ApiService
    private lateinit var prefManager: PreferenceManager

    // Model Kategori Sederhana untuk Dropdown
    data class CategoryItem(val id: Long, val name: String) {
        override fun toString(): String = name
    }

    // Daftar Kategori Statis
    private val categoryList = listOf(
        CategoryItem(1, "Makanan & Minuman"),
        CategoryItem(2, "Transportasi"),
        CategoryItem(3, "Belanja"),
        CategoryItem(4, "Tagihan & Utilitas"),
        CategoryItem(5, "Hiburan"),
        CategoryItem(6, "Pendidikan"),
        CategoryItem(7, "Lainnya")
    )

    private var selectedCategoryId: Long? = null
    private var selectedWalletId: Long = -1L
    private var transactionType: String = "expense" // Default: expense (pengeluaran)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ApiService & PreferenceManager
        apiService = ApiClient.getApiService(this)
        prefManager = PreferenceManager(this)

        // Ambil wallet_id dari Intent atau PreferenceManager
        val intentWalletId = intent.getLongExtra("EXTRA_WALLET_ID", -1L)
        selectedWalletId = if (intentWalletId != -1L) {
            intentWalletId
        } else {
            prefManager.getActiveWalletId()
        }

        if (selectedWalletId == -1L) {
            Toast.makeText(this, "Peringatan: Dompet aktif tidak ditemukan!", Toast.LENGTH_SHORT).show()
        }

        // Navigation Header
        binding.btnBack.setOnClickListener { finish() }
        binding.btnClear.setOnClickListener { clearForm() }

        // Setup Pickers & Dropdown
        setupCategoryDropdown()
        setupDatePicker()
        setupTimePicker()
        initDefaultDateTime()

        // Toggle / Chip Jenis Transaksi
        setupTransactionTypeSelection()

        // Action Save
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun setupTransactionTypeSelection() {
        /*
        binding.rgTransactionType.setOnCheckedChangeListener { _, checkedId ->
            transactionType = if (checkedId == R.id.rbIncome) "income" else "expense"
        }
        */
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryList)
        binding.dropdownCategory.setAdapter(adapter)

        binding.dropdownCategory.setOnItemClickListener { _, _, position, _ ->
            val selectedCategory = adapter.getItem(position)
            selectedCategoryId = selectedCategory?.id
        }
    }

    private fun setupDatePicker() {
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.etDate.setText(sdfDate.format(calendar.time))
        }

        binding.etDate.setOnClickListener {
            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTimePicker() {
        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        binding.etTime.setOnClickListener {
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                calendar.set(Calendar.SECOND, 0)
                binding.etTime.setText(sdfTime.format(calendar.time))
            }, hour, minute, true).show()
        }
    }

    private fun initDefaultDateTime() {
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val sdfTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        binding.etDate.setText(sdfDate.format(calendar.time))
        binding.etTime.setText(sdfTime.format(calendar.time))
    }

    private fun clearForm() {
        binding.etAmount.text?.clear()
        binding.dropdownCategory.text.clear()
        selectedCategoryId = null
        binding.etNote.text?.clear()

        // Bersihkan input penerima & nomor rekening
        binding.etRecipientName.text?.clear()
        binding.etRecipientAccount.text?.clear()

        // Reset waktu ke saat ini
        calendar.timeInMillis = System.currentTimeMillis()
        initDefaultDateTime()

        // Reset tombol
        setLoadingState(false)

        Toast.makeText(this, "Formulir dibersihkan", Toast.LENGTH_SHORT).show()
    }

    private fun saveTransaction() {
        val amountText = binding.etAmount.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val time = binding.etTime.text.toString().trim()
        val note = binding.etNote.text.toString().trim()

        // Ambil input penerima & nomor rekening
        val recipientName = binding.etRecipientName.text.toString().trim()
        val recipientAccount = binding.etRecipientAccount.text.toString().trim()

        // Validasi Dompet Aktif
        if (selectedWalletId == -1L) {
            Toast.makeText(this, "Gagal menyimpan: Dompet aktif belum dipilih!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validasi Input Lokal
        if (amountText.isEmpty() || selectedCategoryId == null || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Nominal, Kategori, Tanggal, dan Waktu wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            Toast.makeText(this, "Nominal transaksi harus lebih dari 0!", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button saat loading
        setLoadingState(true)

        lifecycleScope.launch {
            try {
                // Kirim data penerima ke Request Payload
                val requestPayload = CreateTransactionRequest(
                    walletId = selectedWalletId,
                    categoryId = selectedCategoryId!!,
                    type = transactionType,
                    amount = amount,
                    date = "$date $time",
                    description = note.ifEmpty { null },
                    recipientName = recipientName.ifEmpty { null },
                    recipientAccount = recipientAccount.ifEmpty { null }
                )

                // Panggil endpoint createTransaction
                val response = apiService.createTransaction(requestPayload)

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@AddTransactionActivity, "Transaksi Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)

                    // PERBAIKAN: Kembali ke MainActivity secara eksplisit
                    val intent = Intent(this@AddTransactionActivity, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        JSONObject(errorBody).optString("message", "Transaksi Gagal diproses")
                    } else {
                        response.body()?.message ?: "Transaksi Gagal diproses"
                    }

                    Toast.makeText(this@AddTransactionActivity, errorMessage, Toast.LENGTH_LONG).show()
                    setLoadingState(false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                setLoadingState(false)
                Toast.makeText(this@AddTransactionActivity, "Koneksi Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSave.isEnabled = false
            binding.btnSave.text = "Memproses Transaksi..."
        } else {
            binding.btnSave.isEnabled = true
            binding.btnSave.text = "Simpan Transaksi"
        }
    }
}