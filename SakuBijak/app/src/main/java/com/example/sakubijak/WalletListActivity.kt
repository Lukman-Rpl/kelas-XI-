package com.example.sakubijak

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sakubijak.databinding.ActivityWalletListBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class WalletListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletListBinding
    private val walletList = ArrayList<WalletResponse>()
    private lateinit var walletAdapter: WalletAdapter
    private lateinit var prefManager: PreferenceManager
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWalletListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)
        apiService = ApiClient.getApiService(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        setupRecyclerView()
        fetchWalletsFromApi()

        binding.btnAddWallet.setOnClickListener {
            showAddWalletDialog()
        }
    }

    private fun showAddWalletDialog() {
        val bottomSheet = AddWalletBottomSheet { newWallet ->
            Toast.makeText(
                this,
                "Dompet '${newWallet.name}' berhasil dibuat!",
                Toast.LENGTH_SHORT
            ).show()

            prefManager.saveActiveWalletId(newWallet.id)
            fetchWalletsFromApi()
        }
        bottomSheet.show(supportFragmentManager, AddWalletBottomSheet.TAG)
    }

    private fun setupRecyclerView() {
        val currentActiveId: Long = prefManager.getActiveWalletId()

        walletAdapter = WalletAdapter(
            walletList = walletList,
            activeWalletId = currentActiveId,
            onItemClick = { selectedWallet ->
                prefManager.saveActiveWalletId(selectedWallet.id)

                Toast.makeText(
                    this,
                    "Berpindah ke: ${selectedWallet.name}",
                    Toast.LENGTH_SHORT
                ).show()

                setResult(Activity.RESULT_OK)
                finish()
            },
            onOptionClick = { selectedWallet, anchorView ->
                // Opsi menu tambahan
            }
        )

        binding.rvWallets.apply {
            layoutManager = LinearLayoutManager(this@WalletListActivity)
            adapter = walletAdapter
        }
    }

    private fun fetchWalletsFromApi() {
        lifecycleScope.launch {
            try {
                val response = apiService.getWallets()

                if (response.isSuccessful && response.body()?.status == "success") {

                    // Ambil objek WalletGroupData dari response body
                    val groupData: WalletGroupData? = response.body()?.data
                    val apiWallets: List<WalletResponse> = groupData?.all ?: emptyList()

                    walletList.clear()
                    walletList.addAll(apiWallets)

                    var currentActiveId: Long = prefManager.getActiveWalletId()

                    // Cek apakah ID dompet aktif saat ini masih ada dalam daftar
                    val isActiveWalletValid = walletList.any { it.id == currentActiveId }

                    if (!isActiveWalletValid && walletList.isNotEmpty()) {
                        currentActiveId = walletList[0].id
                        prefManager.saveActiveWalletId(currentActiveId)

                        Toast.makeText(
                            this@WalletListActivity,
                            "Akses dompet sebelumnya telah dicabut. Berpindah ke dompet utama.",
                            Toast.LENGTH_LONG
                        ).show()

                        setResult(Activity.RESULT_OK)
                    }

                    // Update Adapter dengan data terbaru & active ID yang valid
                    walletAdapter.updateData(walletList, currentActiveId)

                    // Cek jika list kosong, tampilkan placeholder
                    checkEmptyState()

                    // Hitung total saldo dari seluruh wallet yang tersisa
                    calculateTotalBalance()

                } else {
                    val errorMessage = response.body()?.message ?: "Gagal mengambil data dompet"
                    Toast.makeText(
                        this@WalletListActivity,
                        "$errorMessage (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@WalletListActivity,
                    "Koneksi Error: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkEmptyState() {
        if (walletList.isEmpty()) {
            binding.tvEmptyWallet.visibility = View.VISIBLE
            binding.rvWallets.visibility = View.GONE
        } else {
            binding.tvEmptyWallet.visibility = View.GONE
            binding.rvWallets.visibility = View.VISIBLE
        }
    }

    private fun calculateTotalBalance() {
        val total = walletList.sumOf { it.getSafeBalance() }

        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
        }

        binding.tvTotalWalletBalance.text = numberFormat.format(total)
    }
}