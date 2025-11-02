package com.example.cgpa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etGrade1 = findViewById<EditText>(R.id.etGrade1)
        val etGrade2 = findViewById<EditText>(R.id.etGrade2)
        val etGrade3 = findViewById<EditText>(R.id.etGrade3)
        val etGrade4 = findViewById<EditText>(R.id.etGrade4)

        val etCredit1 = findViewById<EditText>(R.id.etCredit1)
        val etCredit2 = findViewById<EditText>(R.id.etCredit2)
        val etCredit3 = findViewById<EditText>(R.id.etCredit3)
        val etCredit4 = findViewById<EditText>(R.id.etCredit4)

        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnCalculate.setOnClickListener {
            val grades = listOf(
                etGrade1.text.toString(),
                etGrade2.text.toString(),
                etGrade3.text.toString(),
                etGrade4.text.toString()
            )
            val credits = listOf(
                etCredit1.text.toString(),
                etCredit2.text.toString(),
                etCredit3.text.toString(),
                etCredit4.text.toString()
            )

            val cgpa = calculateCGPA(grades, credits)
            tvResult.text = "Your CGPA: %.2f".format(cgpa)
        }
    }

    private fun calculateCGPA(grades: List<String>, credits: List<String>): Double {
        val gradeMap = mapOf(
            "A" to 4.0,
            "B" to 3.0,
            "C" to 2.0,
            "D" to 1.0
        )

        var totalPoints = 0.0
        var totalCredits = 0.0

        for (i in grades.indices) {
            val gradeValue = gradeMap[grades[i].uppercase()] ?: 0.0
            val creditValue = credits[i].toDoubleOrNull() ?: 0.0
            totalPoints += gradeValue * creditValue
            totalCredits += creditValue
        }

        return if (totalCredits > 0) totalPoints / totalCredits else 0.0
    }
}
