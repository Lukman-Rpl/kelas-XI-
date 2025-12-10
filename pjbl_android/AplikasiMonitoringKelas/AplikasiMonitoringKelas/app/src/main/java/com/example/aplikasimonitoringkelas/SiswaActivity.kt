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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.model.*
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import com.google.gson.Gson
import kotlinx.coroutines.launch

class SiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                SiswaScreen()
            }
        }
    }
}

@Composable
fun SiswaScreen() {
    var jadwalList by remember { mutableStateOf<List<JadwalItem>>(emptyList()) }
    var guruList by remember { mutableStateOf<List<GuruItem>>(emptyList()) }
    var mapelList by remember { mutableStateOf<List<MapelItem>>(emptyList()) }
    var ijinList by remember { mutableStateOf<List<IjinItem>>(emptyList()) }

    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelas by remember { mutableStateOf<Long?>(2L) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    var showAddJadwal by remember { mutableStateOf(false) }
    var editJadwal by remember { mutableStateOf<JadwalItem?>(null) }
    var showIjin by remember { mutableStateOf(false) }
    var showGuruPengganti by remember { mutableStateOf(false) }

    var selectedMapelId by remember { mutableStateOf<Long?>(null) }
    var selectedMapelName by remember { mutableStateOf("") }
    var selectedGuruPenggantiId by remember { mutableStateOf<Long?>(null) }
    var selectedGuruPenggantiName by remember { mutableStateOf("") }

    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val coroutineScope = rememberCoroutineScope()

    // Load data API
    LaunchedEffect(selectedHari, selectedKelas) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                // Load Guru
                val resGuru = ApiClient.apiService.getGuruList()
                if (resGuru.isSuccessful) {
                    guruList = resGuru.body()?.data?.guru ?: emptyList()
                    Log.d("DEBUG_API", "Guru loaded: ${guruList.size}")
                } else {
                    Log.e("DEBUG_API", "Guru load failed: ${resGuru.code()} ${resGuru.errorBody()?.string()}")
                }

                // Load Mapel
                val resMapel = ApiClient.apiService.getMapelList()
                if (resMapel.isSuccessful) {
                    mapelList = resMapel.body()?.data ?: emptyList()
                    Log.d("DEBUG_API", "Mapel loaded: ${mapelList.size}")
                } else {
                    Log.e("DEBUG_API", "Mapel load failed: ${resMapel.code()} ${resMapel.errorBody()?.string()}")
                }

                // Load Jadwal
                if (selectedKelas != null) {
                    val resJadwal = ApiClient.apiService.getJadwalFiltered(
                        kelasId = selectedKelas,
                        hari = selectedHari
                    )
                    val rawJson = resJadwal.body()?.let { Gson().toJson(it) }
                    Log.d("DEBUG_JADWAL", "Raw Jadwal Response: $rawJson")

                    if (resJadwal.isSuccessful) {
                        val body = resJadwal.body()
                        jadwalList = body?.data ?: emptyList()
                        Log.d("DEBUG_JADWAL_LIST", "Jumlah jadwal: ${jadwalList.size}")
                        if (jadwalList.isEmpty()) {
                            errorMessage = "Tidak ada jadwal untuk kelas ini pada hari $selectedHari"
                        }
                    } else {
                        val errorBody = resJadwal.errorBody()?.string()
                        Log.e("DEBUG_JADWAL", "Jadwal API error: ${resJadwal.code()} $errorBody")
                        errorMessage = "Gagal memuat jadwal (${resJadwal.code()})"
                    }
                }

                // Load Ijin
                val resIjin = ApiClient.apiService.getAllIjin()
                if (resIjin.isSuccessful) {
                    ijinList = resIjin.body()?.data ?: emptyList()
                    Log.d("DEBUG_API", "Ijin loaded: ${ijinList.size}")
                }

            } catch (e: Exception) {
                Log.e("DEBUG_API", "Exception: ${e.localizedMessage}")
                errorMessage = "Kesalahan jaringan: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Jadwal Pelajaran Siswa", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { showAddJadwal = true }) { Text("Tambah Jadwal") }
            Button(onClick = { showIjin = true }) { Text("Lihat Ijin") }
            Button(onClick = { showGuruPengganti = true }) { Text("Lihat Guru Pengganti") }
        }

        Spacer(Modifier.height(16.dp))

        DropdownSelectorSiswa(
            label = "Hari",
            selectedValue = selectedHari,
            options = hariList,
            onValueSelected = { selectedHari = it }
        )

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            errorMessage.isNotEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text(errorMessage) }
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(jadwalList) { jadwal ->
                    val guruNama = jadwal.guru
                    val penggantiNama = jadwal.guruPengganti ?: "-"
                    val mapelNama = jadwal.mapel.ifBlank { "-" }

                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Jam: ${jadwal.jamKe} - ${jadwal.sampaiJam}")
                            Text("Mapel: $mapelNama")
                            Text("Guru: $guruNama")
                            Text("Guru Pengganti: $penggantiNama")
                            Text("Tanggal: ${jadwal.tanggal}")
                            Text("Hari: ${jadwal.hari}")
                            Text("Status: ${jadwal.status}")
                            if (!jadwal.keterangan.isNullOrBlank()) Text("Ket: ${jadwal.keterangan}")

                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { editJadwal = jadwal }) { Text("Edit") }
                        }
                    }
                }
            }
        }
    }

    // Tambah/Edit Jadwal
    if (showAddJadwal || editJadwal != null) {
        var selectedGuru by remember { mutableStateOf(editJadwal?.guruId) }
        var jamKe by remember { mutableStateOf(editJadwal?.jamKe ?: "") }
        var sampaiJam by remember { mutableStateOf(editJadwal?.sampaiJam ?: "") }
        var tanggal by remember { mutableStateOf(editJadwal?.tanggal ?: "") }
        var status by remember { mutableStateOf(editJadwal?.status ?: "hadir") }
        var message by remember { mutableStateOf("") }

        LaunchedEffect(editJadwal) {
            editJadwal?.let {
                selectedMapelId = it.mapelId
                selectedMapelName = it.mapel
                selectedGuruPenggantiId = it.guruPenggantiId
                selectedGuruPenggantiName = it.guruPengganti ?: ""
            }
        }

        AlertDialog(
            onDismissRequest = { showAddJadwal = false; editJadwal = null },
            title = { Text(if (editJadwal != null) "Edit Jadwal" else "Tambah Jadwal") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    DropdownSelectorSiswa(
                        label = "Guru",
                        selectedValue = guruList.find { it.id_guru == selectedGuru }?.nama_guru ?: "-",
                        options = guruList.map { it.nama_guru },
                        onValueSelected = { name ->
                            selectedGuru = guruList.find { it.nama_guru == name }?.id_guru
                        }
                    )

                    DropdownSelectorSiswa(
                        label = "Guru Pengganti",
                        selectedValue = selectedGuruPenggantiName.ifBlank { "-" },
                        options = guruList.map { it.nama_guru },
                        onValueSelected = { name ->
                            selectedGuruPenggantiName = name
                            selectedGuruPenggantiId = guruList.find { it.nama_guru == name }?.id_guru
                        }
                    )

                    DropdownSelectorSiswa(
                        label = "Mapel",
                        selectedValue = selectedMapelName,
                        options = mapelList.map { it.nama_mapel },
                        onValueSelected = { name ->
                            selectedMapelName = name
                            selectedMapelId = mapelList.find { it.nama_mapel == name }?.id?.toLong()
                        }
                    )

                    OutlinedTextField(value = jamKe, onValueChange = { jamKe = it }, label = { Text("Jam Ke") })
                    OutlinedTextField(value = sampaiJam, onValueChange = { sampaiJam = it }, label = { Text("Sampai Jam") })
                    OutlinedTextField(value = tanggal, onValueChange = { tanggal = it }, label = { Text("Tanggal") })

                    DropdownSelectorSiswa(
                        label = "Status",
                        selectedValue = status,
                        options = listOf("hadir", "tidak_hadir", "terlambat"),
                        onValueSelected = { status = it }
                    )

                    if (message.isNotBlank()) Text(message, color = MaterialTheme.colorScheme.error)
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        try {
                            if (selectedKelas == null || selectedGuru == null || selectedMapelId == null) {
                                message = "Pilih kelas, guru, dan mapel!"
                                return@launch
                            }

                            val request = JadwalCreateRequest(
                                hari = selectedHari,
                                tanggal = tanggal,
                                kelasId = selectedKelas!!,
                                jamKe = jamKe,
                                sampaiJam = sampaiJam,
                                mapelId = selectedMapelId!!,
                                guruId = selectedGuru!!,
                                guruPenggantiId = selectedGuruPenggantiId,
                                status = status,
                                keterangan = null
                            )

                            val res = if (editJadwal != null)
                                ApiClient.apiService.updateJadwal(editJadwal!!.id, request)
                            else
                                ApiClient.apiService.createJadwal(request)

                            if (res.isSuccessful) {
                                showAddJadwal = false
                                editJadwal = null
                                val resJadwal = ApiClient.apiService.getJadwalFiltered(
                                    kelasId = selectedKelas!!,
                                    hari = selectedHari
                                )
                                if (resJadwal.isSuccessful) {
                                    jadwalList = resJadwal.body()?.data ?: emptyList()
                                }
                            } else {
                                 message = "Gagal simpan jadwal (${res.code()})"
                            }

                        } catch (e: Exception) {
                            message = "Kesalahan: ${e.localizedMessage}"
                        }
                    }
                }) { Text("Simpan") }
            },
            dismissButton = {
                Button(onClick = { showAddJadwal = false; editJadwal = null }) { Text("Batal") }
            }
        )
    }

    // Lihat Ijin Guru
    if (showIjin) {
        AlertDialog(
            onDismissRequest = { showIjin = false },
            title = { Text("Daftar Ijin Guru") },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ijinList) { ijin ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(8.dp)) {
                                Text("Guru: ${ijin.namaGuru}")
                                Text("Mulai: ${ijin.tanggalMulai}")
                                Text("Selesai: ${ijin.tanggalSelesai}")
                                Text("Status: ${ijin.status}")
                                if (!ijin.keterangan.isNullOrBlank()) Text("Keterangan: ${ijin.keterangan}")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showIjin = false }) { Text("Tutup") }
            }
        )
    }

    // Lihat Guru Pengganti
    if (showGuruPengganti) {
        GuruPenggantiScreen(
            guruList = guruList,
            onDismiss = { showGuruPengganti = false }
        )
    }
}

@Composable
fun GuruPenggantiScreen(
    guruList: List<GuruItem>,
    onDismiss: () -> Unit
) {
    var guruPenggantiList by remember { mutableStateOf<List<GuruPenggantiItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loading = true
        try {
            val res = ApiClient.apiService.getGuruPenggantiList()
            if (res.isSuccessful) {
                guruPenggantiList = res.body()?.data ?: emptyList()
                if (guruPenggantiList.isEmpty()) error = "Tidak ada guru pengganti"
            } else {
                error = "Gagal memuat data (${res.code()})"
            }
        } catch (e: Exception) {
            error = "Kesalahan jaringan: ${e.localizedMessage}"
        } finally {
            loading = false
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daftar Guru Pengganti") },
        text = {
            Box(Modifier.fillMaxWidth().height(300.dp)) {
                when {
                    loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                    error.isNotEmpty() -> Text(error, Modifier.align(Alignment.Center))
                    else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(guruPenggantiList) { item ->
                            val namaGuruAsli = guruList.find { it.id_guru == item.guru_id }?.nama_guru ?: "-"
                            val namaGuruPengganti = item.nama ?: "-"
                            Card(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(8.dp)) {
                                    Text("Guru Asli: $namaGuruAsli")
                                    Text("Guru Pengganti: $namaGuruPengganti")
                                    Text("Mapel ID: ${item.mapel_id ?: "-"}")
                                    Text("Kelas ID: ${item.kelas_id ?: "-"}")
                                    Text("Tanggal: ${item.tanggal}")
                                    Text("Jam: ${item.jam ?: "-"}")
                                    Text("Keterangan: ${item.keterangan ?: "-"}")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Tutup") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelectorSiswa(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
