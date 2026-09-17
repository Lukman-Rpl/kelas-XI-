package com.example.sakubijak

import com.google.gson.annotations.SerializedName


data class BudgetItem(
    val categoryName: String = "Total Anggaran Dompet",
    val monthYear: String,
    val usedAmount: Long,
    val totalAmount: Long
)

// -------------------------------------------------------------
// TAMBAHKAN CLASS INI DENGAN ANOTASI SERIALIZEDNAME YANG SESUAI
// -------------------------------------------------------------
data class BudgetResponse(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("category_id") val categoryId: Int? = null,
    @SerializedName("category_name") val categoryName: String? = null,
    @SerializedName("limit_amount") val limitAmount: Long? = null,
    @SerializedName("used_amount") val usedAmount: Long? = null,
    @SerializedName("period") val period: String? = null
)

// Extension function untuk mengonversi BudgetResponse ke BudgetItem
fun BudgetResponse.toBudgetItem(defaultMonthYear: String): BudgetItem {
    return BudgetItem(
        categoryName = this.categoryName ?: "Tanpa Kategori",
        monthYear = this.period ?: defaultMonthYear,
        usedAmount = this.usedAmount ?: 0L,
        totalAmount = this.limitAmount ?: 0L
    )
}

data class TransactionUpdateResponse(
    val status: String,
    val message: String,
    @SerializedName("budget_summary") val budgetSummary: BudgetSummaryResponse?
)

data class BudgetSummaryResponse(
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("limit_amount") val limitAmount: Long,
    @SerializedName("total_spent") val totalSpent: Long
)

// Extension function untuk mengonversi response API ke BudgetItem
fun BudgetSummaryResponse.toBudgetItem(monthYear: String): BudgetItem {
    return BudgetItem(
        categoryName = this.categoryName,
        monthYear = monthYear,
        usedAmount = this.totalSpent,
        totalAmount = this.limitAmount
    )
}