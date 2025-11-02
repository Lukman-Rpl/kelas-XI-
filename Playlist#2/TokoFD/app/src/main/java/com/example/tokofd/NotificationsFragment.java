package com.example.tokofd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView notificationRecyclerView;
    private ImageButton notificationButton;
    private TextView textNotificationsTitle;

    private NotificationAdapter notificationAdapter;
    private List<String> dummyNotifications = new ArrayList<>();

    public NotificationsFragment() {
        // Konstruktor kosong
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // Inisialisasi view
        notificationRecyclerView = view.findViewById(R.id.notificationRecyclerView);
        notificationButton = view.findViewById(R.id.notificationButton);
        textNotificationsTitle = view.findViewById(R.id.textNotificationsTitle);

        // Dummy data notifikasi
        dummyNotifications.add("Pesanan kamu sedang diproses");
        dummyNotifications.add("Promo Hari Ini: Diskon 20%");
        dummyNotifications.add("Item baru telah ditambahkan");

        // Setup RecyclerView
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(dummyNotifications);
        notificationRecyclerView.setAdapter(notificationAdapter);

        // Aksi tombol notifikasi (hanya contoh)
        notificationButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Notifikasi terbaru", Toast.LENGTH_SHORT).show()
        );

        return view;
    }
}
