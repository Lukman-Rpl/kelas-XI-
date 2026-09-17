package com.example.sakubijak

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sakubijak.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var budgetAdapter: BudgetAdapter
    private lateinit var prefManager: PreferenceManager
    private var tabLayoutMediator: TabLayoutMediator? = null

    private val apiService: ApiService by lazy {
        ApiClient.getApiService(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PreferenceManager(requireContext())

        setupRecyclerView()
        setupViewPagerBudget()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun setupRecyclerView() {
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())

        transactionAdapter = TransactionAdapter {
            navigateToBudgetFragment()
        }
        binding.rvRecentTransactions.adapter = transactionAdapter
    }

    private fun setupViewPagerBudget() {
        budgetAdapter = BudgetAdapter {
            navigateToBudgetFragment()
        }
        binding.viewPagerBudget.adapter = budgetAdapter

        tabLayoutMediator = TabLayoutMediator(binding.tabLayoutBudgetIndicator, binding.viewPagerBudget) { _, _ ->
            // Dibiarkan kosong agar hanya menampilkan bulatan indikator tanpa teks
        }
        tabLayoutMediator?.attach()

        binding.tabLayoutBudgetIndicator.post {
            forceSquareTabDots()
        }
    }

    private fun forceSquareTabDots() {
        val tabStrip = binding.tabLayoutBudgetIndicator.getChildAt(0) as? ViewGroup ?: return
        val sizePx = (16 * resources.displayMetrics.density).toInt()

        for (i in 0 until tabStrip.childCount) {
            val tab = tabStrip.getChildAt(i)
            val params = tab.layoutParams
            params.width = sizePx
            params.height = sizePx
            tab.layoutParams = params
            tab.requestLayout()
        }
    }

    private fun setupListeners() {
        binding.btnMoreTips.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Waspadai pengeluaran kecil harian seperti camilan atau kopi karena jumlahnya bisa membengkak dalam sebulan",
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.cardAnalysis.setOnClickListener {
            navigateToFragment(ReportFragment())
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_report
        }

        binding.cardTarget.setOnClickListener {
            val intent = Intent(requireContext(), WalletListActivity::class.java)
            startActivity(intent)
        }

        binding.tvSeeAll.setOnClickListener {
            navigateToBudgetFragment()
        }
    }

    private fun navigateToBudgetFragment() {
        navigateToFragment(BudgetFragment())
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_budget
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadData() {
        val activeWalletId = prefManager.getActiveWalletId()

        if (activeWalletId == null || activeWalletId == -1L) {
            hideBudgetSection()
            showEmptyTransactions()
            return
        }

        lifecycleScope.launch {
            try {
                val response = apiService.getDashboardData(activeWalletId)

                if (response.isSuccessful) {
                    val dashboardData = response.body()?.data
                    val currentMonthYear = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(Date())

                    // 1. PROSES DATA BUDGET & CATEGORIES FOR VIEWPAGER2
                    val budgetItemList = mutableListOf<BudgetItem>()

                    dashboardData?.walletSummary?.let {
                        budgetItemList.add(it.toBudgetItem())
                    }

                    dashboardData?.categoryBudgets?.forEach { categoryBudget ->
                        budgetItemList.add(categoryBudget.toBudgetItem(currentMonthYear))
                    }

                    if (budgetItemList.isNotEmpty()) {
                        showBudgetSection()
                        budgetAdapter.submitList(budgetItemList) {
                            binding.tabLayoutBudgetIndicator.post {
                                forceSquareTabDots()
                            }
                        }
                    } else {
                        hideBudgetSection()
                    }

                    // 2. PROSES DATA RECENT TRANSACTIONS
                    val recentTxList = dashboardData?.recentTransactions ?: emptyList()

                    if (recentTxList.isNotEmpty()) {
                        val transactionResponseList = recentTxList.map { dto ->
                            TransactionResponse(
                                id = dto.id.toInt(),
                                userId = 0,
                                walletId = activeWalletId.toInt(),
                                wallet = null,
                                categoryId = 0,
                                category = CategoryResponse(
                                    id = 0,
                                    categories = dto.category ?: "Umum",
                                    limitAmount = "0", // Diubah menjadi String
                                    walletId = activeWalletId.toInt()
                                ),
                                amount = dto.amount,
                                type = dto.type,
                                date = dto.date,
                                description = null
                            )
                        }

                        transactionAdapter.submitList(transactionResponseList)
                        showTransactionsList()
                    } else {
                        showEmptyTransactions()
                    }

                } else {
                    hideBudgetSection()
                    showEmptyTransactions()
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "Gagal memuat data dashboard: ${e.message}")
                hideBudgetSection()
                showEmptyTransactions()
            }
        }
    }

    private fun showTransactionsList() {
        binding.rvRecentTransactions.visibility = View.VISIBLE
        binding.tvEmptyPlaceholder.visibility = View.GONE
    }

    private fun showEmptyTransactions() {
        binding.rvRecentTransactions.visibility = View.GONE
        binding.tvEmptyPlaceholder.visibility = View.VISIBLE
    }

    private fun showBudgetSection() {
        binding.viewPagerBudget.visibility = View.VISIBLE
        binding.tabLayoutBudgetIndicator.visibility = View.VISIBLE
    }

    private fun hideBudgetSection() {
        binding.viewPagerBudget.visibility = View.GONE
        binding.tabLayoutBudgetIndicator.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
        _binding = null
    }
}