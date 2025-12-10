package com.example.aplikasimonitoringkelas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.model.JadwalItem
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch

class KepalaSekolahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiMonitoringKelasTheme {
                JadwalPageKepsek()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPageKepsek() {

    var jadwalList by remember { mutableStateOf<List<JadwalItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val kelasList = listOf("10", "11", "12")

    var selectedHari by remember { mutableStateOf(hariList.first()) }
    var selectedKelas by remember { mutableStateOf(kelasList.first()) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedHari, selectedKelas) {
        scope.launch {
            isLoading = true
            errorMessage = ""

            try {
                val response = ApiClient.apiService.getJadwalFiltered(
                    kelasId = selectedKelas.toLongOrNull(),
                    hari = selectedHari
                )

                if (response.isSuccessful) {

                    val result = response.body()

                    if (result != null && result.success) {

                        // ⬇⬇⬇ SUDAH MENERIMA ARRAY LANGSUNG ⬇⬇⬇
                        jadwalList = result.data ?: emptyList()

                        if (jadwalList.isEmpty()) {
                            errorMessage = "Tidak ada jadwal untuk kelas $selectedKelas hari $selectedHari"
                        }

                    } else {
                        errorMessage = "Gagal memuat data"
                    }

                } else {
                    errorMessage = "Error server: ${response.code()}"
                }

            } catch (e: Exception) {
                errorMessage = "Tidak dapat terhubung: ${e.localizedMessage}"
            }

            isLoading = false
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text("📘 Jadwal Pelajaran", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                DropdownSelectorKepsek(
                    label = "Hari",
                    selected = selectedHari,
                    options = hariList,
                    onSelect = { selectedHari = it },
                    modifier = Modifier.weight(1f)
                )

                DropdownSelectorKepsek(
                    label = "Kelas",
                    selected = selectedKelas,
                    options = kelasList,
                    onSelect = { selectedKelas = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            when {
                isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                errorMessage.isNotEmpty() ->
                    Text(errorMessage, color = MaterialTheme.colorScheme.error)

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(jadwalList) { item ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {

                                Text("Hari: ${item.hari}")
                                Text("Tanggal: ${item.tanggal}")
                                Text("Jam ke: ${item.jamKe} - ${item.sampaiJam}")
                                Text("Mapel: ${item.mapel}")
                                Text("Kelas ID: ${item.kelasId ?: "-"}")
                                Text("Guru ID: ${item.guruId ?: "-"}")
                                Text("Guru Pengganti: ${item.guruPenggantiId ?: "-"}")
                                Text("Status: ${item.status}")

                                item.keterangan?.let {
                                    if (it.isNotEmpty()) Text("Keterangan: $it")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelectorKepsek(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = modifier.menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
