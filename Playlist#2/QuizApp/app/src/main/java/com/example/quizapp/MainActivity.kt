package com.example.quizapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSpin = findViewById<MaterialButton>(R.id.btnSpinWheel)

        btnSpin.setOnClickListener {
            Toast.makeText(this, "Spin feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}
