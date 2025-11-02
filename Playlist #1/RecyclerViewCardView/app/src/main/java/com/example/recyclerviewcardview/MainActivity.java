package com.example.recyclerviewcardview;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SiswaAdapter adapter;
    List<Siswa> siswaList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        load();
        isiData();
    }



    private void load() {
        recyclerView =findViewById(R.id.rcvsiswa);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
    }

    private void isiData() {
        siswaList=new ArrayList<Siswa>();
        siswaList.add(new Siswa("jono","Darjo"));
        siswaList.add(new Siswa("joni","Darjo"));
        siswaList.add(new Siswa("jona","Darjo"));
        siswaList.add(new Siswa("jonko","Darjo"));
        siswaList.add(new Siswa("jonma","Darjo"));
        siswaList.add(new Siswa("jonka","Darjo"));
        siswaList.add(new Siswa("jonu","Darjo"));
        siswaList.add(new Siswa("johu","Darjo"));
        siswaList.add(new Siswa("joshua","Darjo"));
        siswaList.add(new Siswa("jamban","Darjo"));
        siswaList.add(new Siswa("jjan","Darjo"));
        siswaList.add(new Siswa("jjusa","Darjo"));
        siswaList.add(new Siswa("jaka","Darjo"));
        siswaList.add(new Siswa("jaka","Darjo"));
        siswaList.add(new Siswa("jaka","Darjo"));
        siswaList.add(new Siswa("jaka","Darjo"));
        siswaList.add(new Siswa("jaka","Darjo"));
        siswaList.add(new Siswa("jaka","Darjo"));
        siswaList.add(new Siswa("jaka","Darjo"));
        siswaList.add(new Siswa("jaka","Darjo"));

        adapter= new SiswaAdapter(this,siswaList);
        recyclerView.setAdapter(adapter);
    }

    public void btnPlus(View view) {
        siswaList.add(new Siswa("yono","kalimanatan"));
        adapter.notifyDataSetChanged();
    }
}