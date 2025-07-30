package com.example.recyclerviewcardview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SiswaAdapter extends RecyclerView.Adapter<SiswaAdapter.ViewHolder> {
    private Context context;
    private List<Siswa> siswalist;

    public SiswaAdapter(Context context, List<Siswa> siswalist) {
        this.context = context;
        this.siswalist = siswalist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_siswa, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Siswa siswa = siswalist.get(position);
        holder.tvNama.setText(siswa.getNama());
        holder.tvAlamat.setText(siswa.getAlamat());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "Nama: " + siswa.getNama() + ", Alamat: " + siswa.getAlamat(), Toast.LENGTH_SHORT).show();
//            }
//        });

        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu= new PopupMenu(context,holder.tvMenu);
                popupMenu.inflate(R.menu.menu_option);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.menu_s;
                                Toast.makeText(context,"simpan"+siswa.getNama(),Toast.LENGTH_SHORT).show();
                                break;

                             case R.id.menu_h;
                             siswalist.remove(1);
                             notifyDataSetChanged();
                             Toast.makeText(context,siswa.getNama()+"terhapus", Toast.LENGTH_SHORT).show();
                             break;
                        }
                    }
                });

                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return siswalist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvAlamat,tvMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}
