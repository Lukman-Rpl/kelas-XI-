package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class ReportResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: ReportData
)

data class ReportData(
    @SerializedName("period")
    val period: ReportPeriod,

    @SerializedName("summary")
    val summary: ReportSummary,

    @SerializedName("categories")
    val categories: List<CategoryReportItem>
)

data class ReportPeriod(
    @SerializedName("label")
    val label: String // Contoh: "Juli 2026", "27 Jul - 02 Agu 2026", atau "29 Juli 2026"
)

data class ReportSummary(
    @SerializedName("total_expense")
    val totalExpense: Long,

    @SerializedName("total_expense_formatted")
    val totalExpenseFormatted: String, // Contoh: "Rp 2.750.000"

    @SerializedName("total_income")
    val totalIncome: Long,

    @SerializedName("total_income_formatted")
    val totalIncomeFormatted: String, // Contoh: "Rp 5.000.000"

    @SerializedName("net_cashflow")
    val netCashflow: Long,

    @SerializedName("net_cashflow_formatted")
    val netCashflowFormatted: String
)

data class CategoryReportItem(
    @SerializedName("category_id")
    val categoryId: Long,

    @SerializedName("name")
    val name: String, // Contoh: "Makanan & Minuman"

    @SerializedName("color_hex")
    val colorHex: String?, // Contoh: "#2196F3"

    @SerializedName("amount")
    val amount: Long,

    @SerializedName("amount_formatted")
    val amountFormatted: String, // Contoh: "Rp 1.500.000"

    @SerializedName("percentage")
    val percentage: Int, // Contoh: 55

    @SerializedName("percentage_label")
    val percentageLabel: String // Contoh: "55%"
)