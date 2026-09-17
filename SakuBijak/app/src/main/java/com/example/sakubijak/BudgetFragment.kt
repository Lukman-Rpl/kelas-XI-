package com.example.sakubijak

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sakubijak.databinding.FragmentBudgetBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    private lateinit var prefManager: PreferenceManager

    // PERBAIKAN: Jika item tidak perlu diklik lagi, adapter cukup diinisialisasi tanpa listener
    // (Atau jika TransactionAdapter butuh argumen, kirim lambda kosong/Toast)
    private val transactionAdapter by lazy {
        TransactionAdapter { transaction ->
            // Opsional: tampilkan Toast singkat atau biarkan kosong
            // Toast.makeText(requireContext(), transaction.description, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = ApiClient.getApiService(requireContext())
        prefManager = PreferenceManager(requireContext())

        setupRecyclerView()
        loadTransactionsData()
    }

    private fun setupRecyclerView() {
        binding.rvBudget.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    fun loadTransactionsData() {
        val activeWalletId = prefManager.getActiveWalletId()

        if (activeWalletId != -1L) {
            fetchWalletSummaryAndTransactions(activeWalletId)
        } else {
            fetchDefaultWalletAndLoad()
        }
    }

    private fun fetchDefaultWalletAndLoad() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = apiService.getWallets()

                if (response.isSuccessful && response.body()?.status == "success") {
                    val groupData = response.body()?.data
                    val walletList = groupData?.all ?: emptyList()

                    if (walletList.isNotEmpty()) {
                        val defaultWalletId = walletList[0].id
                        prefManager.saveActiveWalletId(defaultWalletId)
                        fetchWalletSummaryAndTransactions(defaultWalletId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchWalletSummaryAndTransactions(walletId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1. Ambil detail Wallet Aktif
                val walletResponse = apiService.getWalletDetail(walletId)
                if (walletResponse.isSuccessful && walletResponse.body() != null) {
                    val wallet = walletResponse.body()?.data

                    val localeID = Locale("in", "ID")
                    val formatter = NumberFormat.getCurrencyInstance(localeID).apply {
                        maximumFractionDigits = 0
                    }

                    val balanceValue = wallet?.balance ?: 0.0
                    _binding?.tvTotalBudgetAll?.text = formatter.format(balanceValue)
                }

                // 2. Ambil daftar transaksi untuk wallet aktif
                val transResponse = apiService.getTransactionsByWallet(walletId)
                if (transResponse.isSuccessful) {
                    val transactions: List<TransactionResponse> = transResponse.body()?.data ?: emptyList()

                    _binding?.let { b ->
                        b.tvTotalRemainingAll.text = "${transactions.size} Transaksi"

                        if (transactions.isEmpty()) {
                            b.layoutEmptyState.visibility = View.VISIBLE
                            b.rvBudget.visibility = View.GONE
                        } else {
                            b.layoutEmptyState.visibility = View.GONE
                            b.rvBudget.visibility = View.VISIBLE
                            transactionAdapter.submitList(transactions)
                        }
                    }
                } else {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Gagal memuat data transaksi", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("BudgetFragment", "Error loading transactions: ${e.message}", e)
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}