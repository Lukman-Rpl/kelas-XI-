package com.example.sakubijak

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.sakubijak.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var apiService: ApiService
    private lateinit var themePref: ThemePreferences

    // Base URL untuk media storage backend Laravel
    private val storageBaseUrl = "http://192.168.137.1:8000/storage/"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Helper & Services
        themePref = ThemePreferences(requireContext())
        apiService = ApiClient.getApiService(requireContext())

        // 1. Tampilkan data dari SharedPreferences sebagai fallback cepat
        loadUserProfileFromCache()

        // 2. Setup status tema aplikasi
        setupThemeMode()

        // 3. Fetch data terbaru dari server via apiService.getProfile()
        fetchUserProfileFromApi()

        // 4. Setup listener menu
        setupClickListeners()
    }

    private fun loadUserProfileFromCache() {
        val sharedPref = requireContext().getSharedPreferences("sakubijak_pref", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("user_name", "User Sakubijak")
        val userEmail = sharedPref.getString("user_email", "user@sakubijak.com")
        val photoUrl = sharedPref.getString("user_photo_url", null)

        binding.tvUserName.text = userName
        binding.tvUserEmail.text = userEmail

        if (!photoUrl.isNullOrEmpty() && isAdded) {
            Glide.with(this)
                .load(photoUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(binding.ivProfilePic)
        }
    }

    private fun setupThemeMode() {
        val isDarkMode = themePref.isDarkMode()

        // Jika layout menggunakan Switch:
        // binding.switchDarkMode.isChecked = isDarkMode

        // Jika layout menggunakan TextView Subtitle:
        // binding.tvCurrentTheme.text = if (isDarkMode) "Gelap" else "Terang"
    }

    private fun fetchUserProfileFromApi() {
        lifecycleScope.launch {
            try {
                val response = apiService.getProfile()

                if (response.isSuccessful && response.body()?.status == true) {
                    val user: UserData? = response.body()?.user

                    user?.let { userData ->
                        val name = userData.name
                        val email = userData.email
                        val relativePhotoPath = userData.profilePhoto

                        val fullPhotoUrl = if (!relativePhotoPath.isNullOrEmpty()) {
                            if (relativePhotoPath.startsWith("http")) {
                                relativePhotoPath
                            } else {
                                storageBaseUrl + relativePhotoPath
                            }
                        } else null

                        if (_binding != null && isAdded) {
                            if (name.isNotEmpty()) binding.tvUserName.text = name
                            if (email.isNotEmpty()) binding.tvUserEmail.text = email

                            if (!fullPhotoUrl.isNullOrEmpty()) {
                                Glide.with(this@ProfileFragment)
                                    .load(fullPhotoUrl)
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .placeholder(android.R.drawable.sym_def_app_icon)
                                    .error(android.R.drawable.sym_def_app_icon)
                                    .into(binding.ivProfilePic)
                            }

                            // Simpan cache profil terbaru
                            val sharedPref = requireContext().getSharedPreferences("sakubijak_pref", Context.MODE_PRIVATE)
                            sharedPref.edit().apply {
                                putString("user_name", name)
                                putString("user_email", email)
                                putString("user_photo_url", fullPhotoUrl)
                                apply()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupClickListeners() {
        binding.menuEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.menuSecurity.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        // Listener untuk Mengganti Tema
        binding.menuTheme.setOnClickListener {
            toggleAppTheme()
        }

        binding.menuAbout.setOnClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            performLogout()
        }
    }

    private fun toggleAppTheme() {
        // Balik status mode malam saat ini
        val newDarkModeState = !themePref.isDarkMode()

        // Simpan dan langsung terapkan (ThemePreferences otomatis memanggil setDefaultNightMode)
        themePref.setDarkMode(newDarkModeState)

        setupThemeMode()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                apiService.logout()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (isAdded) {
                    val activity = requireActivity()
                    val sharedPref = activity.getSharedPreferences("sakubijak_pref", Context.MODE_PRIVATE)

                    // Hapus data sesi spesifik saja tanpa menghapus is_dark_mode
                    sharedPref.edit().apply {
                        remove("token")
                        remove("user_name")
                        remove("user_email")
                        remove("user_photo_url")
                        apply()
                    }

                    Toast.makeText(activity, "Berhasil keluar dari akun", Toast.LENGTH_SHORT).show()

                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    activity.finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchUserProfileFromApi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}