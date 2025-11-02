package com.example.firebasetutorial;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    // Konstanta untuk notifikasi channel
    public static final String CHANNEL_ID = "dummy_channel";

    // Firebase Analytics
    private FirebaseAnalytics mFirebaseAnalytics;

    // Notification Manager
    private NotificationManager notificationManager;

    // Launcher untuk permission notifikasi (Android 13+)
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inisialisasi Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Inisialisasi NotificationManager
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Inisialisasi launcher untuk permission notifikasi
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Log.d("PERMISSION", "Izin notifikasi diberikan.");
                    } else {
                        Log.w("PERMISSION", "Izin notifikasi ditolak.");
                    }
                });

        // Tambahkan tombol untuk uji crash (Crashlytics)
        Button crashButton = new Button(this);
        crashButton.setText("Test Crash");
        crashButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                throw new RuntimeException("Test Crash"); // Memicu crash
            }
        });

        addContentView(crashButton, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // UI layout responsif
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Eksekusi
        createNotificationChannel(); // Android 8+
        requestPermission();         // Android 13+
        createToken();               // Ambil token FCM
    }

    // Minta izin notifikasi untuk Android 13+
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Log.d("PERMISSION", "Izin notifikasi sudah diberikan.");
            }
        }
    }

    // Buat Notification Channel untuk Android 8+
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Default Channel";
            String description = "Channel untuk notifikasi default";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    // Ambil token FCM
    private void createToken() {
        final String TAG = "FCM_PESAN";

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Token berhasil diperoleh
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);

                        // Kirim token ke server kamu di sini jika perlu
                    }
                });
    }
}
