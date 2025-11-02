package com.example.tokofd;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokofd.model.OrderItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView profileFullName, profileAccount;
    private RecyclerView orderRecyclerView;
    private Button filterDateButton;

    private OrderAdapter orderAdapter;
    private List<OrderItem> fullOrderList = new ArrayList<>();
    private List<OrderItem> filteredOrderList = new ArrayList<>();

    private String currentUserId;
    private DatabaseReference pelangganRef, ordersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileFullName = view.findViewById(R.id.profileFullName);
        profileAccount = view.findViewById(R.id.profileAccount);
        orderRecyclerView = view.findViewById(R.id.orderRecyclerView);
        filterDateButton = view.findViewById(R.id.filterDateButton);

        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(filteredOrderList);
        orderRecyclerView.setAdapter(orderAdapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        pelangganRef = FirebaseDatabase.getInstance().getReference("pelanggan");
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        loadProfileInfo();
        loadOrders();

        filterDateButton.setOnClickListener(v -> showDatePicker());

        return view;
    }

    private void loadProfileInfo() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        pelangganRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String username = snapshot.child("username").getValue(String.class);

                profileFullName.setText(username != null ? username : "User");
                profileAccount.setText(email != null ? email : "email@contoh.com");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

                        filteredOrderList.clear();
                        filteredOrderList.addAll(fullOrderList);
                        orderAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error if needed
                    }
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
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
