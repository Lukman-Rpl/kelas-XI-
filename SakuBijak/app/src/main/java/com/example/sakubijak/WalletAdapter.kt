package com.example.sakubijak

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sakubijak.databinding.ItemWalletBinding
import java.text.NumberFormat
import java.util.Locale

class WalletAdapter(
    private var walletList: MutableList<WalletResponse>,
    private var activeWalletId: Long,
    private val onItemClick: (WalletResponse) -> Unit,
    private val onOptionClick: ((WalletResponse, View) -> Unit)? = null
) : RecyclerView.Adapter<WalletAdapter.WalletViewHolder>() {

    private val rupiahFormatter: NumberFormat by lazy {
        NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
            maximumFractionDigits = 0
        }
    }

    inner class WalletViewHolder(val binding: ItemWalletBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val binding = ItemWalletBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WalletViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        val wallet = walletList[position]
        val context = holder.itemView.context

        with(holder.binding) {
            // 1. Set Nama Dompet
            tvWalletName.text = wallet.name

            // 2. Set Nomor Dompet Unik & Fitur Tap to Copy
            val noWalletText = wallet.noWallet ?: "-"
            tvWalletNo.text = noWalletText

            // Pengguna bisa menyalin nomor dompet dengan sekali tekan
            tvWalletNo.setOnClickListener {
                if (!wallet.noWallet.isNullOrEmpty()) {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Nomor Wallet", wallet.noWallet)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Nomor wallet tersalin: ${wallet.noWallet}", Toast.LENGTH_SHORT).show()
                }
            }

            // 3. Tampilkan Tipe Dompet (Personal / Group)
            val isGroup = wallet.type?.equals("group", ignoreCase = true) == true
            tvWalletType.text = if (isGroup) "Dompet Grup" else "Dompet Personal"

            // 4. Format Saldo ke Rupiah
            tvWalletBalance.text = rupiahFormatter.format(wallet.getSafeBalance())

            // 5. Cek Indikator Dompet Aktif
            ivActiveStatus.visibility = if (wallet.id == activeWalletId) View.VISIBLE else View.GONE

            // 6. Tombol Titik Tiga (Options Menu)
            if (onOptionClick != null) {
                btnWalletOptions.visibility = View.VISIBLE
                btnWalletOptions.setOnClickListener { view ->
                    onOptionClick.invoke(wallet, view)
                }
            } else {
                btnWalletOptions.visibility = View.GONE
            }

            // 7. Event Klik Utama Item Card
            root.setOnClickListener {
                onItemClick(wallet)
            }
        }
    }

    override fun getItemCount(): Int = walletList.size

    fun updateData(newList: List<WalletResponse>, newActiveId: Long) {
        this.walletList = newList.toMutableList()
        this.activeWalletId = newActiveId
        notifyDataSetChanged()
    }
}