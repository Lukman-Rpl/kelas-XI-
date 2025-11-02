package com.example.tokofd;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notificationRecyclerView;
    private ImageButton notificationButton;
    private TextView textNotificationsTitle;

    private NotificationAdapter notificationAdapter;
    private List<String> notificationMessages = new ArrayList<>();

    private DatabaseReference notificationRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notifications);

        notificationRecyclerView = findViewById(R.id.notificationRecyclerView);
        notificationButton = findViewById(R.id.notificationButton);
        textNotificationsTitle = findViewById(R.id.textNotificationsTitle);

        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(notificationMessages);
        notificationRecyclerView.setAdapter(notificationAdapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId);

        loadNotifications();

        notificationButton.setOnClickListener(v ->
                Toast.makeText(this, "Notifikasi diperbarui", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadNotifications() {
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationMessages.clear();
                for (DataSnapshot notifSnap : snapshot.getChildren()) {
                    String pesan = notifSnap.child("pesan").getValue(String.class);
                    String tanggal = notifSnap.child("tanggal").getValue(String.class);
                    if (pesan != null && tanggal != null) {
                        notificationMessages.add(pesan + " (" + tanggal + ")");
                    }
                }
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NotificationsActivity.this, "Gagal memuat notifikasi", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
