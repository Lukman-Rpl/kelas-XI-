package com.example.sakubijak

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.sakubijak.databinding.DialogAddWalletBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.json.JSONObject

class AddWalletBottomSheet(
    // Menggunakan WalletResponse, bukan data class Wallet terpisah
    private val onWalletAdded: (wallet: WalletResponse) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogAddWalletBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi ApiService
        apiService = ApiClient.getApiService(requireContext())

        binding.btnSaveWallet.setOnClickListener {
            savePersonalWallet()
        }
    }

    private fun savePersonalWallet() {
        val walletName = binding.etWalletName.text.toString().trim()

        binding.tilWalletName.error = null

        if (walletName.isEmpty()) {
            binding.tilWalletName.error = "Nama dompet wajib diisi"
            return
        }

        setLoadingState(true)

        lifecycleScope.launch {
            try {
                // Request Payload menggunakan CreateWalletRequest
                val requestPayload = CreateWalletRequest(
                    name = walletName,
                    type = "personal",
                    balance = 0.0
                )

                val response = apiService.createWallet(requestPayload)

                if (response.isSuccessful && response.body()?.status == "success") {
                    val createdWallet: WalletResponse? = response.body()?.data

                    if (createdWallet != null) {
                        Toast.makeText(
                            requireContext(),
                            "Dompet '${createdWallet.name}' berhasil dibuat!",
                            Toast.LENGTH_SHORT
                        ).show()

                        onWalletAdded(createdWallet)
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Gagal memproses data dompet", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (!errorBody.isNullOrEmpty()) {
                        JSONObject(errorBody).optString("message", "Gagal menambahkan dompet")
                    } else {
                        "Gagal menambahkan dompet"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Koneksi Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.btnSaveWallet.isEnabled = false
            binding.btnSaveWallet.text = "Menyimpan..."
        } else {
            binding.btnSaveWallet.isEnabled = true
            binding.btnSaveWallet.text = "Simpan Dompet"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddWalletBottomSheet"
    }
}