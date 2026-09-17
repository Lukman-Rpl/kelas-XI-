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
import com.example.sakubijak.databinding.FragmentReportBinding
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class  ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    private lateinit var prefManager: PreferenceManager
    private val categoryAdapter by lazy { ReportCategoryAdapter() }

    private enum class PeriodMode {
        DAILY, WEEKLY, MONTHLY
    }

    private var currentMode = PeriodMode.MONTHLY
    private val calendar: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = ApiClient.getApiService(requireContext())
        prefManager = PreferenceManager(requireContext())

        setupRecyclerView()
        setupPeriodToggle()

        binding.btnDateFilter.setOnClickListener {
            showDatePicker()
        }

        // Ambil data laporan pertama kali
        fetchReportData()
    }

    private fun setupRecyclerView() {
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
    }

    private fun setupPeriodToggle() {
        binding.togglePeriod.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_daily -> currentMode = PeriodMode.DAILY
                    R.id.btn_weekly -> currentMode = PeriodMode.WEEKLY
                    R.id.btn_monthly -> currentMode = PeriodMode.MONTHLY
                }
                fetchReportData()
            }
        }
    }

    private fun showDatePicker() {
        val title = when (currentMode) {
            PeriodMode.DAILY -> "Pilih Tanggal Laporan"
            PeriodMode.WEEKLY -> "Pilih Tanggal dalam Minggu"
            PeriodMode.MONTHLY -> "Pilih Bulan Laporan"
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(title)
            .setSelection(calendar.timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            calendar.timeInMillis = selection
            fetchReportData()
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    /**
     * Memanggil API Laporan berdasarkan Wallet Aktif, Periode, dan Tanggal yang dipilih
     */
    private fun fetchReportData() {
        val activeWalletId = prefManager.getActiveWalletId()
        if (activeWalletId == -1L) return

        val dateFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)
        val monthFormatted = SimpleDateFormat("yyyy-MM", Locale.US).format(calendar.time)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = when (currentMode) {
                    PeriodMode.DAILY -> apiService.getDailyReport(dateFormatted, activeWalletId)
                    PeriodMode.WEEKLY -> apiService.getWeeklyReport(dateFormatted, activeWalletId)
                    PeriodMode.MONTHLY -> apiService.getMonthlyReport(monthFormatted, activeWalletId)
                }

                if (response.isSuccessful && response.body() != null) {
                    val reportData = response.body()!!.data
                    bindReportToUi(reportData)//Argument type mismatch: actual type is 'ReportResponse', but 'ReportData' was expected.
                } else {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Gagal memuat data laporan", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ReportFragment", "Error loading report: ${e.message}", e)
            }
        }
    }

    /**
     * Mengisi data langsung dari ReportData DTO ke UI View
     */
    private fun bindReportToUi(data: ReportData) {
        _binding?.let { b ->
            // 1. Set Label Periode di Button (Diambil langsung dari Backend)
            b.btnDateFilter.text = data.period.label

            // 2. Set Total Pengeluaran & Pemasukan (Menggunakan String Formatted dari Backend)
            b.tvTotalExpense.text = data.summary.totalExpenseFormatted
            b.tvTotalIncome.text = data.summary.totalIncomeFormatted

            // 3. Set List Kategori
            if (data.categories.isEmpty()) {
                b.layoutEmptyStateReport.visibility = View.VISIBLE
                b.rvCategories.visibility = View.GONE
            } else {
                b.layoutEmptyStateReport.visibility = View.GONE
                b.rvCategories.visibility = View.VISIBLE
                categoryAdapter.submitList(data.categories)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}