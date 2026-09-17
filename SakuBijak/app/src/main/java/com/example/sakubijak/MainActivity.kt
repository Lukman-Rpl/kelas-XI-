package com.example.sakubijak

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sakubijak.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apiService: ApiService
    private lateinit var prefManager: PreferenceManager

    private var isFabExpanded = false

    // Variable untuk menyimpan state dan nilai saldo aktif
    private var isBalanceVisible = true
    private var currentBalance: Double = 0.0

    val walletManagerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshHomeFragment()
        }
    }

    private val transactionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshHomeFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        // Selalu perbarui saldo & data cashflow saat Activity kembali ke layar utama
        checkAndUpdateCashflow()
        setupProfileData() // Perbarui foto / info profil jika ada perubahan
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefManager = PreferenceManager(this)

        if (!checkAuthentication()) {
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = ApiClient.getApiService(this)

        setupBottomNavListener()
        setupFabSpeedDial()
        setupToggleBalance() // Listener untuk sembunyikan/tampilkan saldo
        setupProfileHeader()  // <-- LISTENER BARU UNTUK FOTO PROFIL

        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.nav_home
            replaceFragment(HomeFragment())
        }

        checkAndUpdateCashflow()
    }

    private fun checkAuthentication(): Boolean {
        val token = prefManager.getToken()
        if (token.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return false
        }
        return true
    }

    /**
     * Konfigurasi Aksi Tombol / Foto Profil di Header
     */
    private fun setupProfileHeader() {
        binding.ivProfile.setOnClickListener {
            // Pindah ke ProfileFragment saat foto profil diklik
            replaceFragment(ProfileFragment())
            binding.bottomNavigation.selectedItemId = R.id.nav_profile
        }

        setupProfileData()
    }

    /**
     * Menampilkan/Memuat Data Gambar Profil
     */
    private fun setupProfileData() {
        lifecycleScope.launch {
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful) {
                    val user = response.body()?.user

                    // 1. Ambil path foto dari UserData (sesuai properti profilePhoto)
                    val photoPath = user?.profilePhoto

                    if (!photoPath.isNullOrEmpty()) {
                        // 2. Gabungkan dengan Base URL jika API hanya mengembalikan relative path
                        val fullImageUrl = if (photoPath.startsWith("http")) {
                            photoPath
                        } else {
                            "http://192.168.137.1:8000/storage/" + photoPath.removePrefix("/")
                        }

                        // 3. Muat gambar menggunakan Glide
                        Glide.with(this@MainActivity)
                            .load(fullImageUrl)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .placeholder(android.R.drawable.sym_def_app_icon)
                            .error(android.R.drawable.sym_def_app_icon)
                            .into(binding.ivProfile)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Gagal memuat foto profil: ${e.message}")
            }
        }
    }

    private fun setupBottomNavListener() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_budget -> {
                    replaceFragment(BudgetFragment())
                    true
                }
                R.id.nav_input_code -> {
                    val intent = Intent(this, JoinGroupActivity::class.java)
                    walletManagerLauncher.launch(intent)
                    false
                }
                R.id.nav_report -> {
                    replaceFragment(ReportFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFabSpeedDial() {
        binding.fabAdd.setOnClickListener {
            if (isFabExpanded) collapseFab() else expandFab()
        }

        binding.fabOverlay.setOnClickListener {
            collapseFab()
        }

        // Action Listener untuk Transfer
        binding.fabTransfer.setOnClickListener {
            collapseFab()
            val intent = Intent(this, TransferActivity::class.java)
            transactionLauncher.launch(intent)
        }

        binding.fabTopup.setOnClickListener {
            collapseFab()
            val intent = Intent(this, TopUpActivity::class.java)
            transactionLauncher.launch(intent)
        }

        binding.fabExpense.setOnClickListener {
            collapseFab()
            val intent = Intent(this, AddTransactionActivity::class.java)
            transactionLauncher.launch(intent)
        }
    }

    /**
     * Listener untuk tombol toggle mata/sensor saldo
     */
    private fun setupToggleBalance() {
        binding.ivToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            displayTotalBalance(currentBalance)
        }
    }

    private fun expandFab() {
        isFabExpanded = true
        binding.layoutFabTransfer.visibility = View.VISIBLE
        binding.layoutFabTopup.visibility = View.VISIBLE
        binding.layoutFabExpense.visibility = View.VISIBLE
        binding.fabOverlay.visibility = View.VISIBLE
        binding.fabAdd.animate().rotation(45f).setDuration(200).start()
    }

    private fun collapseFab() {
        isFabExpanded = false
        binding.layoutFabTransfer.visibility = View.GONE
        binding.layoutFabTopup.visibility = View.GONE
        binding.layoutFabExpense.visibility = View.GONE
        binding.fabOverlay.visibility = View.GONE
        binding.fabAdd.animate().rotation(0f).setDuration(200).start()
    }

    fun checkAndUpdateCashflow() {
        lifecycleScope.launch {
            try {
                val response = apiService.getWallets()
                if (response.isSuccessful && response.body()?.status == "success") {

                    // PERBAIKAN: Ambil properti .all dari objek WalletGroupData
                    val groupData = response.body()?.data
                    val walletList: List<WalletResponse> = groupData?.all ?: emptyList()

                    val savedActiveId = prefManager.getActiveWalletId()

                    // Cari wallet yang sedang aktif dari walletList
                    val activeWallet = walletList.find { it.id == savedActiveId }
                        ?: walletList.find { it.isActive == true }
                        ?: walletList.firstOrNull()

                    activeWallet?.let { wallet ->
                        prefManager.saveActiveWalletId(wallet.id)

                        // 1. Ambil saldo langsung dari objek Wallet
                        currentBalance = wallet.getSafeBalance()
                        displayTotalBalance(currentBalance)

                        // 2. Ambil akumulasi transaksi untuk card Pemasukan & Pengeluaran
                        updateCashflowCard(wallet.id)
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Gagal mengambil data wallet: ${e.message}")
            }
        }
    }

    fun updateCashflowCard(walletId: Long) {
        lifecycleScope.launch {
            try {
                val response = apiService.getTransactions(walletId = walletId)

                if (response.isSuccessful) {
                    val transactions: List<TransactionResponse> = response.body()?.data ?: emptyList()

                    val totalIncome = transactions
                        .filter {
                            it.type?.equals("income", ignoreCase = true) == true ||
                                    it.type?.equals("in", ignoreCase = true) == true ||
                                    it.type?.equals("topup", ignoreCase = true) == true
                        }
                        .sumOf { it.amount ?: 0.0 }

                    val totalExpense = transactions
                        .filter {
                            it.type?.equals("expense", ignoreCase = true) == true ||
                                    it.type?.equals("out", ignoreCase = true) == true ||
                                    it.type?.equals("transfer", ignoreCase = true) == true
                        }
                        .sumOf { it.amount ?: 0.0 }

                    binding.tvIncome.text = formatRupiah(totalIncome)
                    binding.tvExpense.text = formatRupiah(totalExpense)
                } else {
                    Log.e("MainActivity", "Gagal memuat data transaksi cashflow: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error cashflow calculation: ${e.message}")
            }
        }
    }

    private fun displayTotalBalance(balance: Double) {
        if (isBalanceVisible) {
            binding.tvTotalBalance.text = formatRupiah(balance)
            binding.ivToggleBalance.setImageResource(android.R.drawable.ic_menu_view)
        } else {
            binding.tvTotalBalance.text = "Rp ••••••••"
            binding.ivToggleBalance.setImageResource(android.R.drawable.ic_secure)
        }
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
        }
        return format.format(number)
    }

    private fun refreshHomeFragment() {
        checkAndUpdateCashflow()
        replaceFragment(HomeFragment())
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun openWalletManager() {
        val intent = Intent(this, WalletListActivity::class.java)
        walletManagerLauncher.launch(intent)
    }

    fun openReportActivity() {
        replaceFragment(ReportFragment())
        binding.bottomNavigation.selectedItemId = R.id.nav_report
    }

    fun openWalletListActivity() {
        val intent = Intent(this, WalletListActivity::class.java)
        walletManagerLauncher.launch(intent)
    }
}