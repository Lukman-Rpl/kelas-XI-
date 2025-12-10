package com.example.aplikasimonitoringkelas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.model.UserData
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch

// ======================== ADMIN NAVIGATION ============================
//sealed class AdminNavItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
//    object EntriJadwal : AdminNavItem("Entri Jadwal", Icons.Default.Edit)
//    object Users : AdminNavItem("Users", Icons.Default.People)
//}
//
//// ======================== ACTIVITY ====================================
//class AdminActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            AplikasiMonitoringKelasTheme {
//                AdminScreen()
//            }
//        }
//    }
//}
//
//// ======================== ADMIN SCREEN ================================
//@Composable
//fun AdminScreen() {
//    val items = listOf(AdminNavItem.EntriJadwal, AdminNavItem.Users)
//    var selectedItem by remember { mutableStateOf<AdminNavItem>(AdminNavItem.EntriJadwal) }
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar {
//                items.forEach { item ->
//                    NavigationBarItem(
//                        selected = selectedItem == item,
//                        onClick = { selectedItem = item },
//                        icon = { Icon(item.icon, contentDescription = item.title) },
//                        label = { Text(item.title) }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//        ) {
//            when (selectedItem) {
//                is AdminNavItem.EntriJadwal -> EntriJadwalPage()
//                is AdminNavItem.Users -> UserPage()
//            }
//        }
//    }
//}
//
//// ======================== USER MANAGEMENT PAGE ========================
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UserPage() {
//    var users by remember { mutableStateOf<List<UserData>>(emptyList()) }
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var role by remember { mutableStateOf("siswa") }
//
//    val scope = rememberCoroutineScope()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { inner ->
//        Column(
//            modifier = Modifier
//                .padding(inner)
//                .padding(16.dp)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.Top
//        ) {
//            Text("👥 Manajemen User", style = MaterialTheme.typography.titleLarge)
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Nama") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Password") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = role,
//                onValueChange = { role = it },
//                label = { Text("Role (admin / kurikulum / siswa / kepala-sekolah)") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                Button(onClick = {
//                    scope.launch {
//                        try {
//                            val newUser = UserData(
//                                nama = name,
//                                email = email,
//                                password = password,
//                                role = role
//                            )
//
//                            val responseCreate = ApiClient.apiService.createUser(newUser)
//                            if (responseCreate.isSuccessful) {
//                                snackbarHostState.showSnackbar("✅ User berhasil ditambahkan!")
//                            } else {
//                                snackbarHostState.showSnackbar("❌ Gagal menambah user (${responseCreate.code()})")
//                            }
//
//                            val response = ApiClient.apiService.getUsers()
//                            if (response.isSuccessful) {
//                                users = response.body() ?: emptyList()
//                            } else {
//                                snackbarHostState.showSnackbar("❌ Gagal memuat user (${response.code()})")
//                            }
//
//                        } catch (e: Exception) {
//                            snackbarHostState.showSnackbar("❌ Error: ${e.message}")
//                        }
//                    }
//                }) {
//                    Text("Tambah User")
//                }
//
//                Button(onClick = {
//                    scope.launch {
//                        try {
//                            val response = ApiClient.apiService.getUsers()
//                            if (response.isSuccessful) {
//                                users = response.body() ?: emptyList()
//                                snackbarHostState.showSnackbar("✅ Data user diperbarui")
//                            } else {
//                                snackbarHostState.showSnackbar("❌ Gagal memuat user (${response.code()})")
//                            }
//                        } catch (e: Exception) {
//                            snackbarHostState.showSnackbar("❌ ${e.message}")
//                        }
//                    }
//                }) {
//                    Text("Lihat User")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                items(users.size) { i ->
//                    val user = users[i]
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
//                    ) {
//                        Column(Modifier.padding(12.dp)) {
//                            Text(user.nama ?: "-", style = MaterialTheme.typography.titleMedium)
//                            Text("Email: ${user.email ?: "-"}")
//                            Text("Role: ${user.role ?: "-"}")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// ======================== ENTRI JADWAL PAGE ===========================
//@Composable
//fun EntriJadwalPage() {
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//        Text("📅 Halaman Entri Jadwal (placeholder)")
//    }
//}
