package com.example.admintokofd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.example.admintokofd.model.Produk;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProdukActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 101;

    private RecyclerView recyclerView;
    private ProdukAdapter adapter;
    private List<Produk> produkList = new ArrayList<>();
    private List<String> produkIdList = new ArrayList<>();
    private DatabaseReference produkRef;

    private EditText etNama, etHarga, etStok;
    private Button btnSimpan, btnPilihGambar;
    private ImageView ivPreview;

    private Uri imageUri = null;
    private String base64Image = null; // string base64 gambar
    private String selectedProdukId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_form);

        recyclerView = findViewById(R.id.recyclerViewProduk);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        etNama = findViewById(R.id.etNamaProduk);
        etHarga = findViewById(R.id.etHarga);
        etStok = findViewById(R.id.etStok);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnPilihGambar = findViewById(R.id.btnPilihGambar);
        ivPreview = findViewById(R.id.ivPreview);

        produkRef = FirebaseDatabase.getInstance().getReference("produk");

        adapter = new ProdukAdapter();
        recyclerView.setAdapter(adapter);

        loadData();

        btnPilihGambar.setOnClickListener(v -> pilihGambar());

        btnSimpan.setOnClickListener(v -> simpanAtauUpdateProduk());
    }

    private void pilihGambar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivPreview.setImageBitmap(bitmap);
                base64Image = encodeImageToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        // Resize dulu kalau perlu supaya tidak terlalu besar (optional)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void simpanAtauUpdateProduk() {
        String nama = etNama.getText().toString().trim();
        String hargaStr = etHarga.getText().toString().trim();
        String stokStr = etStok.getText().toString().trim();

        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(hargaStr) || TextUtils.isEmpty(stokStr)) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (base64Image == null && selectedProdukId == null) {
            Toast.makeText(this, "Pilih gambar produk terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        int harga = Integer.parseInt(hargaStr);
        int stok = Integer.parseInt(stokStr);
        String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (selectedProdukId == null) {
            // Tambah produk baru
            String id = produkRef.push().getKey();
            Produk produk = new Produk(nama, stok, harga, tanggal, base64Image);
            produkRef.child(id).setValue(produk)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        clearForm();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal tambah produk: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Update produk lama
            updateProduk(selectedProdukId, nama, harga, stok, tanggal, base64Image);
        }
    }

    private void updateProduk(String id, String nama, int harga, int stok, String tanggal, String gambarBase64) {
        DatabaseReference ref = produkRef.child(id);
        ref.child("nama_produk").setValue(nama);
        ref.child("harga").setValue(harga);
        ref.child("stok").setValue(stok);
        ref.child("tanggal").setValue(tanggal);

        if (gambarBase64 != null) {
            ref.child("gambar").setValue(gambarBase64);
        }

        Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show();
        clearForm();
        selectedProdukId = null;
        base64Image = null;
        imageUri = null;
    }

    private void clearForm() {
        etNama.setText("");
        etHarga.setText("");
        etStok.setText("");
        ivPreview.setImageResource(android.R.color.transparent);
        base64Image = null;
        imageUri = null;
    }

    private void loadData() {
        produkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produkList.clear();
                produkIdList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Produk p = snap.getValue(Produk.class);
                    produkList.add(p);
                    produkIdList.add(snap.getKey());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProdukActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ProdukActivity.this).inflate(R.layout.item_product, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Produk p = produkList.get(position);
            String id = produkIdList.get(position);

            holder.tvNama.setText(p.getNama_produk());
            holder.tvHarga.setText("Rp " + p.getHarga());
            holder.tvStok.setText("Stok: " + p.getStok());

            if (p.getGambar() != null && !p.getGambar().isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(p.getGambar(), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    holder.ivGambar.setImageBitmap(bitmap);
                } catch (IllegalArgumentException e) {
                    holder.ivGambar.setImageResource(R.drawable.ic_placeholder_image);
                }
            } else {
                holder.ivGambar.setImageResource(R.drawable.ic_placeholder_image);
            }

            holder.btnEdit.setOnClickListener(v -> {
                etNama.setText(p.getNama_produk());
                etHarga.setText(String.valueOf(p.getHarga()));
                etStok.setText(String.valueOf(p.getStok()));
                selectedProdukId = id;
                base64Image = p.getGambar();

                if (p.getGambar() != null && !p.getGambar().isEmpty()) {
                    try {
                        byte[] decodedBytes = Base64.decode(p.getGambar(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        ivPreview.setImageBitmap(bitmap);
                    } catch (IllegalArgumentException e) {
                        ivPreview.setImageResource(R.drawable.ic_placeholder_image);
                    }
                } else {
                    ivPreview.setImageResource(R.drawable.ic_placeholder_image);
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(ProdukActivity.this)
                        .setTitle("Hapus Produk")
                        .setMessage("Yakin ingin menghapus produk ini?")
                        .setPositiveButton("Ya", (dialog, which) -> {
                            produkRef.child(id).removeValue();
                            produkList.remove(position);
                            produkIdList.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(ProdukActivity.this, "Produk dihapus", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return produkList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tvNama, tvHarga, tvStok;
            Button btnEdit, btnDelete;
            ImageView ivGambar;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNama = itemView.findViewById(R.id.tvNamaProduk);
                tvHarga = itemView.findViewById(R.id.tvHargaProduk);
                tvStok = itemView.findViewById(R.id.tvStokProduk);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                ivGambar = itemView.findViewById(R.id.ivGambarProduk);
            }
        }
    }
}
