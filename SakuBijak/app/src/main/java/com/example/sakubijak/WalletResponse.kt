package com.example.sakubijak

import com.google.gson.annotations.SerializedName

// Wrapper Response API dari Laravel
data class BaseApiResponse<T>(
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T
)

data class WalletGroupData(
    @SerializedName("all")
    val all: List<WalletResponse> = emptyList(),

    @SerializedName("personal")
    val personal: List<WalletResponse> = emptyList(),

    @SerializedName("group")
    val group: List<WalletResponse> = emptyList()
)

// Model Utama WalletResponse
data class WalletResponse(
    @SerializedName("id")
    val id: Long,

    // --- TAMBAHAN BARU: Nomor Unik Wallet ---
    @SerializedName("no_wallet")
    val noWallet: String? = null,
    // ----------------------------------------

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String? = "personal",

    @SerializedName("group_id")
    val groupId: Long? = null,

    @SerializedName("user_id")
    val userId: Long? = null,

    @SerializedName("balance")
    val balance: Double? = 0.0,

    // --- Tambahan untuk Response Transfer & Identitas Pemilik ---
    @SerializedName("current_balance")
    val currentBalance: Double? = null,

    @SerializedName("owner_id")
    val ownerId: Long? = null,

    @SerializedName("owner_name")
    val ownerName: String? = null,

    @SerializedName("display_label")
    val displayLabel: String? = null,
    // ------------------------------------------------------------

    @SerializedName("budget_limit")
    val budgetLimit: Double? = 0.0,

    @SerializedName("reset_type")
    val resetType: String? = "monthly",

    @SerializedName("is_active")
    val isActive: Boolean = false,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null
) {
    // Fungsi pintar membaca saldo (mengutamakan 'current_balance' jika dari response transfer)
    fun getSafeBalance(): Double = currentBalance ?: balance ?: 0.0

    fun getSafeBudgetLimit(): Double = budgetLimit ?: 0.0

    // Helper untuk mengambil nomor wallet dengan fallback
    fun getSafeNoWallet(): String = noWallet ?: "-"

    // Penyesuaian Helper Label Dropdown/List
    fun getFormattedLabel(): String {
        if (!displayLabel.isNullOrEmpty()) return displayLabel

        val walletNumberInfo = if (!noWallet.isNullOrEmpty()) "($noWallet)" else ""

        val ownerInfo = when {
            ownerId != null && !ownerName.isNullOrEmpty() -> "$ownerName"
            groupId != null -> "Grup"
            else -> ""
        }

        return buildString {
            append(name)
            if (walletNumberInfo.isNotEmpty()) append(" $walletNumberInfo")
            if (ownerInfo.isNotEmpty()) append(" - $ownerInfo")
        }
    }
}