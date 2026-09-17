package com.example.sakubijak

import com.google.gson.annotations.SerializedName

data class TransferResponse(
    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: TransferResultData? = null
)

data class TransferResultData(
    @SerializedName("from_wallet")
    val fromWallet: WalletResponse? = null,

    @SerializedName("to_wallet")
    val toWallet: WalletResponse? = null
)