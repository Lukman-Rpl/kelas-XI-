package com.example.aplikasimonitoringkelas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.model.*
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch

class KurikulumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                KurikulumScreen()
            }
        }
    }
}

sealed class KurikulumNavItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Jadwal : KurikulumNavItem("Jadwal", Icons.Default.Home)
    object Edit : KurikulumNavItem("Edit", Icons.Default.Edit)
    object Ijin : KurikulumNavItem("Ijin", Icons.Default.Home)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumScreen() {
    val items = listOf(KurikulumNavItem.Jadwal, KurikulumNavItem.Ijin, KurikulumNavItem.Edit)
    var selectedItem by remember { mutableStateOf<KurikulumNavItem>(KurikulumNavItem.Jadwal) }

    Scaffold(bottomBar = {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedItem == item,
                    onClick = { selectedItem = item },
                    icon = { Icon(item.icon, item.title) },
                    label = { Text(item.title) }
                )
            }
        }
    }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedItem) {
                is KurikulumNavItem.Jadwal -> JadwalPageKurikulum(padding)
                is KurikulumNavItem.Edit -> EditPageKurikulum(padding)
                is KurikulumNavItem.Ijin -> IjinPageKurikulum()
            }
        }
    }
}

// ===============================
// Jadwal Page
// ===============================
@Composable
fun JadwalPageKurikulum(padding: PaddingValues) {
    var selectedKelas by remember { mutableStateOf<KelasItem?>(null) }
    var selectedHari by remember { mutableStateOf("Senin") }
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")

    var kelasList by remember { mutableStateOf<List<KelasItem>>(emptyList()) }
    var jadwalList by remember { mutableStateOf<List<JadwalItem>>(emptyList()) }
    var guruList by remember { mutableStateOf<List<GuruItem>>(emptyList()) }

    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load kelas & guru
    LaunchedEffect(Unit) {
        loading = true
        try {
            val resKelas = ApiClient.apiService.getKelasList()
            if (resKelas.isSuccessful) kelasList = resKelas.body()?.data ?: emptyList()

            val resGuru = ApiClient.apiService.getGuruList()
            if (resGuru.isSuccessful) guruList = resGuru.body()?.data?.guru ?: emptyList()

            Log.d("DEBUG_API", "Kelas loaded: ${kelasList.size}, Guru loaded: ${guruList.size}")
        } catch (e: Exception) {
            errorMessage = "Kesalahan memuat data: ${e.message}"
            Log.e("DEBUG_API", errorMessage ?: "unknown")
        }
        loading = false
    }

    // Load jadwal
    LaunchedEffect(selectedKelas, selectedHari) {
        if (selectedKelas == null) return@LaunchedEffect

        loading = true
        try {
            val kelasIdNonNull = selectedKelas!!.id
            val res = ApiClient.apiService.getJadwalFiltered(
                kelasId = kelasIdNonNull,
                hari = selectedHari
            )

            if (res.isSuccessful) {
                jadwalList = res.body()?.data ?: emptyList()
                errorMessage = if (jadwalList.isEmpty())
                    "Tidak ada jadwal untuk ${selectedKelas!!.namaKelas} hari $selectedHari"
                else null
                Log.d("DEBUG_API", "Jadwal loaded: ${jadwalList.size}")
            } else {
                errorMessage = "Gagal load jadwal (${res.code()})"
                Log.e("DEBUG_API", errorMessage ?: "unknown")
            }

        } catch (e: Exception) {
            errorMessage = "Kesalahan memuat jadwal: ${e.message}"
            Log.e("DEBUG_API", errorMessage ?: "unknown")
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Jadwal Kurikulum", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        DropdownSelectorKurikulum(
            label = "Pilih Kelas",
            selectedValue = selectedKelas?.namaKelas ?: "Pilih...",
            options = kelasList.map { it.namaKelas },
            onValueSelected = { nama ->
                selectedKelas = kelasList.find { it.namaKelas == nama }
            }
        )
        Spacer(Modifier.height(12.dp))

        DropdownSelectorKurikulum(
            label = "Pilih Hari",
            selectedValue = selectedHari,
            options = hariList,
            onValueSelected = { selectedHari = it }
        )

        Spacer(Modifier.height(16.dp))

        when {
            loading ->
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }

            !errorMessage.isNullOrBlank() ->
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text(errorMessage!!) }

            else ->
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(jadwalList) { jadwal ->
                        val guruNama = guruList.find { it.id_guru == jadwal.guruId }?.nama_guru ?: "Guru Tidak Ditemukan"
                        val penggantiNama = guruList.find { it.id_guru == jadwal.guruPenggantiId }?.nama_guru ?: "-"
                        val mapelNama = jadwal.mapel.ifBlank { "-" }

                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                Text("Jam: ${jadwal.jamKe} - ${jadwal.sampaiJam}")
                                Text("Mapel: $mapelNama")
                                Text("Guru: $guruNama")
                                Text("Pengganti: $penggantiNama")
                                Text("Tanggal: ${jadwal.tanggal}")
                                Text("Hari: ${jadwal.hari}")
                                Text("Status: ${jadwal.status}")
                                if (!jadwal.keterangan.isNullOrBlank())
                                    Text("Ket: ${jadwal.keterangan}")
                            }
                        }
                    }
                }
        }
    }
}

// ===============================
// Edit Jadwal
// ===============================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPageKurikulum(padding: PaddingValues) {
    val scope = rememberCoroutineScope()
    var jadwalId by remember { mutableStateOf("") }
    var selectedGuruId by remember { mutableStateOf<Long?>(null) }
    var message by remember { mutableStateOf("") }

    var jadwalData by remember { mutableStateOf<JadwalItem?>(null) }
    var guruList by remember { mutableStateOf<List<GuruItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val res = ApiClient.apiService.getGuruList()
            if (res.isSuccessful) guruList = res.body()?.data?.guru ?: emptyList()
        } catch (e: Exception) {
            message = "Gagal memuat daftar guru: ${e.localizedMessage}"
        }
    }

    Column(
        modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Edit Jadwal (Ubah Guru)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = jadwalId,
            onValueChange = { jadwalId = it },
            label = { Text("ID Jadwal") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (jadwalId.isBlank()) {
                    message = "ID Jadwal tidak boleh kosong."
                    return@Button
                }
                scope.launch {
                    try {
                        val res = ApiClient.apiService.getJadwalById(jadwalId.toLong())
                        if (res.isSuccessful) {
                            jadwalData = res.body()?.data
                            selectedGuruId = jadwalData?.guruPenggantiId
                            message = ""
                        } else {
                            message = "Jadwal tidak ditemukan (${res.code()})"
                        }
                    } catch (e: Exception) {
                        message = "Kesalahan: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Tampilkan Jadwal") }

        Spacer(Modifier.height(16.dp))

        jadwalData?.let { jadwal ->
            Text("Mapel: ${jadwal.mapel}")
            Text("Guru Lama: ${guruList.find { it.id_guru == jadwal.guruId }?.nama_guru ?: "-"}")

            Spacer(Modifier.height(12.dp))

            DropdownSelectorKurikulum(
                label = "Pilih Guru Pengganti",
                selectedValue = selectedGuruId?.let { id -> guruList.find { it.id_guru == id }?.nama_guru } ?: "Pilih...",
                options = guruList.map { it.nama_guru },
                onValueSelected = { nama ->
                    selectedGuruId = guruList.find { it.nama_guru == nama }?.id_guru
                }
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (selectedGuruId == null) {
                        message = "Pilih guru pengganti!"
                        return@Button
                    }
                    scope.launch {
                        try {
                            val kelasIdNonNull = jadwal.kelasId ?: run { message = "Kelas ID tidak valid"; return@launch }
                            val mapelIdNonNull = jadwal.mapelId ?: run { message = "Mapel ID tidak valid"; return@launch }
                            val guruIdNonNull = jadwal.guruId ?: run { message = "Guru ID tidak valid"; return@launch }
                            val guruPenggantiIdNonNull = selectedGuruId ?: run { message = "Pilih guru pengganti!"; return@launch }

                            val request = JadwalCreateRequest(
                                hari = jadwal.hari,
                                tanggal = jadwal.tanggal,
                                kelasId = kelasIdNonNull,
                                jamKe = jadwal.jamKe,
                                sampaiJam = jadwal.sampaiJam,
                                mapelId = mapelIdNonNull,
                                guruId = guruIdNonNull,
                                guruPenggantiId = guruPenggantiIdNonNull,
                                status = jadwal.status,
                                keterangan = jadwal.keterangan
                            )
                            val res = ApiClient.apiService.updateJadwal(jadwal.id, request)
                            message = if (res.isSuccessful) "Berhasil update guru pengganti" else "Gagal (${res.code()})"
                        } catch (e: Exception) {
                            message = "Kesalahan: ${e.localizedMessage}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Simpan") }
        }

        Spacer(Modifier.height(16.dp))
        Text(message)
    }
}

// ===============================
// Ijin Page
// ===============================
@Composable
fun IjinPageKurikulum() {
    var listIjin by remember { mutableStateOf<List<IjinItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try {
            val res = ApiClient.apiService.getAllIjin()
            listIjin = if (res.isSuccessful) {
                res.body()?.data ?: emptyList()
            } else {
                error = "Gagal memuat izin (${res.code()})"
                emptyList()
            }
        } catch (e: Exception) {
            error = "Kesalahan jaringan: ${e.message}"
        }
        loading = false
    }

    Box(Modifier.fillMaxSize().padding(16.dp)) {
        when {
            loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            error.isNotEmpty() -> Text(error, Modifier.align(Alignment.Center))
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(listIjin) { ijin ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Guru: ${ijin.namaGuru}")
                            Text("Mulai: ${ijin.tanggalMulai}")
                            Text("Selesai: ${ijin.tanggalSelesai}")
                            Text("Status: ${ijin.status}")
                            Text("Keterangan: ${ijin.keterangan ?: "-"}")
                        }
                    }
                }
            }
        }
    }
}

// ===============================
// Dropdown Selector
// ===============================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelectorKurikulum(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedValue,
            readOnly = true,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = { Text(opt) },
                    onClick = {
                        onValueSelected(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}
