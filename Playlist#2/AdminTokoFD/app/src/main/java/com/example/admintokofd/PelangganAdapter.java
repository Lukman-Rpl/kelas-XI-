package com.example.admintokofd;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admintokofd.model.Pelanggan;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PelangganAdapter extends RecyclerView.Adapter<PelangganAdapter.PelangganViewHolder> {

    private List<Pelanggan> pelangganList;
    private List<String> uidList;
    private Context context;
    private DatabaseReference pelangganRef;

    public PelangganAdapter(Context context, List<Pelanggan> pelangganList, List<String> uidList) {
        this.context = context;
        this.pelangganList = pelangganList;
        this.uidList = uidList;
        pelangganRef = FirebaseDatabase.getInstance().getReference("pelanggan");
    }

    @NonNull
    @Override
    public PelangganViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pelanggan, parent, false);
        return new PelangganViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PelangganViewHolder holder, int position) {
        Pelanggan pelanggan = pelangganList.get(position);
        String uid = uidList.get(position);

        holder.tvUsername.setText(pelanggan.getUsername());
        holder.tvEmail.setText(pelanggan.getAccount());

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Akun")
                    .setMessage("Apakah Anda yakin ingin menghapus akun " + pelanggan.getUsername() + "?")
                    .setPositiveButton("Ya", (dialog, which) -> hapusAkun(uid, position))
                    .setNegativeButton("Tidak", null)
                    .show();
        });
    }

    private void hapusAkun(String uid, int position) {
        pelangganRef.child(uid).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Akun berhasil dihapus", Toast.LENGTH_SHORT).show();
                    pelangganList.remove(position);
                    uidList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Gagal menghapus akun: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return pelangganList.size();
    }

    static class PelangganViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail;
        Button btnDelete;

        public PelangganViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
