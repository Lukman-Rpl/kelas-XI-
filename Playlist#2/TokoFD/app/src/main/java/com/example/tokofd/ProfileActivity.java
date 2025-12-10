package com.example.tokofd;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokofd.model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileFullName, profileAccount;
    private RecyclerView orderRecyclerView;
    private Button filterDateButton;

    private OrderAdapter orderAdapter;
    private List<OrderItem> fullOrderList = new ArrayList<>();
    private List<OrderItem> filteredOrderList = new ArrayList<>();

    private String currentUserId;
    private DatabaseReference pelangganRef, ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        profileFullName = findViewById(R.id.profileFullName);
        profileAccount = findViewById(R.id.profileAccount);
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        filterDateButton = findViewById(R.id.filterDateButton);

        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(filteredOrderList); // hanya tampilkan hasil filter
        orderRecyclerView.setAdapter(orderAdapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        pelangganRef = FirebaseDatabase.getInstance().getReference("pelanggan");
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        loadProfileInfo();
        loadOrders();

        filterDateButton.setOnClickListener(v -> showDatePicker());
    }

    private void loadProfileInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            profileFullName.setText("User tidak login");
            profileAccount.setText("-");
            return;
        }

        String uid = user.getUid();
        String email = user.getEmail();

        pelangganRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);

                Log.d("PROFILE_DEBUG", "Snapshot: " + snapshot.toString());
                Log.d("PROFILE_DEBUG", "Username: " + username);
                Log.d("PROFILE_DEBUG", "Email: " + email);

                profileFullName.setText(username != null ? username : "User");
                profileAccount.setText(email != null ? email : "email@contoh.com");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("PROFILE_DEBUG", "Database error: " + error.getMessage());
                profileFullName.setText("User");
                profileAccount.setText(email != null ? email : "email@contoh.com");
            }
        });
    }


    private void loadOrders() {
        ordersRef.orderByChild("user_id").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        fullOrderList.clear();
                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            OrderItem item = orderSnap.getValue(OrderItem.class);
                            fullOrderList.add(item);
                        }

                        // Secara default, tampilkan semua
                        filteredOrderList.clear();
                        filteredOrderList.addAll(fullOrderList);
                        orderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Optional: handle error
                    }
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                ProfileActivity.this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String selectedDate = sdf.format(calendar.getTime());

                    filterOrdersByDate(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void filterOrdersByDate(String selectedDate) {
        filteredOrderList.clear();
        for (OrderItem item : fullOrderList) {
            if (item.getTanggal() != null && item.getTanggal().equals(selectedDate)) {
                filteredOrderList.add(item);
            }
        }

        orderAdapter.notifyDataSetChanged();
    }
}
