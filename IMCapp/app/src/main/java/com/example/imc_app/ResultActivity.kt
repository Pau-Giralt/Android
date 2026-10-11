package com.example.imc_app

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale
import android.widget.Button
import android.widget.ImageButton

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.resultMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        
       
        val height = intent.getIntExtra("HEIGHT", 0)
        val weight = intent.getIntExtra("WEIGHT", 0)

  
        val bmi = if (height > 0) 703.0 * weight / (height * height) else 0.0
        
        val categoryRes: Int
        val descRes: Int
        val colorRes: Int
        when {
            bmi < 18.5 -> {
                categoryRes = R.string.bmi_underweight
                descRes = R.string.desc_underweight
                colorRes = R.color.naranja
            }
            bmi < 25.0 -> {
                categoryRes = R.string.bmi_normal
                descRes = R.string.desc_normal
                colorRes = R.color.verde
            }
            bmi < 30.0 -> {
                categoryRes = R.string.bmi_overweight
                descRes = R.string.desc_overweight
                colorRes = R.color.naranja
            }
            else -> {
                categoryRes = R.string.bmi_obese
                descRes = R.string.desc_obese
                colorRes = R.color.rojo
            }
        }

        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        tvCategory.text = getString(categoryRes)
        tvCategory.setTextColor(ContextCompat.getColor(this, colorRes))
        findViewById<TextView>(R.id.tvBmi).text = String.format(Locale.US, "%.2f", bmi)
        findViewById<TextView>(R.id.tvDescription).text = getString(descRes)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnRecalculate).setOnClickListener { finish() }
    }
}
