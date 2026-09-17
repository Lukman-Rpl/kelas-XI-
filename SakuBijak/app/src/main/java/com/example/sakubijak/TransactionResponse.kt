package com.example.sakubijak

import com.google.gson.annotations.SerializedName

// 1. Wrapper untuk Response Tunggal (Store, Show, Destroy, Top Up)
data class BaseTransactionResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("current_balance") val currentBalance: Double? = null,
    @SerializedName("data") val data: TransactionResponse? = null
)

// 2. Wrapper untuk Response List (Index / GET Transactions)
data class TransactionListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<TransactionResponse> = emptyList()
)

// 3. Model Detail Transaksi
data class TransactionResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("wallet_id") val walletId: Int?,
    @SerializedName("category_id") val categoryId: Int?,
    @SerializedName("type") val type: String?,
    @SerializedName("amount") val amount: Double?, // Mengakomodasi string/double dari JSON
    @SerializedName("description") val description: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("wallet") val wallet: WalletResponse?,

    // Sangat bagus: Mengantisipasi penamaan relasi tunggal maupun jamak dari Laravel
    @SerializedName("category", alternate = ["categories"])
    val category: CategoryResponse? = null
)

// Data class untuk membungkus object data dari Laravel Pagination
data class TransactionPaginatedResponse(
    val status: String?,
    val message: String?,
    val data: TransactionDataContainer?
)

data class TransactionDataContainer(
    val current_page: Int?,
    val data: List<TransactionResponse>? // List transaksi asli
)