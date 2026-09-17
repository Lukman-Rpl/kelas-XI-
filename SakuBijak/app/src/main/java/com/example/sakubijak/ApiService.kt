package com.example.sakubijak

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

// Menambahkan Header Accept JSON untuk seluruh fungsi di interface ini
interface ApiService {

    // --- AUTENTIKASI ---
    @POST("register")
    suspend fun register(
        @Body data: HashMap<String, String>
    ): Response<BaseApiResponse<AuthResponse>>

    @POST("login")
    suspend fun login(
        @Body loginData: Map<String, String>
    ): Response<AuthResponse>

    @POST("auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest
    ): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(): Response<ApiResponseGeneric>

    @GET("me")
    suspend fun getProfile(): Response<UserProfileResponse>

    @Multipart
    @POST("profile/update")
    suspend fun updateProfile(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part photo: MultipartBody.Part? = null
    ): Response<BaseApiResponse<AuthResponse>>


    // --- DASHBOARD ---
    @GET("dashboard")
    suspend fun getDashboardData(
        @Query("wallet_id") walletId: Long? = null
    ): Response<BaseApiResponse<DashboardResponse>>


    // --- WALLETS & GROUP ---
    @GET("wallets")
    suspend fun getWallets(): Response<BaseApiResponse<WalletGroupData>>

    // Diperbaiki dari "wallet/{id}" menjadi "wallets/{id}"
    @GET("wallets/{id}")
    suspend fun getWalletDetail(
        @Path("id") walletId: Long
    ): Response<BaseApiResponse<WalletResponse>>

    @POST("wallets")
    suspend fun createWallet(
        @Body request: CreateWalletRequest
    ): Response<BaseApiResponse<WalletResponse>>

    @POST("wallets/{id}/activate")
    suspend fun setActiveWallet(
        @Path("id") walletId: Long
    ): Response<BaseApiResponse<WalletResponse>>

    @GET("wallets/{id}/transactions")
    suspend fun getTransactionsByWallet(
        @Path("id") walletId: Long
    ): Response<BaseApiResponse<List<TransactionResponse>>>

    @POST("wallets/join")
    suspend fun joinGroup(
        @Body request: GroupJoinRequest
    ): Response<BaseApiResponse<WalletResponse>>

    @GET("wallets/{id}/invite-code")
    suspend fun getInviteCode(
        @Path("id") walletId: Long
    ): Response<BaseApiResponse<InviteCodeResponse>>


    // --- TRANSAKSI ---

    // 1. Tambah Transaksi Baru (Mengembalikan TransactionUpdateResponse agar dapat budget_summary)
    @POST("transactions")
    suspend fun createTransaction(
        @Body request: CreateTransactionRequest
    ): Response<TransactionUpdateResponse>

    // 4. Get List Transaksi
    @GET("transactions")
    suspend fun getTransactions(
        @Query("wallet_id") walletId: Long,
        @Query("type") type: String? = null
    ): Response<BaseApiResponse<List<TransactionResponse>>>

    // --- LAPORAN KEUANGAN ---
    // Menggunakan ReportResponse langsung (tanpa BaseApiResponse) sesuai struktur DTO Report
    @GET("reports/monthly")
    suspend fun getMonthlyReport(
        @Query("month") month: String,
        @Query("wallet_id") walletId: Long
    ): Response<ReportResponse>

    @GET("reports/weekly")
    suspend fun getWeeklyReport(
        @Query("date") date: String,
        @Query("wallet_id") walletId: Long
    ): Response<ReportResponse>

    @GET("reports/daily")
    suspend fun getDailyReport(
        @Query("date") date: String,
        @Query("wallet_id") walletId: Long
    ): Response<ReportResponse>


    // --- TOPUP ---
    @POST("topup")
    suspend fun topUp(
        @Body request: TopUpRequest
    ): Response<TopUpResponse>

    // --- TRANSFER ---
    @POST("wallets/transfer")
    suspend fun transferBalance(
        @Body request: TransferRequest
    ): Response<BaseApiResponse<TransferResultData>>

}